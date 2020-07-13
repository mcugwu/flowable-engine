/* Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.flowable.engine.impl.bpmn.http;

import static org.flowable.bpmn.model.ImplementationType.IMPLEMENTATION_TYPE_CLASS;
import static org.flowable.bpmn.model.ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION;
import static org.flowable.common.engine.impl.util.ExceptionUtil.sneakyThrow;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import org.apache.commons.lang3.StringUtils;
import org.flowable.bpmn.model.FieldExtension;
import org.flowable.bpmn.model.FlowableHttpRequestHandler;
import org.flowable.bpmn.model.FlowableHttpResponseHandler;
import org.flowable.bpmn.model.HttpServiceTask;
import org.flowable.bpmn.model.ImplementationType;
import org.flowable.common.engine.api.FlowableException;
import org.flowable.common.engine.api.async.AsyncTaskInvoker;
import org.flowable.common.engine.api.delegate.Expression;
import org.flowable.common.engine.api.variable.VariableContainer;
import org.flowable.engine.cfg.HttpClientConfig;
import org.flowable.engine.delegate.BpmnError;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.FutureJavaDelegate;
import org.flowable.engine.impl.bpmn.helper.ErrorPropagation;
import org.flowable.engine.impl.bpmn.http.handler.ClassDelegateHttpHandler;
import org.flowable.engine.impl.bpmn.http.handler.DelegateExpressionHttpHandler;
import org.flowable.engine.impl.bpmn.parser.FieldDeclaration;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.el.FixedValue;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.http.common.api.HttpRequest;
import org.flowable.http.common.api.HttpResponse;
import org.flowable.http.common.api.client.AsyncExecutableHttpRequest;
import org.flowable.http.common.api.client.ExecutableHttpRequest;
import org.flowable.http.common.api.client.FlowableHttpClient;
import org.flowable.http.common.api.delegate.HttpRequestHandler;
import org.flowable.http.common.api.delegate.HttpResponseHandler;
import org.flowable.http.common.impl.BaseHttpActivityDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Filip Hrisafov
 * @author Joram Barrez
 */
public class DefaultBpmnHttpActivityDelegate extends BaseHttpActivityDelegate implements FutureJavaDelegate<DefaultBpmnHttpActivityDelegate.ExecutionData> {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultBpmnHttpActivityDelegate.class);

    protected FlowableHttpClient httpClient;

    public DefaultBpmnHttpActivityDelegate() {
        this(null);
    }

    public DefaultBpmnHttpActivityDelegate(FlowableHttpClient httpClient) {
        this.httpClient = httpClient == null ? createHttpClient() : httpClient;
    }

    protected FlowableHttpClient createHttpClient() {
        HttpClientConfig config = CommandContextUtil.getProcessEngineConfiguration().getHttpClientConfig();
        return config.determineHttpClient();
    }

    @Override
    public CompletableFuture<ExecutionData> execute(DelegateExecution execution, AsyncTaskInvoker taskInvoker) {
        HttpRequest request;

        HttpServiceTask httpServiceTask = (HttpServiceTask) execution.getCurrentFlowElement();
        try {
            request = createRequest(execution, httpServiceTask.getId());

        } catch (Exception e) {
            if (e instanceof FlowableException) {
                throw (FlowableException) e;
            } else {
                throw new FlowableException(HTTP_TASK_REQUEST_FIELD_INVALID + " in execution " + execution.getId(), e);
            }
        }

        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
        HttpRequestHandler httpRequestHandler = createHttpRequestHandler(httpServiceTask.getHttpRequestHandler(), processEngineConfiguration);

        if (httpRequestHandler != null) {
            httpRequestHandler.handleHttpRequest(execution, request, null);
        }

        // Validate request
        validateRequest(request);

        boolean parallelInSameTransaction;
        if (httpServiceTask.getParallelInSameTransaction() != null) {
            parallelInSameTransaction = httpServiceTask.getParallelInSameTransaction();
        } else {
            parallelInSameTransaction = processEngineConfiguration.getHttpClientConfig().isDefaultParallelInSameTransaction();
        }

        // Prepare request
        ExecutableHttpRequest httpRequest = httpClient.prepareRequest(request);

        if (!parallelInSameTransaction) {
            CompletableFuture<ExecutionData> future = new CompletableFuture<>();

            try {
                HttpResponse response = httpRequest.call();
                future.complete(new ExecutionData(request, response));
            } catch (Exception ex) {
                future.complete(new ExecutionData(request, null, ex));
            }
            return future;
        } else if (httpRequest instanceof AsyncExecutableHttpRequest) {

            return ((AsyncExecutableHttpRequest) httpRequest).callAsync()
                    .handle((response, throwable) -> new ExecutionData(request, response, throwable));
        }
        return taskInvoker.submit(() -> {
            try {
                return new ExecutionData(request, httpRequest.call());
            } catch (Exception ex) {
                return new ExecutionData(request, null, ex);
            }
        });

    }

    @Override
    public void afterExecution(DelegateExecution execution, ExecutionData result) {

        HttpRequest request = result.request;
        HttpResponse response = result.response;

        if (result.exception != null) {
            if (request.isIgnoreErrors()) {
                LOGGER.info("Error ignored while processing http task in execution {}", execution.getId(), result.exception);
                execution.setVariable(request.getPrefix() + "ErrorMessage", result.exception.getMessage());
                return;
            }

            sneakyThrow(result.exception);
        }

        ProcessEngineConfigurationImpl processEngineConfiguration = CommandContextUtil.getProcessEngineConfiguration();
        HttpServiceTask httpServiceTask = (HttpServiceTask) execution.getCurrentFlowElement();

        try {
            // Pass request through response handler
            HttpResponseHandler httpResponseHandler = createHttpResponseHandler(httpServiceTask.getHttpResponseHandler(), processEngineConfiguration);

            if (httpResponseHandler != null) {
                httpResponseHandler.handleHttpResponse(execution, response);
            }

            // Save response fields in the execution
            saveResponseFields(execution, request, response, processEngineConfiguration.getObjectMapper());
        } catch (BpmnError e) {
            // Rethrow BPMN error so it can be propagated
            throw e;
        } catch (Exception ex) {
            if (request.isIgnoreErrors()) {
                LOGGER.info("Error ignored while processing http task in execution {}", execution.getId(), ex);
                execution.setVariable(request.getPrefix() + "ErrorMessage", ex.getMessage());
            } else {
                sneakyThrow(ex);
            }
        }
    }

    protected HttpRequestHandler createHttpRequestHandler(FlowableHttpRequestHandler handler, ProcessEngineConfigurationImpl processEngineConfiguration) {
        HttpRequestHandler requestHandler = null;

        if (handler != null) {
            if (IMPLEMENTATION_TYPE_CLASS.equalsIgnoreCase(handler.getImplementationType())) {
                requestHandler = new ClassDelegateHttpHandler(handler.getImplementation(),
                        createFieldDeclarations(handler.getFieldExtensions(), processEngineConfiguration));

            } else if (IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equalsIgnoreCase(handler.getImplementationType())) {
                requestHandler = new DelegateExpressionHttpHandler(
                        processEngineConfiguration.getExpressionManager().createExpression(handler.getImplementation()),
                        createFieldDeclarations(handler.getFieldExtensions(), processEngineConfiguration));
            }
        }
        return requestHandler;
    }

    protected HttpResponseHandler createHttpResponseHandler(FlowableHttpResponseHandler handler, ProcessEngineConfigurationImpl processEngineConfiguration) {
        HttpResponseHandler responseHandler = null;

        if (handler != null) {
            if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equalsIgnoreCase(handler.getImplementationType())) {
                responseHandler = new ClassDelegateHttpHandler(handler.getImplementation(),
                        createFieldDeclarations(handler.getFieldExtensions(), processEngineConfiguration));

            } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equalsIgnoreCase(handler.getImplementationType())) {
                responseHandler = new DelegateExpressionHttpHandler(
                        processEngineConfiguration.getExpressionManager().createExpression(handler.getImplementation()),
                        createFieldDeclarations(handler.getFieldExtensions(), processEngineConfiguration));
            }
        }
        return responseHandler;
    }

    protected List<FieldDeclaration> createFieldDeclarations(List<FieldExtension> fieldList, ProcessEngineConfigurationImpl processEngineConfiguration) {
        List<FieldDeclaration> fieldDeclarations = new ArrayList<>();

        for (FieldExtension fieldExtension : fieldList) {
            FieldDeclaration fieldDeclaration;
            if (StringUtils.isNotEmpty(fieldExtension.getExpression())) {
                fieldDeclaration = new FieldDeclaration(fieldExtension.getFieldName(), Expression.class.getName(),
                        processEngineConfiguration.getExpressionManager().createExpression(fieldExtension.getExpression()));
            } else {
                fieldDeclaration = new FieldDeclaration(fieldExtension.getFieldName(), Expression.class.getName(),
                        new FixedValue(fieldExtension.getStringValue()));
            }

            fieldDeclarations.add(fieldDeclaration);
        }
        return fieldDeclarations;
    }

    @Override
    protected void propagateError(VariableContainer container, String code) {
        ErrorPropagation.propagateError("HTTP" + code, (DelegateExecution) container);
    }

    protected static class ExecutionData {

        protected HttpRequest request;
        protected HttpResponse response;
        protected Throwable exception;

        public ExecutionData(HttpRequest request, HttpResponse response) {
            this(request, response, null);
        }

        public ExecutionData(HttpRequest request, HttpResponse response, Throwable exception) {
            this.request = request;
            this.response = response;
            this.exception = exception;
        }

        public HttpRequest getRequest() {
            return request;
        }

        public HttpResponse getResponse() {
            return response;
        }

        public Throwable getException() {
            return exception;
        }
    }
}

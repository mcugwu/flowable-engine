package org.flowable.http.cmmn.impl;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.DefaultHttpRequestRetryHandler;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.flowable.bpmn.model.MapExceptionEntry;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.impl.behavior.CoreCmmnActivityBehavior;
import org.flowable.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import org.flowable.cmmn.engine.impl.util.CommandContextUtil;
import org.flowable.cmmn.model.FlowableHttpRequestHandler;
import org.flowable.cmmn.model.FlowableHttpResponseHandler;
import org.flowable.cmmn.model.HttpServiceTask;
import org.flowable.cmmn.model.ImplementationType;
import org.flowable.engine.common.api.FlowableException;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.flowable.http.ExpressionUtils;
import org.flowable.http.HttpActivityExecutor;
import org.flowable.http.HttpRequest;
import org.flowable.http.NopErrorPropagator;
import org.flowable.http.cmmn.impl.handler.ClassDelegateHttpHandler;
import org.flowable.http.cmmn.impl.handler.DelegateExpressionHttpHandler;
import org.flowable.http.delegate.HttpRequestHandler;
import org.flowable.http.delegate.HttpResponseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;
import java.util.List;

import static org.flowable.http.ExpressionUtils.getStringSetFromField;
import static org.flowable.http.ExpressionUtils.parseBoolean;
import static org.flowable.http.HttpActivityExecutor.HTTP_TASK_REQUEST_FIELD_INVALID;

/**
 * This class provides http task for cmmn models
 *
 * @author martin.grofcik
 */
public class CmmnHttpActivityBehaviorImpl extends CoreCmmnActivityBehavior {

    private static final Logger LOGGER = LoggerFactory.getLogger(CmmnHttpActivityBehaviorImpl.class);

    // HttpRequest method (GET,POST,PUT etc)
    protected String requestMethod;
    // HttpRequest URL (http://flowable.org)
    protected String requestUrl;
    // Line separated HTTP body headers(Optional)
    protected String requestHeaders;
    // HttpRequest body expression (Optional)
    protected String requestBody;
    // Timeout in seconds for the body (Optional)
    protected String requestTimeout;
    // HttpRequest retry disable HTTP redirects (Optional)
    protected String disallowRedirects;
    // Comma separated list of HTTP body status codes to fail, for example 400,5XX (Optional)
    protected String failStatusCodes;
    // Comma separated list of HTTP body status codes to handle, for example 404,3XX (Optional)
    protected String handleStatusCodes;
    // Flag to ignore exceptions (Optional)
    protected String ignoreException;
    // Flag to save request variables. default is false (Optional)
    protected String saveRequestVariables;
    // Flag to save response variables. default is false (Optional)
    protected String saveResponseParameters;
    // Variable name for response body
    protected String responseVariableName;
    // Prefix for the execution variable names (Optional)
    protected String resultVariablePrefix;
    // Exception mapping
    protected List<MapExceptionEntry> mapExceptions;

    protected HttpServiceTask serviceTask;
    protected HttpActivityExecutor httpActivityExecutor;

    public CmmnHttpActivityBehaviorImpl() {
        org.flowable.cmmn.engine.HttpClientConfig config = CommandContextUtil.getCmmnEngineConfiguration().getHttpClientConfig();
        HttpClientBuilder httpClientBuilder = HttpClientBuilder.create();

        // https settings
        if (config.isDisableCertVerify()) {
            try {
                SSLContextBuilder builder = new SSLContextBuilder();
                builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                httpClientBuilder.setSSLSocketFactory(
                        new SSLConnectionSocketFactory(builder.build(), new HostnameVerifier() {
                            @Override
                            public boolean verify(String s, SSLSession sslSession) {
                                return true;
                            }
                        }));

            } catch (Exception e) {
                LOGGER.error("Could not configure HTTP client SSL self signed strategy", e);
            }
        }

        // request retry settings
        int retryCount = 0;
        if (config.getRequestRetryLimit() > 0) {
            retryCount = config.getRequestRetryLimit();
        }
        httpClientBuilder.setRetryHandler(new DefaultHttpRequestRetryHandler(retryCount, false));

        this.httpActivityExecutor = new HttpActivityExecutor(httpClientBuilder, new NopErrorPropagator());
    }


    @Override
    public void execute(CommandContext commandContext, PlanItemInstanceEntity planItemInstanceEntity) {
        HttpRequest request = new HttpRequest();

        try {
            request.setMethod(requestMethod);
            request.setUrl(requestUrl);
            request.setHeaders(requestHeaders);
            request.setBody(requestBody);
            request.setTimeout(ExpressionUtils.parseInt(requestTimeout));
            request.setNoRedirects(parseBoolean(disallowRedirects));
            request.setIgnoreErrors(parseBoolean(ignoreException));
            request.setSaveRequest(parseBoolean(saveRequestVariables));
            request.setSaveResponse(parseBoolean(saveResponseParameters));
            request.setPrefix(resultVariablePrefix);

            String failCodes = failStatusCodes;
            String handleCodes = handleStatusCodes;

            if (failCodes != null) {
                request.setFailCodes(getStringSetFromField(failCodes));
            }
            if (handleCodes != null) {
                request.setHandleCodes(getStringSetFromField(handleCodes));
            }

            if (request.getPrefix() == null) {
                request.setPrefix(planItemInstanceEntity.getElementId());
            }

            // Save request fields
            if (request.isSaveRequest()) {
                planItemInstanceEntity.setVariable(request.getPrefix() + ".requestMethod", request.getMethod());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".requestUrl", request.getUrl());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".requestHeaders", request.getHeaders());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".requestBody", request.getBody());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".requestTimeout", request.getTimeout());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".disallowRedirects", request.isNoRedirects());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".failStatusCodes", failCodes);
                planItemInstanceEntity.setVariable(request.getPrefix() + ".handleStatusCodes", handleCodes);
                planItemInstanceEntity.setVariable(request.getPrefix() + ".ignoreException", request.isIgnoreErrors());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".saveRequestVariables", request.isSaveRequest());
                planItemInstanceEntity.setVariable(request.getPrefix() + ".saveResponseParameters", request.isSaveResponse());
            }

        } catch (Exception e) {
            if (e instanceof FlowableException) {
                throw (FlowableException) e;
            } else {
                throw new FlowableException(HTTP_TASK_REQUEST_FIELD_INVALID + " in execution " + planItemInstanceEntity.getId(), e);
            }
        }

        httpActivityExecutor.validate(request);

        httpActivityExecutor.execute(request,
                planItemInstanceEntity,
                planItemInstanceEntity.getId(),
                createHttpRequestHandler(serviceTask.getHttpRequestHandler(), CommandContextUtil.getCmmnEngineConfiguration()),
                createHttpResponseHandler(serviceTask.getHttpResponseHandler(), CommandContextUtil.getCmmnEngineConfiguration()),
                responseVariableName,
                mapExceptions,
                CommandContextUtil.getCmmnEngineConfiguration().getHttpClientConfig().getSocketTimeout(),
                CommandContextUtil.getCmmnEngineConfiguration().getHttpClientConfig().getConnectTimeout(),
                CommandContextUtil.getCmmnEngineConfiguration().getHttpClientConfig().getConnectionRequestTimeout()
        );

        CommandContextUtil.getAgenda().planCompletePlanItemInstance(planItemInstanceEntity);

    }

    protected HttpRequestHandler createHttpRequestHandler(FlowableHttpRequestHandler flowableHandler, CmmnEngineConfiguration cmmnEngineConfiguration) {
        HttpRequestHandler handler = null;

        if (flowableHandler != null) {
            if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equalsIgnoreCase(flowableHandler.getImplementationType())) {
                handler = new ClassDelegateHttpHandler(flowableHandler.getImplementation(),
                        flowableHandler.getFieldExtensions());

            } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equalsIgnoreCase(flowableHandler.getImplementationType())) {
                handler = new DelegateExpressionHttpHandler(cmmnEngineConfiguration.getExpressionManager().createExpression(flowableHandler.getImplementation()),
                        flowableHandler.getFieldExtensions());
            }
        }
        return handler;
    }

    protected HttpResponseHandler createHttpResponseHandler(FlowableHttpResponseHandler handler, CmmnEngineConfiguration cmmnEngineConfiguration) {
        HttpResponseHandler responseHandler = null;

        if (handler != null) {
            if (ImplementationType.IMPLEMENTATION_TYPE_CLASS.equalsIgnoreCase(handler.getImplementationType())) {
                responseHandler = new ClassDelegateHttpHandler(handler.getImplementation(),
                        handler.getFieldExtensions());

            } else if (ImplementationType.IMPLEMENTATION_TYPE_DELEGATEEXPRESSION.equalsIgnoreCase(handler.getImplementationType())) {
                responseHandler = new DelegateExpressionHttpHandler(cmmnEngineConfiguration.getExpressionManager().createExpression(handler.getImplementation()),
                        handler.getFieldExtensions());
            }
        }
        return responseHandler;
    }

    public void setServiceTask(HttpServiceTask serviceTask) {
        this.serviceTask = serviceTask;
    }

}

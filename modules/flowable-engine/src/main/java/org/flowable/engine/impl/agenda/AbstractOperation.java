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
package org.flowable.engine.impl.agenda;

import org.flowable.bpmn.model.FlowElement;
import org.flowable.bpmn.model.HasExecutionListeners;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.FlowableEngineAgenda;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.engine.impl.util.ProcessDefinitionUtil;

/**
 * Abstract superclass for all operation interfaces (which are {@link Runnable} instances), exposing some shared helper methods and member fields to subclasses.
 * 
 * An operations is a {@link Runnable} instance that is put on the {@link FlowableEngineAgenda} during the execution of a {@link Command}.
 * 
 * @author Joram Barrez
 */
public abstract class AbstractOperation implements Runnable {

    protected CommandContext commandContext;
    protected FlowableEngineAgenda agenda;
    protected ExecutionEntity execution;

    public AbstractOperation() {

    }

    public AbstractOperation(CommandContext commandContext, ExecutionEntity execution) {
        this.commandContext = commandContext;
        this.execution = execution;
        this.agenda = CommandContextUtil.getAgenda(commandContext);
    }

    /**
     * Helper method to match the activityId of an execution with a FlowElement of the process definition referenced by the execution.
     */
    protected FlowElement getCurrentFlowElement(final ExecutionEntity execution) {
        if (execution.getCurrentFlowElement() != null) {
            return execution.getCurrentFlowElement();
        } else if (execution.getCurrentActivityId() != null) {
            String processDefinitionId = execution.getProcessDefinitionId();
            org.flowable.bpmn.model.Process process = ProcessDefinitionUtil.getProcess(processDefinitionId);
            String activityId = execution.getCurrentActivityId();
            FlowElement currentFlowElement = process.getFlowElement(activityId, true);
            return currentFlowElement;
        }
        return null;
    }

    /**
     * Executes the execution listeners defined on the given element, with the given event type. Uses the {@link #execution} of this operation instance as argument for the execution listener.
     *
     * @return true in case of success. false when any execution listener has thrown a BPMN error.
     * @see org.flowable.engine.impl.bpmn.listener.ListenerNotificationHelper#executeExecutionListeners(HasExecutionListeners, DelegateExecution, String)
     */
    protected boolean executeExecutionListeners(HasExecutionListeners elementWithExecutionListeners, String eventType) {
        return executeExecutionListeners(elementWithExecutionListeners, execution, eventType);
    }

    /**
     * Executes the execution listeners defined on the given element, with the given event type, and passing the provided execution to the {@link ExecutionListener} instances.
     *
     * @return true in case of success. false when any execution listener has thrown a BPMN error.
     * @see org.flowable.engine.impl.bpmn.listener.ListenerNotificationHelper#executeExecutionListeners(HasExecutionListeners, DelegateExecution, String)
     */
    protected boolean executeExecutionListeners(HasExecutionListeners elementWithExecutionListeners,
            ExecutionEntity executionEntity, String eventType) {
        return CommandContextUtil.getProcessEngineConfiguration(commandContext).getListenerNotificationHelper()
                .executeExecutionListeners(elementWithExecutionListeners, executionEntity, eventType);
    }

    /**
     * Returns the first parent execution of the provided execution that is a scope.
     */
    protected ExecutionEntity findFirstParentScopeExecution(ExecutionEntity executionEntity) {
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
        ExecutionEntity parentScopeExecution = null;
        ExecutionEntity currentlyExaminedExecution = executionEntityManager.findById(executionEntity.getParentId());
        while (currentlyExaminedExecution != null && parentScopeExecution == null) {
            if (currentlyExaminedExecution.isScope()) {
                parentScopeExecution = currentlyExaminedExecution;
            } else {
                currentlyExaminedExecution = executionEntityManager.findById(currentlyExaminedExecution.getParentId());
            }
        }
        return parentScopeExecution;
    }

    public CommandContext getCommandContext() {
        return commandContext;
    }

    public void setCommandContext(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    public FlowableEngineAgenda getAgenda() {
        return agenda;
    }

    public void setAgenda(FlowableEngineAgenda agenda) {
        this.agenda = agenda;
    }

    public ExecutionEntity getExecution() {
        return execution;
    }

    public void setExecution(ExecutionEntity execution) {
        this.execution = execution;
    }

}

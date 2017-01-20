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
package org.activiti5.engine.delegate.event.impl;

import org.activiti.engine.common.api.delegate.event.ActivitiEvent;
import org.activiti.engine.delegate.event.ActivitiEngineEventType;
import org.activiti5.engine.ActivitiIllegalArgumentException;

/**
 * Base class for all {@link ActivitiEvent} implementations.
 * 
 * @author Frederik Heremans
 */
public class ActivitiEventImpl implements ActivitiEvent {

	protected ActivitiEngineEventType type;
	protected String executionId;
	protected String processInstanceId;
	protected String processDefinitionId;

	/**
	 * Creates a new event implementation, not part of an execution context.
	 */
	public ActivitiEventImpl(ActivitiEngineEventType type) {
		this(type, null, null, null);
	}

	/**
	 * Creates a new event implementation, part of an execution context.
	 */
	public ActivitiEventImpl(ActivitiEngineEventType type, String executionId, String processInstanceId,
      String processDefinitionId) {
		if(type == null) {
			throw new ActivitiIllegalArgumentException("type is null");
		}
		this.type = type;
	  this.executionId = executionId;
	  this.processInstanceId = processInstanceId;
	  this.processDefinitionId = processDefinitionId;
  }

	public ActivitiEngineEventType getType() {
		return type;
	}

	public void setType(ActivitiEngineEventType type) {
		this.type = type;
	}
	
	public String getExecutionId() {
	  return executionId;
  }
	
	public void setExecutionId(String executionId) {
	  this.executionId = executionId;
  }
	
	public String getProcessDefinitionId() {
	  return processDefinitionId;
  }
	
	public void setProcessDefinitionId(String processDefinitionId) {
	  this.processDefinitionId = processDefinitionId;
  }
	
	public String getProcessInstanceId() {
	  return processInstanceId;
  }
	
	public void setProcessInstanceId(String processInstanceId) {
	  this.processInstanceId = processInstanceId;
  }
}

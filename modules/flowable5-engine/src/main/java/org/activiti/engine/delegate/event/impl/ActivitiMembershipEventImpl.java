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
package org.activiti.engine.delegate.event.impl;

import org.flowable.idm.api.event.FlowableIdmEventType;
import org.flowable.idm.api.event.FlowableIdmMembershipEvent;
import org.flowable.idm.engine.delegate.event.impl.FlowableIdmEventImpl;

/**
 * Implementation of {@link FlowableIdmMembershipEvent}.
 * @author Frederik Heremans
 */
public class ActivitiMembershipEventImpl extends FlowableIdmEventImpl implements FlowableIdmMembershipEvent {

	protected String userId;
	protected String groupId;
	
	public ActivitiMembershipEventImpl(FlowableIdmEventType type) {
	  super(type);
  }
	
	public void setUserId(String userId) {
	  this.userId = userId;
  }
	
	public String getUserId() {
	  return userId;
  }
	
	public void setGroupId(String groupId) {
	  this.groupId = groupId;
  }
	
	public String getGroupId() {
	  return groupId;
  }
}

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

package org.activiti.idm.engine.impl.cmd;

import java.io.Serializable;
import java.util.List;

import org.activiti.engine.common.api.ActivitiIllegalArgumentException;
import org.activiti.idm.api.Group;
import org.activiti.idm.engine.impl.interceptor.Command;
import org.activiti.idm.engine.impl.interceptor.CommandContext;

/**
 * @author Joram Barrez
 */
public class GetGroupsWithPrivilegeCmd implements Command<List<Group>>, Serializable {

  private static final long serialVersionUID = 1L;

  protected String name;

  public GetGroupsWithPrivilegeCmd(String name) {
    if(name == null) {
      throw new ActivitiIllegalArgumentException("name is null");
    }
    this.name = name;
  }

  public List<Group> execute(CommandContext commandContext) {
    return commandContext.getGroupEntityManager().findGroupsByPrivilegeId(name);
  }
  
}

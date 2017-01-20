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
package org.activiti.idm.engine.impl;

import java.util.List;
import java.util.Map;

import org.activiti.idm.api.NativeTokenQuery;
import org.activiti.idm.api.Token;
import org.activiti.idm.engine.impl.interceptor.CommandContext;
import org.activiti.idm.engine.impl.interceptor.CommandExecutor;

public class NativeTokenQueryImpl extends AbstractNativeQuery<NativeTokenQuery, Token> implements NativeTokenQuery {

  private static final long serialVersionUID = 1L;

  public NativeTokenQueryImpl(CommandContext commandContext) {
    super(commandContext);
  }

  public NativeTokenQueryImpl(CommandExecutor commandExecutor) {
    super(commandExecutor);
  }

  // results ////////////////////////////////////////////////////////////////

  public List<Token> executeList(CommandContext commandContext, Map<String, Object> parameterMap, int firstResult, int maxResults) {
    return commandContext.getTokenEntityManager().findTokensByNativeQuery(parameterMap, firstResult, maxResults);
  }

  public long executeCount(CommandContext commandContext, Map<String, Object> parameterMap) {
    return commandContext.getTokenEntityManager().findTokenCountByNativeQuery(parameterMap);
  }

}
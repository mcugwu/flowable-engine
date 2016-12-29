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
package org.flowable.rest.dmn.service.api.decision;

import org.flowable.rest.variable.EngineRestVariable;

import java.util.List;

/**
 * @author Yvo Swillens
 */
public class ExecuteDecisionRequest {

  protected String decisionKey;
  protected String tenantId;
  protected List<EngineRestVariable> inputVariables;

  public String getDecisionKey() {
    return decisionKey;
  }

  public void setDecisionKey(String decisionKey) {
    this.decisionKey = decisionKey;
  }

  public String getTenantId() {
    return tenantId;
  }

  public void setTenantId(String tenantId) {
    this.tenantId = tenantId;
  }

  public List<EngineRestVariable> getInputVariables() {
    return inputVariables;
  }

  public void setInputVariables(List<EngineRestVariable> variables) {
    this.inputVariables = variables;
  }

}

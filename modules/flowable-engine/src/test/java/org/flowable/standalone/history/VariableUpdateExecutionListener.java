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

package org.flowable.standalone.history;

import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.flowable.engine.common.api.delegate.Expression;

/**
 * @author Frederik Heremans
 */
public class VariableUpdateExecutionListener implements ExecutionListener {

    private Expression varName;

    @Override
    public void notify(DelegateExecution execution) {
        String variableName = (String) varName.getValue(execution);
        execution.setVariable(variableName, "Event: " + execution.getEventName());
    }

}

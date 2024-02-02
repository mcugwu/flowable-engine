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

package org.flowable.assertions.process;

import org.flowable.engine.*;
import org.flowable.engine.history.HistoricProcessInstance;
import org.flowable.engine.runtime.ProcessInstance;

/**
 * @author martin.grofcik
 */
public class Utils {

    protected static String getProcessDescription(ProcessInstance actual) {
        return getProcessDescription(actual.getProcessDefinitionKey(), actual.getId());
    }

    protected static String getProcessDescription(HistoricProcessInstance actual) {
        return getProcessDescription(actual.getProcessDefinitionKey(), actual.getId());
    }

    protected static String getProcessDescription(String processDefinitionKey, String id) {
        return "Expected process instance <"+processDefinitionKey+", "+id+">";
    }

    protected static TaskService getTaskService() {
        return getProcessEngine().getTaskService();
    }

    protected static ProcessEngine getProcessEngine() {
        return ProcessEngines.getProcessEngines().get("default");
    }

}

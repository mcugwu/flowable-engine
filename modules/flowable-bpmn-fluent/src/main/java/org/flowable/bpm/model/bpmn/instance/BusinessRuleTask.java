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
package org.flowable.bpm.model.bpmn.instance;

import org.flowable.bpm.model.bpmn.builder.BusinessRuleTaskBuilder;

/**
 * The BPMN businessRuleTask element.
 */
public interface BusinessRuleTask
        extends Task {

    BusinessRuleTaskBuilder builder();

    String getImplementation();

    void setImplementation(String implementation);

    /* Flowable extensions */

    String getFlowableClass();

    void setFlowableClass(String flowableClass);

    String getFlowableDelegateExpression();

    void setFlowableDelegateExpression(String flowableExpression);

    String getFlowableExpression();

    void setFlowableExpression(String flowableExpression);

    String getFlowableResultVariable();

    void setFlowableResultVariable(String flowableResultVariable);

    String getFlowableType();

    void setFlowableType(String flowableType);
}

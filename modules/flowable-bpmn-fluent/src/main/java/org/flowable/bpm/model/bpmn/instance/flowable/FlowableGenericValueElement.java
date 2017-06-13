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
package org.flowable.bpm.model.bpmn.instance.flowable;

import org.flowable.bpm.model.bpmn.instance.BpmnModelElementInstance;

/**
 * A helper interface for Flowable extension elements which hold a generic child element like flowable:inputParameter, flowable:outputParameter and
 * flowable:entry..
 */
public interface FlowableGenericValueElement {

    <T extends BpmnModelElementInstance> T getValue();

    void removeValue();

    <T extends BpmnModelElementInstance> void setValue(T value);

}

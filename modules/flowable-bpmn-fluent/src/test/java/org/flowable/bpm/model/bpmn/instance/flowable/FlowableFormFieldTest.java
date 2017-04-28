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

import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.FLOWABLE_NS;

import org.flowable.bpm.model.bpmn.instance.BpmnModelElementInstanceTest;

import java.util.Arrays;
import java.util.Collection;

public class FlowableFormFieldTest
        extends BpmnModelElementInstanceTest {

    @Override
    public TypeAssumption getTypeAssumption() {
        return new TypeAssumption(FLOWABLE_NS, false);
    }

    @Override
    public Collection<ChildElementAssumption> getChildElementAssumptions() {
        return Arrays.asList(
                new ChildElementAssumption(FLOWABLE_NS, FlowableProperties.class, 0, 1),
                new ChildElementAssumption(FLOWABLE_NS, FlowableValidation.class, 0, 1),
                new ChildElementAssumption(FLOWABLE_NS, FlowableValue.class));
    }

    @Override
    public Collection<AttributeAssumption> getAttributesAssumptions() {
        return Arrays.asList(
                new AttributeAssumption(FLOWABLE_NS, "id"),
                new AttributeAssumption(FLOWABLE_NS, "type"));
    }
}

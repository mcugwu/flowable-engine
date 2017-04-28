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

import org.flowable.bpm.model.bpmn.impl.instance.DataInputRefs;
import org.flowable.bpm.model.bpmn.impl.instance.OptionalInputRefs;
import org.flowable.bpm.model.bpmn.impl.instance.OutputSetRefs;
import org.flowable.bpm.model.bpmn.impl.instance.WhileExecutingInputRefs;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;


public class InputSetTest
        extends BpmnModelElementInstanceTest {

    @Override
    public TypeAssumption getTypeAssumption() {
        return new TypeAssumption(BaseElement.class, false);
    }

    @Override
    public Collection<ChildElementAssumption> getChildElementAssumptions() {
        return Arrays.asList(
                new ChildElementAssumption(DataInputRefs.class),
                new ChildElementAssumption(OptionalInputRefs.class),
                new ChildElementAssumption(WhileExecutingInputRefs.class),
                new ChildElementAssumption(OutputSetRefs.class));
    }

    @Override
    public Collection<AttributeAssumption> getAttributesAssumptions() {
        return Collections.singletonList(
                new AttributeAssumption("name"));
    }
}

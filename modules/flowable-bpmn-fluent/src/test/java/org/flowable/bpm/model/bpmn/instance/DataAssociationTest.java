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

import org.flowable.bpm.model.bpmn.impl.instance.SourceRef;
import org.flowable.bpm.model.bpmn.impl.instance.TargetRef;
import org.flowable.bpm.model.bpmn.impl.instance.Transformation;

import java.util.Arrays;
import java.util.Collection;


public class DataAssociationTest
        extends BpmnModelElementInstanceTest {

    public TypeAssumption getTypeAssumption() {
        return new TypeAssumption(BaseElement.class, false);
    }

    public Collection<ChildElementAssumption> getChildElementAssumptions() {
        return Arrays.asList(
                new ChildElementAssumption(SourceRef.class),
                new ChildElementAssumption(TargetRef.class, 1, 1),
                new ChildElementAssumption(Transformation.class, 0, 1),
                new ChildElementAssumption(Assignment.class));
    }

    public Collection<AttributeAssumption> getAttributesAssumptions() {
        return null;
    }
}

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
package org.flowable.bpm.model.bpmn.impl.instance;

import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.BPMN20_NS;
import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ELEMENT_OUTGOING;
import static org.flowable.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;

import org.flowable.bpm.model.xml.ModelBuilder;
import org.flowable.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.flowable.bpm.model.xml.type.ModelElementTypeBuilder;

/**
 * The BPMN outgoing element of the BPMN tFlowNode type.
 */
public class Outgoing
        extends BpmnModelElementInstanceImpl {

    public static void registerType(ModelBuilder modelBuilder) {
        ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(Outgoing.class, BPMN_ELEMENT_OUTGOING)
                .namespaceUri(BPMN20_NS)
                .instanceProvider(new ModelTypeInstanceProvider<Outgoing>() {
                    @Override
                    public Outgoing newInstance(ModelTypeInstanceContext instanceContext) {
                        return new Outgoing(instanceContext);
                    }
                });

        typeBuilder.build();
    }

    public Outgoing(ModelTypeInstanceContext instanceContext) {
        super(instanceContext);
    }
}

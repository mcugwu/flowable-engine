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
import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ATTRIBUTE_SIGNAL_REF;
import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ELEMENT_SIGNAL_EVENT_DEFINITION;
import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.FLOWABLE_ATTRIBUTE_ASYNC;
import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.FLOWABLE_NS;
import static org.flowable.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;

import org.flowable.bpm.model.bpmn.instance.EventDefinition;
import org.flowable.bpm.model.bpmn.instance.Signal;
import org.flowable.bpm.model.bpmn.instance.SignalEventDefinition;
import org.flowable.bpm.model.xml.ModelBuilder;
import org.flowable.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.flowable.bpm.model.xml.type.ModelElementTypeBuilder;
import org.flowable.bpm.model.xml.type.attribute.Attribute;
import org.flowable.bpm.model.xml.type.reference.AttributeReference;

/**
 * The BPMN signalEventDefinition element.
 */
public class SignalEventDefinitionImpl
        extends EventDefinitionImpl
        implements SignalEventDefinition {

    protected static AttributeReference<Signal> signalRefAttribute;
    protected static Attribute<Boolean> flowableAsyncAttribute;

    public static void registerType(ModelBuilder modelBuilder) {
        ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(SignalEventDefinition.class, BPMN_ELEMENT_SIGNAL_EVENT_DEFINITION)
                .namespaceUri(BPMN20_NS)
                .extendsType(EventDefinition.class)
                .instanceProvider(new ModelTypeInstanceProvider<SignalEventDefinition>() {
                    @Override
                    public SignalEventDefinition newInstance(ModelTypeInstanceContext instanceContext) {
                        return new SignalEventDefinitionImpl(instanceContext);
                    }
                });

        signalRefAttribute = typeBuilder.stringAttribute(BPMN_ATTRIBUTE_SIGNAL_REF)
                .qNameAttributeReference(Signal.class)
                .build();

        /* Flowable Attributes */
        flowableAsyncAttribute = typeBuilder.booleanAttribute(FLOWABLE_ATTRIBUTE_ASYNC)
                .namespace(FLOWABLE_NS)
                .defaultValue(false)
                .build();

        typeBuilder.build();
    }

    public SignalEventDefinitionImpl(ModelTypeInstanceContext context) {
        super(context);
    }

    @Override
    public Signal getSignal() {
        return signalRefAttribute.getReferenceTargetElement(this);
    }

    @Override
    public void setSignal(Signal signal) {
        signalRefAttribute.setReferenceTargetElement(this, signal);
    }

    @Override
    public boolean isFlowableAsync() {
        return flowableAsyncAttribute.getValue(this);
    }

    @Override
    public void setFlowableAsync(boolean flowableAsync) {
        flowableAsyncAttribute.setValue(this, flowableAsync);
    }
}

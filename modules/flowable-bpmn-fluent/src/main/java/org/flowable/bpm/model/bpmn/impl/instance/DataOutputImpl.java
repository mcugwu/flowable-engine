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
import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ATTRIBUTE_IS_COLLECTION;
import static org.flowable.bpm.model.bpmn.impl.BpmnModelConstants.BPMN_ATTRIBUTE_NAME;
import static org.flowable.bpm.model.xml.type.ModelElementTypeBuilder.ModelTypeInstanceProvider;

import org.flowable.bpm.model.bpmn.impl.BpmnModelConstants;
import org.flowable.bpm.model.bpmn.instance.DataOutput;
import org.flowable.bpm.model.bpmn.instance.ItemAwareElement;
import org.flowable.bpm.model.xml.ModelBuilder;
import org.flowable.bpm.model.xml.impl.instance.ModelTypeInstanceContext;
import org.flowable.bpm.model.xml.type.ModelElementTypeBuilder;
import org.flowable.bpm.model.xml.type.attribute.Attribute;

public class DataOutputImpl
        extends ItemAwareElementImpl
        implements DataOutput {

    protected static Attribute<String> nameAttribute;
    protected static Attribute<Boolean> isCollectionAttribute;

    public static void registerType(ModelBuilder modelBuilder) {
        ModelElementTypeBuilder typeBuilder = modelBuilder.defineType(DataOutput.class, BpmnModelConstants.BPMN_ELEMENT_DATA_OUTPUT)
                .namespaceUri(BPMN20_NS)
                .extendsType(ItemAwareElement.class)
                .instanceProvider(new ModelTypeInstanceProvider<DataOutput>() {
                    public DataOutput newInstance(ModelTypeInstanceContext instanceContext) {
                        return new DataOutputImpl(instanceContext);
                    }
                });

        nameAttribute = typeBuilder.stringAttribute(BPMN_ATTRIBUTE_NAME)
                .build();

        isCollectionAttribute = typeBuilder.booleanAttribute(BPMN_ATTRIBUTE_IS_COLLECTION)
                .defaultValue(false)
                .build();

        typeBuilder.build();
    }

    public DataOutputImpl(ModelTypeInstanceContext instanceContext) {
        super(instanceContext);
    }

    public String getName() {
        return nameAttribute.getValue(this);
    }

    public void setName(String name) {
        nameAttribute.setValue(this, name);
    }

    public boolean isCollection() {
        return isCollectionAttribute.getValue(this);
    }

    public void setCollection(boolean isCollection) {
        isCollectionAttribute.setValue(this, isCollection);
    }

}

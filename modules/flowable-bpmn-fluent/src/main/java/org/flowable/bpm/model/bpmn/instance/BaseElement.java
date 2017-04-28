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

import org.flowable.bpm.model.bpmn.instance.di.DiagramElement;

import java.util.Collection;

/**
 * The BPMN baseElement element.
 */
public interface BaseElement
        extends BpmnModelElementInstance {

    String getId();

    void setId(String id);

    Collection<Documentation> getDocumentations();

    ExtensionElements getExtensionElements();

    void setExtensionElements(ExtensionElements extensionElements);

    DiagramElement getDiagramElement();

}

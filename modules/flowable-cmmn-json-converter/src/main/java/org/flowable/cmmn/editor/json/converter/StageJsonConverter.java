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
package org.flowable.cmmn.editor.json.converter;

import java.util.Map;

import org.flowable.cmmn.editor.json.converter.CmmnJsonConverter.CmmnModelIdHelper;
import org.flowable.cmmn.editor.json.model.ModelInfo;
import org.flowable.cmmn.model.BaseElement;
import org.flowable.cmmn.model.CaseElement;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.GraphicInfo;
import org.flowable.cmmn.model.PlanItem;
import org.flowable.cmmn.model.Stage;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Tijs Rademakers
 */
public class StageJsonConverter extends BaseCmmnJsonConverter implements FormAwareConverter, FormKeyAwareConverter,
        DecisionTableAwareConverter, DecisionTableKeyAwareConverter {

    protected Map<String, String> formMap;
    protected Map<String, ModelInfo> formKeyMap;
    protected Map<String, String> decisionTableMap;
    protected Map<String, ModelInfo> decisionTableKeyMap;

    public static void fillTypes(Map<String, Class<? extends BaseCmmnJsonConverter>> convertersToCmmnMap,
            Map<Class<? extends BaseElement>, Class<? extends BaseCmmnJsonConverter>> convertersToJsonMap) {

        fillJsonTypes(convertersToCmmnMap);
        fillCmmnTypes(convertersToJsonMap);
    }

    public static void fillJsonTypes(Map<String, Class<? extends BaseCmmnJsonConverter>> convertersToCmmnMap) {
        convertersToCmmnMap.put(STENCIL_STAGE, StageJsonConverter.class);
    }

    public static void fillCmmnTypes(Map<Class<? extends BaseElement>, Class<? extends BaseCmmnJsonConverter>> convertersToJsonMap) {
        convertersToJsonMap.put(Stage.class, StageJsonConverter.class);
    }

    protected String getStencilId(BaseElement baseElement) {
        return STENCIL_STAGE;
    }

    protected void convertElementToJson(ObjectNode elementNode, ObjectNode propertiesNode, ActivityProcessor processor, BaseElement baseElement, CmmnModel cmmnModel) {
        PlanItem planItem = (PlanItem) baseElement;
        Stage stage = (Stage) planItem.getPlanItemDefinition();

        GraphicInfo graphicInfo = cmmnModel.getGraphicInfo(planItem.getId());
        ArrayNode subProcessShapesArrayNode = objectMapper.createArrayNode();
        
        processor.processPlanItems(stage, cmmnModel, subProcessShapesArrayNode, formKeyMap, decisionTableKeyMap, graphicInfo.getX(), graphicInfo.getY());
        
        elementNode.set("childShapes", subProcessShapesArrayNode);
    }
    
    @Override
    protected CaseElement convertJsonToElement(JsonNode elementNode, JsonNode modelNode, ActivityProcessor processor, 
                    BaseElement parentElement, Map<String, JsonNode> shapeMap, CmmnModel cmmnModel, CmmnModelIdHelper cmmnModelIdHelper) {
        
        Stage stage = new Stage();

        JsonNode childShapesArray = elementNode.get(EDITOR_CHILD_SHAPES);
        processor.processJsonElements(childShapesArray, modelNode, stage, shapeMap, formMap, decisionTableMap, cmmnModel, cmmnModelIdHelper);
        
        Stage parentStage = (Stage) parentElement;
        stage.setParent(parentStage);

        return stage;
    }

    @Override
    public void setFormMap(Map<String, String> formMap) {
        this.formMap = formMap;
    }

    @Override
    public void setFormKeyMap(Map<String, ModelInfo> formKeyMap) {
        this.formKeyMap = formKeyMap;
    }

    @Override
    public void setDecisionTableMap(Map<String, String> decisionTableMap) {
        this.decisionTableMap = decisionTableMap;
    }

    @Override
    public void setDecisionTableKeyMap(Map<String, ModelInfo> decisionTableKeyMap) {
        this.decisionTableKeyMap = decisionTableKeyMap;
    }
}

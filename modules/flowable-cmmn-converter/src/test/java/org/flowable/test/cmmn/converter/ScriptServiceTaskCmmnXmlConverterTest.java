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
package org.flowable.test.cmmn.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

import java.util.List;

import org.flowable.cmmn.model.Case;
import org.flowable.cmmn.model.CmmnModel;
import org.flowable.cmmn.model.FieldExtension;
import org.flowable.cmmn.model.PlanItem;
import org.flowable.cmmn.model.PlanItemDefinition;
import org.flowable.cmmn.model.ScriptServiceTask;
import org.flowable.cmmn.model.Stage;
import org.flowable.cmmn.model.Task;
import org.junit.Test;

/**
 * @author Tijs Rademakers
 */
public class ScriptServiceTaskCmmnXmlConverterTest extends AbstractConverterTest {

    private static final String CMMN_RESOURCE = "org/flowable/test/cmmn/converter/script-task.cmmn";

    @Test
    public void convertXMLToModel() throws Exception {
        CmmnModel cmmnModel = readXMLFile(CMMN_RESOURCE);
        validateModel(cmmnModel);
    }

    @Test
    public void convertModelToXML() throws Exception {
        CmmnModel cmmnModel = readXMLFile(CMMN_RESOURCE);
        CmmnModel parsedModel = exportAndReadXMLFile(cmmnModel);
        validateModel(parsedModel);
    }

    public void validateModel(CmmnModel cmmnModel) {
        assertThat(cmmnModel).isNotNull();

        // Case
        assertThat(cmmnModel.getCases())
            .hasSize(1)
            .extracting(Case::getId, Case::getInitiatorVariableName)
            .containsExactly(tuple("scriptCase", "test"));

        // Plan model
        Stage planModel = cmmnModel.getCases().get(0).getPlanModel();
        assertThat(planModel)
            .isNotNull()
            .extracting(Stage::getId, Stage::getName)
            .containsExactly("myScriptPlanModel", "My Script CasePlanModel");

        // Plan items definitions
        List<PlanItemDefinition> planItemDefinitions = planModel.getPlanItemDefinitions();
        assertThat(planItemDefinitions).hasSize(1);
        assertThat(planModel.findPlanItemDefinitionsOfType(Task.class, false)).hasSize(1);

        // Plan items
        List<PlanItem> planItems = planModel.getPlanItems();
        assertThat(planItems).hasSize(1);

        PlanItem planItemTaskA = cmmnModel.findPlanItem("planItemTaskA");
        PlanItemDefinition planItemDefinition = planItemTaskA.getPlanItemDefinition();

        assertThat(planItemTaskA.getEntryCriteria()).isEmpty();
        assertThat(planItemDefinition).isInstanceOf(ScriptServiceTask.class);
        ScriptServiceTask scriptTask = (ScriptServiceTask) planItemDefinition;
        assertThat(scriptTask.getType()).isEqualTo(ScriptServiceTask.SCRIPT_TASK);
        assertThat(scriptTask.getScriptFormat()).isEqualTo("javascript");
        assertThat(scriptTask.getResultVariableName()).isEqualTo("scriptResult");
        assertThat(scriptTask.isAutoStoreVariables()).isFalse();
        assertThat(scriptTask.isBlocking()).isTrue();
        assertThat(scriptTask.isAsync()).isFalse();

        assertThat(scriptTask.getFieldExtensions())
            .hasSize(1)
            .extracting(FieldExtension::getFieldName, FieldExtension::getStringValue)
            .containsExactly(tuple("script", "var a = 5;"));
    }

}

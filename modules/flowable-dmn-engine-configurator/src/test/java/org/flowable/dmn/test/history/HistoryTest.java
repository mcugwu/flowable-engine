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
package org.flowable.dmn.test.history;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collections;
import java.util.List;

import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.cmmn.api.CmmnRuntimeService;
import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.common.engine.impl.interceptor.EngineConfigurationConstants;
import org.flowable.dmn.api.DmnHistoricDecisionExecution;
import org.flowable.dmn.api.DmnHistoryService;
import org.flowable.dmn.engine.DmnEngineConfiguration;
import org.flowable.dmn.engine.DmnEngines;
import org.flowable.dmn.engine.test.AbstractFlowableDmnEngineConfiguratorTest;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.junit.Test;

/**
 * @author Tijs Rademakers
 */
public class HistoryTest extends AbstractFlowableDmnEngineConfiguratorTest {

    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/callActivityProcess.bpmn20.xml",
            "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml",
            "org/flowable/dmn/engine/test/deployment/simple.dmn" })
    public void deployNestedProcessAndDecisionTable() {
        try {
            ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("callActivityProcess", Collections.singletonMap("inputVariable1", 10));
    
            DmnHistoryService dmnHistoryService = DmnEngines.getDefaultDmnEngine().getDmnHistoryService();
            DmnHistoricDecisionExecution decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().processInstanceIdWithChildren(processInstance.getId()).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            String subProcessInstanceId = decisionExecution.getInstanceId();
            assertNotEquals(subProcessInstanceId, processInstance.getId());
            
            decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().instanceId(processInstance.getId()).singleResult();
            assertNull(decisionExecution);
            
            decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().instanceId(subProcessInstanceId).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            
        } finally {
            deleteAllDmnDeployments();
        }
    }
    
    @Test
    @Deployment(resources = { "org/flowable/dmn/engine/test/deployment/callActivityProcess.bpmn20.xml",
            "org/flowable/dmn/engine/test/deployment/oneDecisionTaskProcess.bpmn20.xml",
            "org/flowable/dmn/engine/test/deployment/simple.dmn" })
    public void deployNestedCaseAndDecisionTable() {
        try {
            CmmnEngineConfiguration cmmnEngineConfiguration = (CmmnEngineConfiguration) processEngineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
            
            CmmnRepositoryService cmmnRepositoryService = cmmnEngineConfiguration.getCmmnRepositoryService();
            cmmnRepositoryService.createDeployment().addClasspathResource("org/flowable/dmn/engine/test/deployment/decisionAndProcessTask.cmmn").deploy();
            
            CmmnRuntimeService cmmnRuntimeService = cmmnEngineConfiguration.getCmmnRuntimeService();
            CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("myCase").variable("inputVariable1", 10).start();
            
            DmnHistoryService dmnHistoryService = DmnEngines.getDefaultDmnEngine().getDmnHistoryService();
            List<DmnHistoricDecisionExecution> decisionExecutions = dmnHistoryService.createHistoricDecisionExecutionQuery().caseInstanceIdWithChildren(caseInstance.getId()).list();
            assertEquals(1, decisionExecutions.size());
            
            PlanItemInstance planItemInstance = cmmnRuntimeService.createPlanItemInstanceQuery()
                            .caseInstanceId(caseInstance.getId())
                            .planItemDefinitionId("task")
                            .singleResult();
            cmmnRuntimeService.triggerPlanItemInstance(planItemInstance.getId());
            
            decisionExecutions = dmnHistoryService.createHistoricDecisionExecutionQuery().caseInstanceIdWithChildren(caseInstance.getId()).list();
            assertEquals(2, decisionExecutions.size());
            
            String caseInstanceId = null;
            String processInstanceId = null;
            for (DmnHistoricDecisionExecution dmnExecution : decisionExecutions) {
                assertEquals("decision1", dmnExecution.getDecisionKey());
                if (caseInstance.getId().equals(dmnExecution.getInstanceId())) {
                    caseInstanceId = dmnExecution.getInstanceId();
                } else {
                    processInstanceId = dmnExecution.getInstanceId();
                }
            }
            
            assertNotNull(caseInstanceId);
            assertNotNull(processInstanceId);
            
            DmnHistoricDecisionExecution decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().instanceId(processInstanceId).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            
            decisionExecution = dmnHistoryService.createHistoricDecisionExecutionQuery().processInstanceIdWithChildren(processInstanceId).singleResult();
            assertEquals("decision1", decisionExecution.getDecisionKey());
            
        } finally {
            deleteAllDmnDeployments();
        }
    }
    
    protected void deleteAllDmnDeployments() {
        DmnEngineConfiguration dmnEngineConfiguration = (DmnEngineConfiguration) flowableRule.getProcessEngine().getProcessEngineConfiguration().getEngineConfigurations()
            .get(EngineConfigurationConstants.KEY_DMN_ENGINE_CONFIG);
        dmnEngineConfiguration.getDmnRepositoryService().createDeploymentQuery().list().stream()
            .forEach(
                deployment -> dmnEngineConfiguration.getDmnRepositoryService().deleteDeployment(deployment.getId())
            );
        
        CmmnEngineConfiguration cmmnEngineConfiguration = (CmmnEngineConfiguration) processEngineConfiguration.getEngineConfigurations().get(EngineConfigurationConstants.KEY_CMMN_ENGINE_CONFIG);
        cmmnEngineConfiguration.getCmmnRepositoryService().createDeploymentQuery().list().stream()
            .forEach(
                deployment -> cmmnEngineConfiguration.getCmmnRepositoryService().deleteDeployment(deployment.getId(), true)
            );
    }
}

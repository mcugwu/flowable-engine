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
package org.flowable.cmmn.test.itemcontrol;

import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.ACTIVE;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.AVAILABLE;
import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import org.flowable.cmmn.api.runtime.CaseInstance;
import org.flowable.cmmn.api.runtime.PlanItemInstance;
import org.flowable.cmmn.engine.test.CmmnDeployment;
import org.flowable.cmmn.engine.test.FlowableCmmnTestCase;
import org.junit.Test;

/**
 * Tests combinations of repetition with a collection variable.
 *
 * @author Micha Kiener
 */
public class PlanItemRepetitionWithCollectionVariableTest extends FlowableCmmnTestCase {

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/itemcontrol/PlanItemRepetitionWithCollectionVariableTest.multipleTests.cmmn")
    public void testRepetitionOnCollectionTriggeredByUserTask() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("repetitionWithCollectionVariableTestOne").start();

        List<PlanItemInstance> planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(3, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);

        List<String> taskOutputList = Arrays.asList("A", "B", "C", "D");

        // complete Task A by providing the collection used for repetition
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(getPlanItemInstanceIdByNameAndState(planItemInstances, "Task A", ACTIVE))
            .variable("taskOutputList", taskOutputList)
            .trigger();

        // now we need to have 4 instances of Task B with adequate local variables
        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(7, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", ACTIVE, ACTIVE, ACTIVE, ACTIVE, AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);

        assertPlanItemLocalVariables(caseInstance.getId(), "Task B", taskOutputList);

        // now let's complete all Tasks B -> nothing must happen additionally
        List<PlanItemInstance> tasks = cmmnRuntimeService.createPlanItemInstanceQuery()
            .caseInstanceId(caseInstance.getId())
            .planItemInstanceName("Task B")
            .planItemInstanceStateActive()
            .orderByCreateTime().asc()
            .list();

        assertEquals(4, tasks.size());
        for (PlanItemInstance task : tasks) {
            cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        }

        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(3, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/itemcontrol/PlanItemRepetitionWithCollectionVariableTest.multipleTests.cmmn")
    public void testRepetitionOnCollectionTriggeredByUserTaskSeveralTimes() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("repetitionWithCollectionVariableTestOne").start();

        List<PlanItemInstance> planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(3, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);

        List<String> taskOutputList = Arrays.asList("A", "B", "C", "D");

        // complete Task A by providing the collection used for repetition
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(getPlanItemInstanceIdByNameAndState(planItemInstances, "Task A", ACTIVE))
            .variable("taskOutputList", taskOutputList)
            .trigger();

        // now we need to have 4 instances of Task B with adequate local variables
        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(7, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", ACTIVE, ACTIVE, ACTIVE, ACTIVE, AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);

        assertPlanItemLocalVariables(caseInstance.getId(), "Task B", taskOutputList);

        // complete all active tasks
        completePlanItems(caseInstance.getId(), "Task B", 4);
        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(3, planItemInstances.size());

        taskOutputList = Arrays.asList("E", "F");

        // complete Task A again by providing a different collection used for repetition
        cmmnRuntimeService.createPlanItemInstanceTransitionBuilder(getPlanItemInstanceIdByNameAndState(planItemInstances, "Task A", ACTIVE))
            .variable("taskOutputList", taskOutputList)
            .trigger();

        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(5, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", ACTIVE, ACTIVE, AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);

        assertPlanItemLocalVariables(caseInstance.getId(), "Task B", taskOutputList);

        // now let's complete all Tasks B -> nothing must happen additionally
        completePlanItems(caseInstance.getId(), "Task B", 2);

        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(3, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);
    }

    @Test
    @CmmnDeployment(resources = "org/flowable/cmmn/test/itemcontrol/PlanItemRepetitionWithCollectionVariableTest.multipleTests.cmmn")
    public void testRepetitionOnCollectionTriggeredByCollectionVariableSet() {
        CaseInstance caseInstance = cmmnRuntimeService.createCaseInstanceBuilder().caseDefinitionKey("repetitionWithCollectionVariableTestOne").start();

        List<PlanItemInstance> planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(3, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", AVAILABLE);

        List<String> myCollection = Arrays.asList("A", "B", "C", "D");

        // set the collection variable to kick-off the creation of the Task C repetition
        cmmnRuntimeService.setVariable(caseInstance.getId(), "myCollection", myCollection);

        // now we need to have 4 instances of Task C with adequate local variables
        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(6, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", ACTIVE, ACTIVE, ACTIVE, ACTIVE);

        assertPlanItemLocalVariables(caseInstance.getId(), "Task C", myCollection);

        // if we change the collection variable, nothing else must happen
        cmmnRuntimeService.setVariable(caseInstance.getId(), "myCollection", Arrays.asList("foo"));
        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(6, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", ACTIVE, ACTIVE, ACTIVE, ACTIVE);

        // even if we remove the variable completely, nothing else must happen
        cmmnRuntimeService.removeVariable(caseInstance.getId(), "myCollection");
        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(6, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertPlanItemInstanceState(planItemInstances, "Task C", ACTIVE, ACTIVE, ACTIVE, ACTIVE);

        // now let's complete all Tasks C -> nothing must happen additionally
        List<PlanItemInstance> tasks = cmmnRuntimeService.createPlanItemInstanceQuery()
            .caseInstanceId(caseInstance.getId())
            .planItemInstanceName("Task C")
            .planItemInstanceStateActive()
            .orderByCreateTime().asc()
            .list();

        assertEquals(4, tasks.size());
        for (PlanItemInstance task : tasks) {
            cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        }

        planItemInstances = getPlanItemInstances(caseInstance.getId());
        assertEquals(2, planItemInstances.size());
        assertPlanItemInstanceState(planItemInstances, "Task A", ACTIVE);
        assertPlanItemInstanceState(planItemInstances, "Task B", AVAILABLE);
        assertNoPlanItemInstance(planItemInstances, "Task C");
    }

    protected void assertPlanItemLocalVariables(String caseInstanceId, String planItemName, List<?> itemVariableValues) {
        List<PlanItemInstance> tasks = cmmnRuntimeService.createPlanItemInstanceQuery()
            .caseInstanceId(caseInstanceId)
            .planItemInstanceName(planItemName)
            .planItemInstanceStateActive()
            .orderByCreateTime().asc()
            .list();

        assertEquals(itemVariableValues.size(), tasks.size());
        for (int ii = 0; ii < tasks.size(); ii++) {
            PlanItemInstance task = tasks.get(ii);

            Object itemValue = cmmnRuntimeService.getLocalVariable(task.getId(), "item");
            Object itemIndexValue = cmmnRuntimeService.getLocalVariable(task.getId(), "itemIndex");
            assertEquals(itemValue, itemVariableValues.get(ii));
            assertEquals(itemIndexValue, ii);
        }
    }

    protected void completePlanItems(String caseInstanceId, String planItemName, int expectedCount) {
        // now let's complete all Tasks B -> nothing must happen additionally
        List<PlanItemInstance> tasks = cmmnRuntimeService.createPlanItemInstanceQuery()
            .caseInstanceId(caseInstanceId)
            .planItemInstanceName(planItemName)
            .planItemInstanceStateActive()
            .orderByCreateTime().asc()
            .list();

        assertEquals(expectedCount, tasks.size());
        for (PlanItemInstance task : tasks) {
            cmmnRuntimeService.triggerPlanItemInstance(task.getId());
        }
    }
}

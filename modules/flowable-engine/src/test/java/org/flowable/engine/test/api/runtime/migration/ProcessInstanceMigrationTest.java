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

package org.flowable.engine.test.api.runtime.migration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Comparator;
import java.util.List;

import org.flowable.engine.history.HistoricActivityInstance;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.repository.Deployment;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.runtime.Execution;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.task.api.Task;
import org.flowable.task.api.history.HistoricTaskInstance;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

/**
 * @author Dennis Federico
 */
public class ProcessInstanceMigrationTest extends PluggableFlowableTestCase {

    @AfterEach
    protected void tearDown() throws Exception {
        deleteDeployments();
    }

    public ProcessDefinition deployProcessDefinition(String name, String path) {
        Deployment deployment = repositoryService.createDeployment()
            .name(name)
            .addClasspathResource(path)
            .deploy();

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery()
            .deploymentId(deployment.getId()).singleResult();

        return processDefinition;
    }

    @Test
    public void testSimpleMigrationWithActivityAutoMapping() {
        //Deploy first version of the process
        ProcessDefinition version1ProcessDef = deployProcessDefinition("my deploy", "org/flowable/engine/test/api/runtime/migration/MyProcess-v1.bpmn20.xml");

        //Start and instance of the recent first version of the process for migration and one for reference
        ProcessInstance processInstanceToMigrate = runtimeService.startProcessInstanceByKey("MP");

        //Deploy second version of the process
        ProcessDefinition version2ProcessDef = deployProcessDefinition("my deploy", "org/flowable/engine/test/api/runtime/migration/MyProcess-v2.bpmn20.xml");

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("MP")
            .processDefinitionWithoutTenantId()
            .list();

        assertEquals(2, processDefinitions.size());
        processDefinitions.sort(Comparator.comparingInt(ProcessDefinition::getVersion));
        assertEquals(processDefinitions.get(0).getId(), version1ProcessDef.getId());
        assertEquals(processDefinitions.get(1).getId(), version2ProcessDef.getId());

        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceToMigrate.getId()).list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version1ProcessDef.getId(), e.getProcessDefinitionId()));

        List<Task> tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version1ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasks.get(0).getTaskDefinitionKey());

        //Migrate process
        runtimeService.createProcessInstanceMigrationBuilder()
            .migrateToProcessDefinition(version2ProcessDef.getId())
            .migrate(processInstanceToMigrate.getId());

        executions = runtimeService.createExecutionQuery().list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version2ProcessDef.getId(), e.getProcessDefinitionId()));

        tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version2ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasks.get(0).getTaskDefinitionKey()); //AutoMapped by Id

        //The first process version only had one activity, there should be a second activity in the process now
        taskService.complete(tasks.get(0).getId());
        tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals("userTask2Id", tasks.get(0).getTaskDefinitionKey());
        taskService.complete(tasks.get(0).getId());
        assertProcessEnded(processInstanceToMigrate.getId());

    }

    @Test
    public void testSimpleMigrationWithExplicitActivityMapping1() {
        //Deploy first version of the process
        Deployment oneActivityProcessDeployment = repositoryService.createDeployment()
            .name("My Process Deployment")
            .addClasspathResource("org/flowable/engine/test/api/runtime/migration/MyProcess-v1.bpmn20.xml")
            .deploy();

        //Start and instance of the recent first version of the process for migration and one for reference
        ProcessInstance processInstanceToMigrate = runtimeService.startProcessInstanceByKey("MP");

        //Deploy second version of the process
        Deployment twoActivitiesProcessDeployment = repositoryService.createDeployment()
            .name("My Process Deployment")
            .addClasspathResource("org/flowable/engine/test/api/runtime/migration/MyProcess-v2.bpmn20.xml")
            .deploy();

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("MP")
            .processDefinitionWithoutTenantId()
            .list();

        assertEquals(2, processDefinitions.size());

        ProcessDefinition version1ProcessDef = processDefinitions.stream().filter(d -> d.getVersion() == 1).findFirst().get();
        assertEquals(oneActivityProcessDeployment.getId(), version1ProcessDef.getDeploymentId());
        ProcessDefinition version2ProcessDef = processDefinitions.stream().filter(d -> d.getVersion() == 2).findFirst().get();
        assertEquals(twoActivitiesProcessDeployment.getId(), version2ProcessDef.getDeploymentId());

        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceToMigrate.getId()).list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version1ProcessDef.getId(), e.getProcessDefinitionId()));

        List<Task> tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version1ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasks.get(0).getTaskDefinitionKey());

        //Migrate process - moving the current execution explicitly
        runtimeService.createProcessInstanceMigrationBuilder()
            .migrateToProcessDefinition(version2ProcessDef.getId())
            .addActivityMigrationMapping("userTask1Id", "userTask1Id")
            .migrate(processInstanceToMigrate.getId());

        executions = runtimeService.createExecutionQuery().list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version2ProcessDef.getId(), e.getProcessDefinitionId()));

        tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version2ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasks.get(0).getTaskDefinitionKey());

        //This new process definition has two activities
        taskService.complete(tasks.get(0).getId());
        tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version2ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask2Id", tasks.get(0).getTaskDefinitionKey());
        taskService.complete(tasks.get(0).getId());
        assertProcessEnded(processInstanceToMigrate.getId());

    }

    @Test
    public void testSimpleMigrationWithExplicitActivityMapping2() {
        //Deploy first version of the process
        Deployment oneActivityProcessDeployment = repositoryService.createDeployment()
            .name("My Process Deployment")
            .addClasspathResource("org/flowable/engine/test/api/runtime/migration/MyProcess-v1.bpmn20.xml")
            .deploy();

        //Start and instance of the recent first version of the process for migration and one for reference
        ProcessInstance processInstanceToMigrate = runtimeService.startProcessInstanceByKey("MP");

        //Deploy second version of the process
        Deployment twoActivitiesProcessDeployment = repositoryService.createDeployment()
            .name("My Process Deployment")
            .addClasspathResource("org/flowable/engine/test/api/runtime/migration/MyProcess-v2.bpmn20.xml")
            .deploy();

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("MP")
            .processDefinitionWithoutTenantId()
            .list();

        assertEquals(2, processDefinitions.size());

        ProcessDefinition version1ProcessDef = processDefinitions.stream().filter(d -> d.getVersion() == 1).findFirst().get();
        assertEquals(oneActivityProcessDeployment.getId(), version1ProcessDef.getDeploymentId());
        ProcessDefinition version2ProcessDef = processDefinitions.stream().filter(d -> d.getVersion() == 2).findFirst().get();
        assertEquals(twoActivitiesProcessDeployment.getId(), version2ProcessDef.getDeploymentId());

        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceToMigrate.getId()).list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version1ProcessDef.getId(), e.getProcessDefinitionId()));

        List<Task> tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version1ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasks.get(0).getTaskDefinitionKey());

        //Migrate process - moving the current execution explicitly
        runtimeService.createProcessInstanceMigrationBuilder()
            .migrateToProcessDefinition(version2ProcessDef.getId())
            .addActivityMigrationMapping("userTask1Id", "userTask2Id")
            .migrate(processInstanceToMigrate.getId());

        executions = runtimeService.createExecutionQuery().list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version2ProcessDef.getId(), e.getProcessDefinitionId()));

        tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version2ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask2Id", tasks.get(0).getTaskDefinitionKey());

        //This new process definition has two activities, but we have mapped to the last activity explicitely
        taskService.complete(tasks.get(0).getId());
        assertProcessEnded(processInstanceToMigrate.getId());

    }

    @Test
    public void testSimpleMigrationWithExplicitActivityMapping3() {
        //Deploy first version of the process
        Deployment twoActivitiesProcessDeployment = repositoryService.createDeployment()
            .name("My Process Deployment")
            .addClasspathResource("org/flowable/engine/test/api/runtime/migration/MyProcess-v2.bpmn20.xml")
            .deploy();

        //Start and instance of the recent first version of the process for migration and one for reference
        ProcessInstance processInstanceToMigrate = runtimeService.startProcessInstanceByKey("MP");

        //Deploy second version of the process
        Deployment oneActivityProcessDeployment = repositoryService.createDeployment()
            .name("My Process Deployment")
            .addClasspathResource("org/flowable/engine/test/api/runtime/migration/MyProcess-v1.bpmn20.xml")
            .deploy();

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("MP")
            .processDefinitionWithoutTenantId()
            .list();

        assertEquals(2, processDefinitions.size());

        ProcessDefinition version1ProcessDef = processDefinitions.stream().filter(d -> d.getVersion() == 1).findFirst().get();
        assertEquals(twoActivitiesProcessDeployment.getId(), version1ProcessDef.getDeploymentId());
        ProcessDefinition version2ProcessDef = processDefinitions.stream().filter(d -> d.getVersion() == 2).findFirst().get();
        assertEquals(oneActivityProcessDeployment.getId(), version2ProcessDef.getDeploymentId());

        List<Execution> executions = runtimeService.createExecutionQuery().processInstanceId(processInstanceToMigrate.getId()).list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version1ProcessDef.getId(), e.getProcessDefinitionId()));

        List<Task> tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version1ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasks.get(0).getTaskDefinitionKey());

        //We want to migrate from the next activity
        taskService.complete(tasks.get(0).getId());
        tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals(version1ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());
        assertEquals("userTask2Id", tasks.get(0).getTaskDefinitionKey());

        //Migrate process - moving the current execution explicitly
        runtimeService.createProcessInstanceMigrationBuilder()
            .migrateToProcessDefinition(version2ProcessDef.getId())
            .addActivityMigrationMapping("userTask2Id", "userTask1Id")
            .migrate(processInstanceToMigrate.getId());

        executions = runtimeService.createExecutionQuery().list();
        assertEquals(2, executions.size()); //includes root execution
        executions.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version2ProcessDef.getId(), e.getProcessDefinitionId()));

        tasks = taskService.createTaskQuery().list();
        assertEquals(1, tasks.size());
        assertEquals("userTask1Id", tasks.get(0).getTaskDefinitionKey());
        assertEquals(version2ProcessDef.getId(), tasks.get(0).getProcessDefinitionId());

        //This new process version only have one activity
        taskService.complete(tasks.get(0).getId());
        assertProcessEnded(processInstanceToMigrate.getId());

    }

    @Test
    public void testUserTaskDirectMigration() {
        //Deploy first version of the process
        ProcessDefinition version1ProcessDef = deployProcessDefinition("my deploy", "org/flowable/engine/test/api/runtime/migration/MyProcess-v1.bpmn20.xml");

        //Start and instance of the recent first version of the process for migration and one for reference
        ProcessInstance processInstanceToMigrate = runtimeService.startProcessInstanceByKey("MP");

        //Deploy second version of the process
        ProcessDefinition version2ProcessDef = deployProcessDefinition("my deploy", "org/flowable/engine/test/api/runtime/migration/MyProcess-v2.bpmn20.xml");

        List<ProcessDefinition> processDefinitions = repositoryService.createProcessDefinitionQuery()
            .processDefinitionKey("MP")
            .processDefinitionWithoutTenantId()
            .list();

        assertEquals(2, processDefinitions.size());
        processDefinitions.sort(Comparator.comparingInt(ProcessDefinition::getVersion));
        assertEquals(processDefinitions.get(0).getId(), version1ProcessDef.getId());
        assertEquals(processDefinitions.get(1).getId(), version2ProcessDef.getId());

        List<Execution> executionsBefore = runtimeService.createExecutionQuery().processInstanceId(processInstanceToMigrate.getId()).list();
        assertEquals(2, executionsBefore.size()); //includes root execution
        executionsBefore.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version1ProcessDef.getId(), e.getProcessDefinitionId()));

        List<Task> tasksBefore = taskService.createTaskQuery().list();
        assertEquals(1, tasksBefore.size());
        assertEquals(version1ProcessDef.getId(), tasksBefore.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasksBefore.get(0).getTaskDefinitionKey());

        List<HistoricActivityInstance> historicActivityInstancesBefore = historyService.createHistoricActivityInstanceQuery().orderByExecutionId().asc().list();
        List<HistoricTaskInstance> historicTaskInstancesBefore = historyService.createHistoricTaskInstanceQuery().orderByExecutionId().asc().list();

        //Migrate process
        runtimeService.createProcessInstanceMigrationBuilder()
            .migrateToProcessDefinition(version2ProcessDef.getId())
            .migrate(processInstanceToMigrate.getId());

        List<Execution> executionsAfter = runtimeService.createExecutionQuery().list();
        assertEquals(2, executionsAfter.size()); //includes root execution
        executionsAfter.stream()
            .map(e -> (ExecutionEntity) e)
            .forEach(e -> assertEquals(version2ProcessDef.getId(), e.getProcessDefinitionId()));

        List<Task> tasksAfter = taskService.createTaskQuery().list();
        assertEquals(1, tasksAfter.size());
        assertEquals(version2ProcessDef.getId(), tasksAfter.get(0).getProcessDefinitionId());
        assertEquals("userTask1Id", tasksAfter.get(0).getTaskDefinitionKey()); //AutoMapped by Id

        List<HistoricActivityInstance> historicActivityInstancesAfter = historyService.createHistoricActivityInstanceQuery().orderByExecutionId().asc().list();
        List<HistoricTaskInstance> historicTaskInstancesAfter = historyService.createHistoricTaskInstanceQuery().orderByExecutionId().asc().list();

        assertEquals(historicActivityInstancesBefore.size(), historicActivityInstancesAfter.size());
        assertThat(historicActivityInstancesBefore)
            .usingElementComparatorIgnoringFields("revision", "processDefinitionId")
            .containsExactlyInAnyOrderElementsOf(historicActivityInstancesAfter);
        assertEquals(historicTaskInstancesBefore.size(), historicTaskInstancesAfter.size());
        assertThat(historicTaskInstancesBefore)
            .usingElementComparatorIgnoringFields("revision", "processDefinitionId", "originalPersistentState", "lastUpdateTime")
            .containsExactlyInAnyOrderElementsOf(historicTaskInstancesAfter);

        //The first process version only had one activity, there should be a second activity in the process now
        taskService.complete(tasksAfter.get(0).getId());
        tasksAfter = taskService.createTaskQuery().list();
        assertEquals(1, tasksAfter.size());
        assertEquals("userTask2Id", tasksAfter.get(0).getTaskDefinitionKey());
        taskService.complete(tasksAfter.get(0).getId());
        assertProcessEnded(processInstanceToMigrate.getId());
    }
}

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
package org.flowable.examples.taskforms;

import java.util.HashMap;
import java.util.Map;

import org.flowable.common.engine.impl.util.CollectionUtil;
import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.repository.ProcessDefinition;
import org.flowable.engine.test.Deployment;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class TaskFormsTest extends PluggableFlowableTestCase {

    @Override
    public void setUp() throws Exception {
        identityService.saveUser(identityService.newUser("fozzie"));
        identityService.saveGroup(identityService.newGroup("management"));
        identityService.createMembership("fozzie", "management");
    }

    @Override
    public void tearDown() throws Exception {
        identityService.deleteGroup("management");
        identityService.deleteUser("fozzie");
    }

    @Deployment(resources = { "org/flowable/examples/taskforms/VacationRequest_deprecated_forms.bpmn20.xml", "org/flowable/examples/taskforms/approve.form",
            "org/flowable/examples/taskforms/request.form", "org/flowable/examples/taskforms/adjustRequest.form" })
    public void testTaskFormsWithVacationRequestProcess() {

        // Get start form
        String procDefId = repositoryService.createProcessDefinitionQuery().singleResult().getId();
        Object startForm = formService.getRenderedStartForm(procDefId);
        assertNotNull(startForm);

        ProcessDefinition processDefinition = repositoryService.createProcessDefinitionQuery().singleResult();
        String processDefinitionId = processDefinition.getId();
        assertEquals("org/flowable/examples/taskforms/request.form", formService.getStartFormData(processDefinitionId).getFormKey());

        // Define variables that would be filled in through the form
        Map<String, String> formProperties = new HashMap<>();
        formProperties.put("employeeName", "kermit");
        formProperties.put("numberOfDays", "4");
        formProperties.put("vacationMotivation", "I'm tired");
        formService.submitStartFormData(procDefId, formProperties);

        // Management should now have a task assigned to them
        org.flowable.task.api.Task task = taskService.createTaskQuery().taskCandidateGroup("management").singleResult();
        assertEquals("Vacation request by kermit", task.getDescription());
        Object taskForm = formService.getRenderedTaskForm(task.getId());
        assertNotNull(taskForm);

        // Rejecting the task should put the process back to first task
        taskService.complete(task.getId(), CollectionUtil.singletonMap("vacationApproved", "false"));
        task = taskService.createTaskQuery().singleResult();
        assertEquals("Adjust vacation request", task.getName());
    }

    @Deployment
    public void testTaskFormUnavailable() {
        String procDefId = repositoryService.createProcessDefinitionQuery().singleResult().getId();
        assertNull(formService.getRenderedStartForm(procDefId));

        runtimeService.startProcessInstanceByKey("noStartOrTaskForm");
        org.flowable.task.api.Task task = taskService.createTaskQuery().singleResult();
        assertNull(formService.getRenderedTaskForm(task.getId()));
    }

}

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

package org.flowable.engine.test.bpmn.usertask;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.engine.impl.test.PluggableFlowableTestCase;
import org.flowable.engine.runtime.ProcessInstance;
import org.flowable.engine.test.Deployment;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.node.ObjectNode;

/**
 * @author Tijs Rademakers
 */
public class DynamicUserTaskTest extends PluggableFlowableTestCase {

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/usertask/DynamicUserTaskTest.assignment.bpmn20.xml" })
    public void testChangeAssignee() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getAssignee()).isEqualTo("test");
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskAssignee("task1", "test2");
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getAssignee()).isEqualTo("test2");
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/usertask/DynamicUserTaskTest.assignment.bpmn20.xml" })
    public void testChangeOwner() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getOwner()).isEqualTo("ownerTest");
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskOwner("task1", "ownerTest2");
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getOwner()).isEqualTo("ownerTest2");
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/usertask/DynamicUserTaskTest.basictask.bpmn20.xml" })
    public void testChangeCandidateUsers() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        List<IdentityLink> taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        boolean candidateUserTestFound = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getUserId() != null && identityLink.getGroupId() == null) {
                if ("test".equals(identityLink.getUserId())) {
                    candidateUserTestFound = true;
                }
            }
        }
        assertThat(candidateUserTestFound).isFalse();
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskCandidateUser("task1", "test", true);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        candidateUserTestFound = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getUserId() != null && identityLink.getGroupId() == null) {
                if ("test".equals(identityLink.getUserId())) {
                    candidateUserTestFound = true;
                }
            }
        }
        assertThat(candidateUserTestFound).isTrue();
        completeTask(task);

        infoNode = dynamicBpmnService.getProcessDefinitionInfo(processDefinitionId);
        dynamicBpmnService.changeUserTaskCandidateUser("task1", "test2", false, infoNode);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        candidateUserTestFound = false;
        boolean candidateUserTest2Found = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getUserId() != null && identityLink.getGroupId() == null) {
                if ("test".equals(identityLink.getUserId())) {
                    candidateUserTestFound = true;
                } else if ("test2".equals(identityLink.getUserId())) {
                    candidateUserTest2Found = true;
                }
            }
        }
        assertThat(candidateUserTestFound).isTrue();
        assertThat(candidateUserTest2Found).isTrue();
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/usertask/DynamicUserTaskTest.basictask.bpmn20.xml" })
    public void testChangeCandidateGroups() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        List<IdentityLink> taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        boolean candidateGroupTestFound = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getGroupId() != null && identityLink.getUserId() == null) {
                if ("test".equals(identityLink.getGroupId())) {
                    candidateGroupTestFound = true;
                }
            }
        }
        assertThat(candidateGroupTestFound).isFalse();
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskCandidateGroup("task1", "test", true);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        candidateGroupTestFound = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getGroupId() != null && identityLink.getUserId() == null) {
                if ("test".equals(identityLink.getGroupId())) {
                    candidateGroupTestFound = true;
                }
            }
        }
        assertThat(candidateGroupTestFound).isTrue();
        completeTask(task);

        infoNode = dynamicBpmnService.getProcessDefinitionInfo(processDefinitionId);
        dynamicBpmnService.changeUserTaskCandidateGroup("task1", "test2", false, infoNode);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        candidateGroupTestFound = false;
        boolean candidateGroupTest2Found = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getGroupId() != null && identityLink.getUserId() == null) {
                if ("test".equals(identityLink.getGroupId())) {
                    candidateGroupTestFound = true;
                } else if ("test2".equals(identityLink.getGroupId())) {
                    candidateGroupTest2Found = true;
                }
            }
        }
        assertThat(candidateGroupTestFound).isTrue();
        assertThat(candidateGroupTest2Found).isTrue();
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/usertask/DynamicUserTaskTest.basictask.bpmn20.xml" })
    public void testChangeCandidateUsersAndGroups() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        List<IdentityLink> taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        boolean candidateUserTestFound = false;
        boolean candidateGroupTestFound = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getUserId() != null && identityLink.getGroupId() == null) {
                if ("test".equals(identityLink.getUserId())) {
                    candidateUserTestFound = true;
                }
            } else if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getGroupId() != null && identityLink.getUserId() == null) {
                if ("test".equals(identityLink.getGroupId())) {
                    candidateGroupTestFound = true;
                }
            }
        }
        assertThat(candidateUserTestFound).isFalse();
        assertThat(candidateGroupTestFound).isFalse();
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskCandidateGroup("task1", "test", true);
        dynamicBpmnService.changeUserTaskCandidateUser("task1", "test", true, infoNode);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        candidateUserTestFound = false;
        candidateGroupTestFound = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getUserId() != null && identityLink.getGroupId() == null) {
                if ("test".equals(identityLink.getUserId())) {
                    candidateUserTestFound = true;
                }
            } else if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getGroupId() != null && identityLink.getUserId() == null) {
                if ("test".equals(identityLink.getGroupId())) {
                    candidateGroupTestFound = true;
                }
            }
        }
        assertThat(candidateUserTestFound).isTrue();
        assertThat(candidateGroupTestFound).isTrue();
        completeTask(task);

        infoNode = dynamicBpmnService.getProcessDefinitionInfo(processDefinitionId);
        dynamicBpmnService.changeUserTaskCandidateGroup("task1", "test2", false, infoNode);
        dynamicBpmnService.changeUserTaskCandidateUser("task1", "test2", false, infoNode);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        taskIdentityLinks = taskService.getIdentityLinksForTask(task.getId());
        candidateUserTestFound = false;
        boolean candidateUserTestFound2 = false;
        candidateGroupTestFound = false;
        boolean candidateGroupTest2Found = false;
        for (IdentityLink identityLink : taskIdentityLinks) {
            if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getUserId() != null && identityLink.getGroupId() == null) {
                if ("test".equals(identityLink.getUserId())) {
                    candidateUserTestFound = true;
                } else if ("test2".equals(identityLink.getUserId())) {
                    candidateUserTestFound2 = true;
                }
            } else if (IdentityLinkType.CANDIDATE.equals(identityLink.getType()) && identityLink.getGroupId() != null && identityLink.getUserId() == null) {
                if ("test".equals(identityLink.getGroupId())) {
                    candidateGroupTestFound = true;
                } else if ("test2".equals(identityLink.getGroupId())) {
                    candidateGroupTest2Found = true;
                }
            }
        }
        assertThat(candidateUserTestFound).isTrue();
        assertThat(candidateUserTestFound2).isTrue();
        assertThat(candidateGroupTestFound).isTrue();
        assertThat(candidateGroupTest2Found).isTrue();
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/usertask/DynamicUserTaskTest.basictask.bpmn20.xml" })
    public void testChangeNameAndDescription() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getName()).isNull();
        assertThat(task.getDescription()).isNull();
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskName("task1", "Task name test");
        dynamicBpmnService.changeUserTaskDescription("task1", "Task description test", infoNode);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getName()).isEqualTo("Task name test");
        assertThat(task.getDescription()).isEqualTo("Task description test");
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment(resources = { "org/flowable/engine/test/bpmn/usertask/DynamicUserTaskTest.assignment.bpmn20.xml" })
    public void testChangePriorityAndCategory() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getPriority()).isEqualTo(50);
        assertThat(task.getCategory()).isNull();
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskPriority("task1", "99");
        dynamicBpmnService.changeUserTaskCategory("task1", "categoryTest", infoNode);
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getPriority()).isEqualTo(99);
        assertThat(task.getCategory()).isEqualTo("categoryTest");
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment
    public void testChangeFormKey() {
        // first test without changing the form key
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getFormKey()).isEqualTo("test");
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskFormKey("task1", "test2");
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask");

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getFormKey()).isEqualTo("test2");
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

    @Test
    @Deployment
    public void testChangeFormKeyWithExpression() {
        // first test without changing the form key
        Map<String, Object> varMap = new HashMap<>();
        varMap.put("start", "test");
        ProcessInstance processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask", varMap);
        String processDefinitionId = processInstance.getProcessDefinitionId();

        org.flowable.task.api.Task task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getFormKey()).isEqualTo("test");
        completeTask(task);

        assertProcessEnded(processInstance.getId());

        // now test with changing the form key
        ObjectNode infoNode = dynamicBpmnService.changeUserTaskFormKey("task1", "${anotherKey}");
        dynamicBpmnService.saveProcessDefinitionInfo(processDefinitionId, infoNode);

        varMap = new HashMap<>();
        varMap.put("anotherKey", "test2");
        processInstance = runtimeService.startProcessInstanceByKey("dynamicUserTask", varMap);

        task = taskService.createTaskQuery().processInstanceId(processInstance.getId()).singleResult();
        assertThat(task.getFormKey()).isEqualTo("test2");
        completeTask(task);

        assertProcessEnded(processInstance.getId());
    }

}

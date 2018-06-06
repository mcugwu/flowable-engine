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

package org.flowable.cmmn.engine.impl.cfg;

import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.impl.task.TaskHelper;
import org.flowable.cmmn.engine.impl.util.IdentityLinkUtil;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.api.IdentityLinkType;
import org.flowable.identitylink.service.impl.persistence.entity.IdentityLinkEntity;
import org.flowable.task.api.Task;
import org.flowable.task.service.InternalTaskAssignmentManager;
import org.flowable.task.service.impl.persistence.entity.TaskEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author martin.grofcik
 */
public class DefaultTaskAssignmentManager implements InternalTaskAssignmentManager {
    
    protected CmmnEngineConfiguration cmmnEngineConfiguration;
    protected final String parentIdentityLinkType;

    public DefaultTaskAssignmentManager(CmmnEngineConfiguration cmmnEngineConfiguration) {
        this(cmmnEngineConfiguration, IdentityLinkType.PARTICIPANT);
    }

    public DefaultTaskAssignmentManager(CmmnEngineConfiguration cmmnEngineConfiguration, String parentIdentityLinkType) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
        this.parentIdentityLinkType = parentIdentityLinkType;
    }

    @Override
    public void changeAssignee(Task task, String assignee) {
        TaskHelper.changeTaskAssignee((TaskEntity) task, assignee, parentIdentityLinkType);
    }
    
    @Override
    public void changeOwner(Task task, String owner) {
        TaskHelper.changeTaskOwner((TaskEntity) task, owner, parentIdentityLinkType);
    }

    @Override
    public void addCandidateUser(Task task, IdentityLink identityLink) {
        IdentityLinkUtil.handleTaskIdentityLinkAddition((TaskEntity) task, (IdentityLinkEntity) identityLink, parentIdentityLinkType);
    }

    @Override
    public void addCandidateUsers(Task task, List<IdentityLink> candidateUsers) {
        List<IdentityLinkEntity> identityLinks = new ArrayList<>();
        for (IdentityLink identityLink : candidateUsers) {
            identityLinks.add((IdentityLinkEntity) identityLink);
        }

        IdentityLinkUtil.handleTaskIdentityLinkAdditions((TaskEntity) task, identityLinks, parentIdentityLinkType);
    }

    @Override
    public void addCandidateGroup(Task task, IdentityLink identityLink) {
        IdentityLinkUtil.handleTaskIdentityLinkAddition((TaskEntity) task, (IdentityLinkEntity) identityLink, parentIdentityLinkType);
    }

    @Override
    public void addCandidateGroups(Task task, List<IdentityLink> candidateGroups) {
        List<IdentityLinkEntity> identityLinks = new ArrayList<>();
        for (IdentityLink identityLink : candidateGroups) {
            identityLinks.add((IdentityLinkEntity) identityLink);
        }
        IdentityLinkUtil.handleTaskIdentityLinkAdditions((TaskEntity) task, identityLinks, parentIdentityLinkType);
    }

    @Override
    public void addUserIdentityLink(Task task, IdentityLink identityLink) {
        IdentityLinkUtil.handleTaskIdentityLinkAddition((TaskEntity) task, (IdentityLinkEntity) identityLink, parentIdentityLinkType);
    }

    @Override
    public void addGroupIdentityLink(Task task, IdentityLink identityLink) {
        IdentityLinkUtil.handleTaskIdentityLinkAddition((TaskEntity) task, (IdentityLinkEntity) identityLink, parentIdentityLinkType);
    }

    @Override
    public void deleteUserIdentityLink(Task task, IdentityLink identityLink) {
        List<IdentityLinkEntity> identityLinks = new ArrayList<>();
        identityLinks.add((IdentityLinkEntity) identityLink);
        IdentityLinkUtil.handleTaskIdentityLinkDeletions((TaskEntity) task, identityLinks, true);
    }

    @Override
    public void deleteGroupIdentityLink(Task task, IdentityLink identityLink) {
        List<IdentityLinkEntity> identityLinks = new ArrayList<>();
        identityLinks.add((IdentityLinkEntity) identityLink);
        IdentityLinkUtil.handleTaskIdentityLinkDeletions((TaskEntity) task, identityLinks, true);
    }
}

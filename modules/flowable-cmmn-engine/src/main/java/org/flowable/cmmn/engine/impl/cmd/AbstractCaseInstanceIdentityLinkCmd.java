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
package org.flowable.cmmn.engine.impl.cmd;

import java.util.ArrayList;
import java.util.List;

import org.flowable.cmmn.engine.impl.util.CommandContextUtil;
import org.flowable.common.engine.api.scope.ScopeTypes;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.identitylink.api.IdentityLink;
import org.flowable.identitylink.service.IdentityLinkService;

/**
 * An abstract command supporting functionality around identity link management for case instances.
 *
 * @author Micha Kiener
 */
public abstract class AbstractCaseInstanceIdentityLinkCmd {

    protected void removeIdentityLinkType(CommandContext commandContext, String caseInstanceId, String identityType) {
        List<IdentityLink> linksToRemove = new ArrayList<>(1);
        for (IdentityLink identityLink : getIdentityLinkService(commandContext).findIdentityLinksByScopeIdAndType(caseInstanceId, ScopeTypes.CMMN)) {
            if (identityLink.getType().equalsIgnoreCase(identityType)) {
                linksToRemove.add(identityLink);
            }
        }
        // remove links in a second loop as we might run into possible concurrent modification exceptions to the identity link list
        for (IdentityLink identityLink : linksToRemove) {
            getIdentityLinkService(commandContext).deleteScopeIdentityLink(caseInstanceId, ScopeTypes.CMMN,
                identityLink.getUserId(), identityLink.getGroupId(), identityLink.getType());
        }
    }

    protected IdentityLinkService getIdentityLinkService(CommandContext commandContext) {
        return CommandContextUtil.getIdentityLinkService(commandContext);
    }
}


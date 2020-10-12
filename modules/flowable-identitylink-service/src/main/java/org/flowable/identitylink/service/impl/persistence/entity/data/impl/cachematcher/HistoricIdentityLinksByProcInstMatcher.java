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
package org.flowable.identitylink.service.impl.persistence.entity.data.impl.cachematcher;

import org.flowable.common.engine.impl.persistence.cache.CachedEntityMatcherAdapter;
import org.flowable.identitylink.service.impl.persistence.entity.HistoricIdentityLinkEntity;

/**
 * @author Joram Barrez
 */
public class HistoricIdentityLinksByProcInstMatcher extends CachedEntityMatcherAdapter<HistoricIdentityLinkEntity> {

    @Override
    public boolean isRetained(HistoricIdentityLinkEntity historicIdentityLinkEntity, Object parameter) {
        return historicIdentityLinkEntity.getProcessInstanceId() != null
                && historicIdentityLinkEntity.getProcessInstanceId().equals(parameter);
    }

}
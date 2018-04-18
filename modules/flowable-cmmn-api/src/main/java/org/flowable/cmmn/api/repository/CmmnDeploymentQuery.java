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

package org.flowable.cmmn.api.repository;

import java.util.List;

import org.flowable.cmmn.api.CmmnRepositoryService;
import org.flowable.common.engine.api.query.Query;

/**
 * Allows programmatic querying of {@link CmmnDeployment}s.
 * 
 * Note that it is impossible to retrieve the deployment resources through the results of this operation, 
 * since that would cause a huge transfer of (possibly) unneeded bytes over the wire.
 * 
 * To retrieve the actual bytes of a deployment resource use the operations on the 
 * {@link CmmnRepositoryService#getDeploymentResourceNames(String)} and
 * {@link CmmnRepositoryService#getResourceAsStream(String, String)}
 * 
 * @author Tijs Rademakers
 * @author Joram Barrez
 */
public interface CmmnDeploymentQuery extends Query<CmmnDeploymentQuery, CmmnDeployment> {

    /**
     * Only select deployments with the given deployment id.
     */
    CmmnDeploymentQuery deploymentId(String deploymentId);
    
    /**
     * Only select deployments with the given deployment ids.
     */
    CmmnDeploymentQuery deploymentIds(List<String> deploymentIds);

    /**
     * Only select deployments with the given name.
     */
    CmmnDeploymentQuery deploymentName(String name);

    /**
     * Only select deployments with a name like the given string.
     */
    CmmnDeploymentQuery deploymentNameLike(String nameLike);

    /**
     * Only select deployments with the given category.
     * 
     * @see CmmnDeploymentBuilder#category(String)
     */
    CmmnDeploymentQuery deploymentCategory(String category);

    /**
     * Only select deployments that have a different category then the given one.
     * 
     * @see CmmnDeploymentBuilder#category(String)
     */
    CmmnDeploymentQuery deploymentCategoryNotEquals(String categoryNotEquals);
    
    /**
     * Only select deployments with the given key.
     * 
     * @see CmmnDeploymentBuilder#key(String)
     */
    CmmnDeploymentQuery deploymentKey(String key);

    /**
     * Only select deployment that have the given tenant id.
     */
    CmmnDeploymentQuery deploymentTenantId(String tenantId);

    /**
     * Only select deployments with a tenant id like the given one.
     */
    CmmnDeploymentQuery deploymentTenantIdLike(String tenantIdLike);

    /**
     * Only select deployments that do not have a tenant id.
     */
    CmmnDeploymentQuery deploymentWithoutTenantId();

    /**
     * Only select deployment that have the given parent deployment id.
     */
    CmmnDeploymentQuery parentDeploymentId(String parentDeploymentId);

    /**
     * Only select deployments with a parent deployment id like the given one.
     */
    CmmnDeploymentQuery parentDeploymentIdLike(String parentDeploymentIdLike);
    
    /**
     * Only select deployments where the deployment time is the latest value. Can only be used together with the deployment key.
     */
    CmmnDeploymentQuery latest();

    // sorting ////////////////////////////////////////////////////////

    /**
     * Order by deployment id (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    CmmnDeploymentQuery orderByDeploymentId();

    /**
     * Order by deployment name (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    CmmnDeploymentQuery orderByDeploymentName();

    /**
     * Order by deployment time (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    CmmnDeploymentQuery orderByDeploymenTime();

    /**
     * Order by tenant id (needs to be followed by {@link #asc()} or {@link #desc()}).
     */
    CmmnDeploymentQuery orderByTenantId();
}

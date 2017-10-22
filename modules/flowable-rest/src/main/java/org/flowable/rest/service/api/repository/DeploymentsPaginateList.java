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

package org.flowable.rest.service.api.repository;

import java.util.List;

import org.flowable.engine.repository.Deployment;
import org.flowable.rest.api.AbstractPaginateList;
import org.flowable.rest.service.api.RestResponseFactory;

/**
 * @author Tijs Rademakers
 */
public class DeploymentsPaginateList extends AbstractPaginateList<DeploymentResponse, Deployment> {

    protected RestResponseFactory restResponseFactory;

    public DeploymentsPaginateList(RestResponseFactory restResponseFactory) {
        this.restResponseFactory = restResponseFactory;
    }

    @Override
    protected List<DeploymentResponse> processList(List<Deployment> list) {
        return restResponseFactory.createDeploymentResponseList(list);
    }
}

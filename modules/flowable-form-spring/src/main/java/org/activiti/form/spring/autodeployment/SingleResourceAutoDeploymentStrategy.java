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

package org.activiti.form.spring.autodeployment;

import java.io.IOException;

import org.activiti.engine.ActivitiException;
import org.activiti.form.api.FormDeploymentBuilder;
import org.activiti.form.api.FormRepositoryService;
import org.springframework.core.io.Resource;

/**
 * Implementation of {@link AutoDeploymentStrategy} that performs a separate deployment for each resource by name.
 * 
 * @author Tiese Barrell
 */
public class SingleResourceAutoDeploymentStrategy extends AbstractAutoDeploymentStrategy {

  /**
   * The deployment mode this strategy handles.
   */
  public static final String DEPLOYMENT_MODE = "single-resource";

  @Override
  protected String getDeploymentMode() {
    return DEPLOYMENT_MODE;
  }

  @Override
  public void deployResources(final String deploymentNameHint, final Resource[] resources, final FormRepositoryService repositoryService) {

    // Create a separate deployment for each resource using the resource name

    for (final Resource resource : resources) {

      final String resourceName = determineResourceName(resource);
      final FormDeploymentBuilder deploymentBuilder = repositoryService.createDeployment().enableDuplicateFiltering().name(resourceName);

      try {
        deploymentBuilder.addInputStream(resourceName, resource.getInputStream());
       
      } catch (IOException e) {
        throw new ActivitiException("couldn't auto deploy resource '" + resource + "': " + e.getMessage(), e);
      }

      deploymentBuilder.deploy();
    }
  }

}

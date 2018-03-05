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
package org.flowable.spring.boot.dmn;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * Properties for configuring the dmn engine.
 *
 * @author Filip Hrisafov
 */
@ConfigurationProperties(prefix = "flowable.dmn")
public class FlowableDmnProperties {

    /**
     * The name of the deployment for the dmn resources.
     */
    private String deploymentName = "SpringBootAutoDeployment";

    /**
     * The location where the dmn resources are located.
     * Default is {@code classpath*:/dmn/}
     */
    private String resourceLocation = "classpath*:/dmn/";

    /**
     * The suffixes for the resources that need to be scanned.
     * Default is {@code **.dmn, **.dmn.xml, **.dmn11, **.dmn11.xml}
     */
    private List<String> resourceSuffixes = Arrays.asList("**.dmn", "**.dmn.xml", "**.dmn11", "**.dmn11.xml");

    /**
     * Whether to perform deployment of resources, default is {@code true}.
     */
    private boolean deployResources = true;

    /**
     * Whether the dmn engine needs to be started.
     */
    private boolean enabled = true;

    public String getDeploymentName() {
        return deploymentName;
    }

    public void setDeploymentName(String deploymentName) {
        this.deploymentName = deploymentName;
    }

    public String getResourceLocation() {
        return resourceLocation;
    }

    public void setResourceLocation(String resourceLocation) {
        this.resourceLocation = resourceLocation;
    }

    public List<String> getResourceSuffixes() {
        return resourceSuffixes;
    }

    public void setResourceSuffixes(List<String> resourceSuffixes) {
        this.resourceSuffixes = resourceSuffixes;
    }

    public boolean isDeployResources() {
        return deployResources;
    }

    public void setDeployResources(boolean deployResources) {
        this.deployResources = deployResources;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

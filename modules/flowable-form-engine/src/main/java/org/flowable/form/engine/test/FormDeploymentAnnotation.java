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
package org.flowable.form.engine.test;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation for a test method to create and delete a deployment around a test method.
 * 
 * <p>
 * Usage:
 * </p>
 * 
 * <pre>
 * package org.example;
 * 
 * ...
 * 
 * public class ExampleTest {
 * 
 *   &#64;DmnDeploymentAnnotation
 *   public void testForADeploymentWithASingleResource() {
 *     // a deployment will be available in the engine repository
 *     // containing the single resource <b>org/example/ExampleTest.testForADeploymentWithASingleResource.bpmn20.xml</b>
 *   }
 * 
 *   &#64;DmnDeploymentAnnotation(resources = { 
 *     "org/example/decisionOne.dmn",
 *     "org/example/decisionTwo.dmn"})
 *   public void testForADeploymentWithASingleResource() {
 *     // a deployment will be available in the engine repository
 *     // containing the three resources
 *   }
 * </pre>
 * 
 * @author Tijs Rademakers
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface FormDeploymentAnnotation {

    /** Specify resources that make up the process definition. */
    public String[] resources() default {};

}

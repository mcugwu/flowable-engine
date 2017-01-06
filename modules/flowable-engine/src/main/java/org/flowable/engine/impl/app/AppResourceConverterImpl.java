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
package org.flowable.engine.impl.app;

import org.flowable.engine.app.AppModel;
import org.flowable.engine.app.AppResourceConverter;
import org.flowable.engine.common.api.FlowableException;

import com.fasterxml.jackson.databind.ObjectMapper;

public class AppResourceConverterImpl implements AppResourceConverter {
  
  protected ObjectMapper objectMapper;

  public AppResourceConverterImpl(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }
  
  public Object convertAppResourceToModel(byte[] appResourceBytes) {
    AppModel appModel;
    try {
      appModel = objectMapper.readValue(appResourceBytes, AppModel.class);
    } catch (Exception e) {
      throw new FlowableException("Error reading app resource", e);
    }
    
    return appModel;
  }
  
}

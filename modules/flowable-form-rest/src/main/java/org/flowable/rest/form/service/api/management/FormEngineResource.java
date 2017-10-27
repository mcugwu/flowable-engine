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
package org.flowable.rest.form.service.api.management;

import io.swagger.annotations.*;
import org.flowable.engine.common.EngineInfo;
import org.flowable.engine.common.api.FlowableException;
import org.flowable.form.engine.FormEngine;
import org.flowable.form.engine.FormEngines;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yvo Swillens
 */
@RestController
@Api(tags = { "Engine" }, description = "Manage Form Engine", authorizations = { @Authorization(value = "basicAuth") })
public class FormEngineResource {

    @ApiOperation(value = "Get form engine info", tags = { "Engine" })
    @ApiResponses(value = {
            @ApiResponse(code = 200, message = "Indicates the engine info is returned."),
    })
    @GetMapping(value = "/form-management/engine", produces = "application/json")
    public FormEngineInfoResponse getEngineInfo() {
        FormEngineInfoResponse response = new FormEngineInfoResponse();

        try {
            FormEngine formEngine = FormEngines.getDefaultFormEngine();
            EngineInfo formEngineInfo = FormEngines.getFormEngineInfo(formEngine.getName());

            if (formEngineInfo != null) {
                response.setName(formEngineInfo.getName());
                response.setResourceUrl(formEngineInfo.getResourceUrl());
                response.setException(formEngineInfo.getException());
            } else {
                response.setName(formEngine.getName());
            }
        } catch (Exception e) {
            throw new FlowableException("Error retrieving form engine info", e);
        }

        response.setVersion(FormEngine.VERSION);

        return response;
    }
}

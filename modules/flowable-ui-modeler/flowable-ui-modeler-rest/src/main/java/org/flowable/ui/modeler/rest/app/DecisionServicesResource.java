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
package org.flowable.ui.modeler.rest.app;

import java.nio.charset.StandardCharsets;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.flowable.ui.common.model.ResultListDataRepresentation;
import org.flowable.ui.modeler.service.FlowableDecisionServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Yvo Swillens
 * @author Tijs Rademakers
 */
@RestController
@RequestMapping("/app/rest/decision-service-models")
public class DecisionServicesResource {

    @Autowired
    protected FlowableDecisionServiceService decisionServiceService;

    @GetMapping(produces = "application/json")
    public ResultListDataRepresentation getDecisionTables(HttpServletRequest request) {
        // need to parse the filterText parameter ourselves, due to encoding issues with the default parsing.
        String filter = null;
        List<NameValuePair> params = URLEncodedUtils.parse(request.getQueryString(), StandardCharsets.UTF_8);
        if (params != null) {
            for (NameValuePair nameValuePair : params) {
                if ("filter".equalsIgnoreCase(nameValuePair.getName())) {
                    filter = nameValuePair.getValue();
                }
            }
        }
        return decisionServiceService.getDecisionServices(filter);
    }
}

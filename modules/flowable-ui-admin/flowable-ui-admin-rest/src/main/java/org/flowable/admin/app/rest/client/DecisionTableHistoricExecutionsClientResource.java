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
package org.flowable.admin.app.rest.client;

import com.fasterxml.jackson.databind.JsonNode;
import org.flowable.admin.domain.EndpointType;
import org.flowable.admin.domain.ServerConfig;
import org.flowable.admin.service.engine.DecisionHistoricExecutionService;
import org.flowable.admin.service.engine.DecisionTableService;
import org.flowable.admin.service.engine.exception.FlowableServiceException;
import org.flowable.app.service.exception.BadRequestException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * @author Yvo Swillens
 */
@RestController
public class DecisionTableHistoricExecutionsClientResource extends AbstractClientResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(DecisionTableHistoricExecutionsClientResource.class);

    @Autowired
    protected DecisionHistoricExecutionService clientService;

    @RequestMapping(value = "/rest/admin/decision-tables/history", method = RequestMethod.GET, produces = "application/json")
    public JsonNode getDecisionTableHistoricExecution(HttpServletRequest request) throws BadRequestException {
        ServerConfig serverConfig = retrieveServerConfig(EndpointType.DMN);
        Map<String, String[]> parameterMap = getRequestParametersWithoutServerId(request);
        return clientService.listHistoricDecisionExecutions(serverConfig, parameterMap);
    }
}

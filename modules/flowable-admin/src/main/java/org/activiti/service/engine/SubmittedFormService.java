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
package org.activiti.service.engine;

import java.util.Map;

import com.fasterxml.jackson.databind.node.ObjectNode;
import org.activiti.domain.ServerConfig;
import org.activiti.service.engine.exception.ActivitiServiceException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * Service for invoking Activiti REST services.
 */
@Service
public class SubmittedFormService {

  @Autowired
  protected ActivitiClientService clientUtil;

  public JsonNode listSubmittedForms(ServerConfig serverConfig, Map<String, String[]> parameterMap) {
    URIBuilder builder = clientUtil.createUriBuilder("/enterprise/submitted-forms");

    for (String name : parameterMap.keySet()) {
      builder.addParameter(name, parameterMap.get(name)[0]);
    }
    HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, builder.toString()));
    return clientUtil.executeRequest(get, serverConfig);
  }

  public JsonNode listFormSubmittedForms(ServerConfig serverConfig, ObjectNode objectNode) {
    JsonNode resultNode = null;

    try {
      URIBuilder builder = clientUtil.createUriBuilder("/query/form-instances");
      HttpPost post = clientUtil.createPost(builder.toString(), serverConfig);
      post.setEntity(clientUtil.createStringEntity(objectNode.toString()));

      resultNode = clientUtil.executeRequest(post, serverConfig);
    } catch (Exception ex) {
      throw new ActivitiServiceException(ex.getMessage(), ex);
    }

    return resultNode;
  }

  public JsonNode getSubmittedForm(ServerConfig serverConfig, String submittedFormId) {
    HttpGet get = new HttpGet(clientUtil.getServerUrl(serverConfig, "/form/form-instance/" + submittedFormId));
    return clientUtil.executeRequest(get, serverConfig);
  }

  public JsonNode getTaskSubmittedForm(ServerConfig serverConfig, ObjectNode objectNode) {

    JsonNode resultNode = null;

    try {
      URIBuilder builder = clientUtil.createUriBuilder("/query/form-instances");
      HttpPost post = clientUtil.createPost(builder.toString(), serverConfig);
      post.setEntity(clientUtil.createStringEntity(objectNode.toString()));

      resultNode = clientUtil.executeRequest(post, serverConfig);
    } catch (Exception ex) {
      throw new ActivitiServiceException(ex.getMessage(), ex);
    }

    return resultNode;
  }

  public JsonNode getProcessSubmittedForms(ServerConfig serverConfig, ObjectNode objectNode) {

    JsonNode resultNode = null;

    try {
      URIBuilder builder = clientUtil.createUriBuilder("/query/form-instances");
      HttpPost post = clientUtil.createPost(builder.toString(), serverConfig);
      post.setEntity(clientUtil.createStringEntity(objectNode.toString()));

      resultNode = clientUtil.executeRequest(post, serverConfig);
    } catch (Exception ex) {
      throw new ActivitiServiceException(ex.getMessage(), ex);
    }

    return resultNode;
  }

}

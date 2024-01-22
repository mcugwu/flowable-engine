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
package org.flowable.engine.impl.persistence.entity;

import org.flowable.common.engine.impl.persistence.entity.MutableEntityManager;
import org.flowable.engine.impl.persistence.entity.data.ProcessDefinitionInfoDataManager;

/**
 * @author Tijs Rademakers
 */
public interface ProcessDefinitionInfoEntityManager extends MutableEntityManager<ProcessDefinitionInfoEntity,ProcessDefinitionInfoDataManager> {

    void insertProcessDefinitionInfo(ProcessDefinitionInfoEntity processDefinitionInfo);

    void updateProcessDefinitionInfo(ProcessDefinitionInfoEntity updatedProcessDefinitionInfo);

    void deleteProcessDefinitionInfo(String processDefinitionId);

    void updateInfoJson(String id, byte[] json);

    void deleteInfoJson(ProcessDefinitionInfoEntity processDefinitionInfo);

    @Override
    ProcessDefinitionInfoEntity findById(String id);

    ProcessDefinitionInfoEntity findProcessDefinitionInfoByProcessDefinitionId(String processDefinitionId);

    byte[] findInfoJsonById(String infoJsonId);

}
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

import java.util.List;
import java.util.Map;

import org.flowable.engine.common.impl.persistence.entity.EntityManager;
import org.flowable.engine.history.HistoricTaskInstance;
import org.flowable.engine.impl.HistoricTaskInstanceQueryImpl;

/**
 * @author Joram Barrez
 */
public interface HistoricTaskInstanceEntityManager extends EntityManager<HistoricTaskInstanceEntity> {

    HistoricTaskInstanceEntity create(TaskEntity task, ExecutionEntity execution);

    long findHistoricTaskInstanceCountByQueryCriteria(HistoricTaskInstanceQueryImpl historicTaskInstanceQuery);

    List<HistoricTaskInstance> findHistoricTaskInstancesByQueryCriteria(HistoricTaskInstanceQueryImpl historicTaskInstanceQuery);

    List<HistoricTaskInstance> findHistoricTaskInstancesAndRelatedEntitiesByQueryCriteria(HistoricTaskInstanceQueryImpl historicTaskInstanceQuery);

    List<HistoricTaskInstance> findHistoricTaskInstancesByNativeQuery(Map<String, Object> parameterMap, int firstResult, int maxResults);

    long findHistoricTaskInstanceCountByNativeQuery(Map<String, Object> parameterMap);

    void deleteHistoricTaskInstancesByProcessInstanceId(String processInstanceId);

}
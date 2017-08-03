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
package org.flowable.variable.service;

import java.util.List;

import org.flowable.engine.common.api.query.NativeQuery;
import org.flowable.variable.service.history.HistoricVariableInstance;
import org.flowable.variable.service.history.HistoricVariableInstanceQuery;
import org.flowable.variable.service.history.NativeHistoricVariableInstanceQuery;
import org.flowable.variable.service.impl.HistoricVariableInstanceQueryImpl;
import org.flowable.variable.service.impl.persistence.entity.HistoricVariableInstanceEntity;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;

/**
 * Service which provides access to historic variables.
 * 
 * @author Tijs Rademakers
 */
public interface HistoricVariableService {
    
    HistoricVariableInstanceEntity getHistoricVariableInstance(String id);

    /**
     * Returns a new {@link HistoricVariableInstanceQuery} that can be used to dynamically query tasks.
     */
    HistoricVariableInstanceQuery createHistoricVariableInstanceQuery();

    /**
     * Returns a new {@link NativeQuery} for tasks.
     */
    NativeHistoricVariableInstanceQuery createNativeHistoricVariableInstanceQuery();
    
    HistoricVariableInstanceEntity createHistoricVariableInstance();
    
    List<HistoricVariableInstance> findHistoricVariableInstancesByQueryCriteria(HistoricVariableInstanceQueryImpl query);
    
    void insertHistoricVariableInstance(HistoricVariableInstanceEntity variable);
    
    HistoricVariableInstanceEntity copyAndInsert(VariableInstanceEntity variable);
    
    void copyVariableValue(HistoricVariableInstanceEntity historicVariable, VariableInstanceEntity variable);
    
    void deleteHistoricVariableInstance(String id);
    
    void deleteHistoricVariableInstance(HistoricVariableInstanceEntity historicVariable);
    
    void deleteHistoricVariableInstancesByProcessInstanceId(String processInstanceId);
    
    void deleteHistoricVariableInstancesByTaskId(String taskId);
}

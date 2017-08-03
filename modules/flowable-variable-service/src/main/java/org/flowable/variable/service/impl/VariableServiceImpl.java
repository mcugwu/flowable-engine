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
package org.flowable.variable.service.impl;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.flowable.variable.service.VariableService;
import org.flowable.variable.service.VariableServiceConfiguration;
import org.flowable.variable.service.impl.persistence.entity.VariableInstanceEntity;
import org.flowable.variable.service.impl.types.VariableType;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 */
public class VariableServiceImpl extends ServiceImpl implements VariableService {

    public VariableServiceImpl() {

    }

    public VariableServiceImpl(VariableServiceConfiguration variableServiceConfiguration) {
        super(variableServiceConfiguration);
    }
    
    public VariableInstanceEntity getVariableInstance(String id) {
        return getVariableInstanceEntityManager().findById(id);
    }
    
    public List<VariableInstanceEntity> findVariableInstancesByTaskId(String taskId) {
        return getVariableInstanceEntityManager().findVariableInstancesByTaskId(taskId);
    }
    
    public List<VariableInstanceEntity> findVariableInstancesByTaskIds(Set<String> taskIds) {
        return getVariableInstanceEntityManager().findVariableInstancesByTaskIds(taskIds);
    }
    
    public List<VariableInstanceEntity> findVariableInstancesByExecutionId(String executionId) {
        return getVariableInstanceEntityManager().findVariableInstancesByExecutionId(executionId);
    }
    
    public List<VariableInstanceEntity> findVariableInstancesByExecutionIds(Set<String> executionIds) {
        return getVariableInstanceEntityManager().findVariableInstancesByExecutionIds(executionIds);
    }
    
    public VariableInstanceEntity findVariableInstanceByTaskAndName(String taskId, String taskName) {
        return getVariableInstanceEntityManager().findVariableInstanceByTaskAndName(taskId, taskName);
    }
    
    public List<VariableInstanceEntity> findVariableInstancesByTaskAndNames(String taskId, Collection<String> taskNames) {
        return getVariableInstanceEntityManager().findVariableInstancesByTaskAndNames(taskId, taskNames);
    }
    
    public VariableInstanceEntity findVariableInstanceByExecutionAndName(String executionId, String taskName) {
        return getVariableInstanceEntityManager().findVariableInstanceByExecutionAndName(executionId, taskName);
    }
    
    public List<VariableInstanceEntity> findVariableInstancesByExecutionAndNames(String executionId, Collection<String> taskNames) {
        return getVariableInstanceEntityManager().findVariableInstancesByExecutionAndNames(executionId, taskNames);
    }
    
    public VariableInstanceEntity createVariableInstance(String name, VariableType type, Object value) {
        return getVariableInstanceEntityManager().create(name, type, value);
    }
    
    public void insertVariableInstance(VariableInstanceEntity variable) {
        getVariableInstanceEntityManager().insert(variable);
    }
    
    public void deleteVariableInstance(VariableInstanceEntity variable) {
        getVariableInstanceEntityManager().delete(variable);
    }
    
    public void deleteVariableInstanceMap(Map<String, VariableInstanceEntity> variableInstances) {
        getVariableInstanceEntityManager().deleteVariableInstanceMap(variableInstances);
    }
    
}

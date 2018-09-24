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

package org.flowable.engine.impl.migration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.common.engine.api.FlowableException;
import org.flowable.engine.migration.ProcessInstanceActivityMigrationMapping;
import org.flowable.engine.migration.ProcessInstanceMigrationDocument;
import org.flowable.engine.migration.ProcessInstanceMigrationDocumentBuilder;

/**
 * @author Dennis Federico
 */
public class ProcessInstanceMigrationDocumentBuilderImpl implements ProcessInstanceMigrationDocumentBuilder {

    protected String migrateToProcessDefinitionId;
    protected String migrateToProcessDefinitionKey;
    protected Integer migrateToProcessDefinitionVersion;
    protected String migrateToProcessDefinitionTenantId;
    protected List<ProcessInstanceActivityMigrationMapping> activityMigrationMappings = new ArrayList<>();
    protected Map<String, Object> processInstanceVariables = new HashMap<>();

    @Override
    public ProcessInstanceMigrationDocumentBuilder setProcessDefinitionToMigrateTo(String processDefinitionId) {
        this.migrateToProcessDefinitionId = processDefinitionId;
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocumentBuilder setProcessDefinitionToMigrateTo(String processDefinitionKey, int processDefinitionVersion) {
        this.migrateToProcessDefinitionKey = processDefinitionKey;
        this.migrateToProcessDefinitionVersion = processDefinitionVersion;
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocumentBuilder setTenantId(String processDefinitionTenantId) {
        this.migrateToProcessDefinitionTenantId = processDefinitionTenantId;
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocumentBuilder addActivityMigrationMappings(List<ProcessInstanceActivityMigrationMapping> activityMigrationMappings) {
        this.activityMigrationMappings.addAll(activityMigrationMappings);
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocumentBuilder addActivityMigrationMapping(ProcessInstanceActivityMigrationMapping activityMigrationMapping) {
        this.activityMigrationMappings.add(activityMigrationMapping);
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocumentBuilder addProcessInstanceVariable(String variableName, Object variableValue) {
        this.processInstanceVariables.put(variableName, variableValue);
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocumentBuilder addProcessInstanceVariables(Map<String, Object> processInstanceVariables) {
        this.processInstanceVariables.putAll(processInstanceVariables);
        return this;
    }

    @Override
    public ProcessInstanceMigrationDocument build() {

        if (migrateToProcessDefinitionId == null) {
            if (migrateToProcessDefinitionKey == null) {
                throw new FlowableException("Process definition key cannot be null");
            }
            if (migrateToProcessDefinitionVersion == null || migrateToProcessDefinitionVersion < 0) {
                throw new FlowableException("Process definition version must be a positive number");
            }
        }

        ProcessInstanceMigrationDocumentImpl document = new ProcessInstanceMigrationDocumentImpl();
        document.setMigrateToProcessDefinitionId(migrateToProcessDefinitionId);
        document.setMigrateToProcessDefinition(migrateToProcessDefinitionKey, migrateToProcessDefinitionVersion, migrateToProcessDefinitionTenantId);
        document.setActivityMigrationMappings(activityMigrationMappings);
        document.setProcessInstanceVariables(processInstanceVariables);

        return document;
    }

}

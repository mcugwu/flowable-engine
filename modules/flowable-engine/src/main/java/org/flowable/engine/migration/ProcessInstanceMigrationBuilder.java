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

package org.flowable.engine.migration;

import java.util.Map;

/**
 * @author Dennis Federico
 */
public interface ProcessInstanceMigrationBuilder {

    ProcessInstanceMigrationBuilder fromProcessInstanceMigrationDocument(ProcessInstanceMigrationDocument processInstanceMigrationDocument);

    ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionId);

    ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionKey, String processDefinitionVersion);

    ProcessInstanceMigrationBuilder migrateToProcessDefinition(String processDefinitionKey, String processDefinitionVersion, String processDefinitionTenantId);

    ProcessInstanceMigrationBuilder withMigrateToProcessDefinitionTenantId(String processDefinitionTenantId);

    ProcessInstanceMigrationBuilder addActivityMigrationMapping(String fromActivityId, String toActivityId);

    ProcessInstanceMigrationBuilder addActivityMigrationMappings(Map<String, String> activityMigrationMappings);

    ProcessInstanceMigrationDocument getProcessInstanceMigrationDocument();

    void migrate(String processInstanceId);

    void migrateProcessInstancesOf(String processDefinitionId);

    void migrateProcessInstancesOf(String processDefinitionKey, String processDefinitionVersion, String processDefinitionTenantId);
}

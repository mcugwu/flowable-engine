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
package org.flowable.cmmn.engine.impl.runtime;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.flowable.cmmn.api.runtime.CaseInstanceStartEventSubscriptionModificationBuilder;
import org.flowable.common.engine.api.FlowableIllegalArgumentException;

/**
 * The implementation for a case start event subscription modification builder.
 *
 * @author Micha Kiener
 */
public class CaseInstanceStartEventSubscriptionModificationBuilderImpl implements CaseInstanceStartEventSubscriptionModificationBuilder {
    protected final CmmnRuntimeServiceImpl cmmnRuntimeService;
    protected String caseDefinitionId;
    protected String newCaseDefinitionId;
    protected String tenantId;
    protected final Map<String, Object> correlationParameterValues = new HashMap<>();

    public CaseInstanceStartEventSubscriptionModificationBuilderImpl(CmmnRuntimeServiceImpl cmmnRuntimeService) {
        this.cmmnRuntimeService = cmmnRuntimeService;
    }

    @Override
    public CaseInstanceStartEventSubscriptionModificationBuilder caseDefinitionId(String caseDefinitionId) {
        this.caseDefinitionId = caseDefinitionId;
        return this;
    }
    
    @Override
    public CaseInstanceStartEventSubscriptionModificationBuilder tenantId(String tenantId) {
        this.tenantId = tenantId;
        return this;
    }

    @Override
    public CaseInstanceStartEventSubscriptionModificationBuilder addCorrelationParameterValue(String parameterName, Object parameterValue) {
        correlationParameterValues.put(parameterName, parameterValue);
        return this;
    }

    @Override
    public CaseInstanceStartEventSubscriptionModificationBuilder addCorrelationParameterValues(Map<String, Object> parameters) {
        correlationParameterValues.putAll(parameters);
        return this;
    }

    public String getCaseDefinitionId() {
        return caseDefinitionId;
    }

    public boolean hasNewCaseDefinitionId() {
        return StringUtils.isNotBlank(newCaseDefinitionId);
    }

    public String getNewCaseDefinitionId() {
        return newCaseDefinitionId;
    }
    
    public String getTenantId() {
        return tenantId;
    }

    public boolean hasCorrelationParameterValues() {
        return correlationParameterValues.size() > 0;
    }

    public Map<String, Object> getCorrelationParameterValues() {
        return correlationParameterValues;
    }

    @Override
    public void migrateToLatestCaseDefinition() {
        checkValidInformation();
        cmmnRuntimeService.migrateCaseInstanceStartEventSubscriptionsToCaseDefinitionVersion(this);
    }

    @Override
    public void migrateToCaseDefinition(String caseDefinitionId) {
        this.newCaseDefinitionId = caseDefinitionId;
        checkValidInformation();
        cmmnRuntimeService.migrateCaseInstanceStartEventSubscriptionsToCaseDefinitionVersion(this);
    }

    protected void checkValidInformation() {
        if (StringUtils.isEmpty(caseDefinitionId)) {
            throw new FlowableIllegalArgumentException("The case definition must be provided using the exact id of the version the subscription was registered for.");
        }
    }
}

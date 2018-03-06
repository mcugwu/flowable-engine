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
package org.flowable.cmmn.engine.configurator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.flowable.cmmn.api.PlanItemInstanceCallbackType;
import org.flowable.cmmn.engine.CmmnEngine;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.configurator.impl.deployer.CmmnDeployer;
import org.flowable.cmmn.engine.configurator.impl.process.DefaultProcessInstanceService;
import org.flowable.cmmn.engine.impl.callback.ChildProcessInstanceStateChangeCallback;
import org.flowable.cmmn.engine.impl.db.EntityDependencyOrder;
import org.flowable.engine.ProcessEngineConfiguration;
import org.flowable.engine.common.api.FlowableException;
import org.flowable.engine.common.impl.AbstractEngineConfiguration;
import org.flowable.engine.common.impl.AbstractEngineConfigurator;
import org.flowable.engine.common.impl.EngineDeployer;
import org.flowable.engine.common.impl.callback.RuntimeInstanceStateChangeCallback;
import org.flowable.engine.common.impl.interceptor.EngineConfigurationConstants;
import org.flowable.engine.common.impl.persistence.entity.Entity;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;

/**
 * @author Joram Barrez
 */
public class CmmnEngineConfigurator extends AbstractEngineConfigurator {

    protected CmmnEngineConfiguration cmmnEngineConfiguration;

    @Override
    public int getPriority() {
        return EngineConfigurationConstants.PRIORITY_ENGINE_CMMN;
    }

    @Override
    protected List<EngineDeployer> getCustomDeployers() {
        return Collections.<EngineDeployer>singletonList(new CmmnDeployer());
    }

    @Override
    protected String getMybatisCfgPath() {
        return CmmnEngineConfiguration.DEFAULT_MYBATIS_MAPPING_FILE;
    }

    @Override
    public void configure(AbstractEngineConfiguration engineConfiguration) {
        if (cmmnEngineConfiguration == null) {
            cmmnEngineConfiguration = new CmmnEngineConfiguration();
        }

        initialiseCommonProperties(engineConfiguration, cmmnEngineConfiguration);

        ProcessEngineConfigurationImpl processEngineConfiguration = (ProcessEngineConfigurationImpl) engineConfiguration;
        initProcessInstanceService(processEngineConfiguration);
        initProcessInstanceStateChangedCallbacks(processEngineConfiguration);

        cmmnEngineConfiguration.setExecuteServiceDbSchemaManagers(false);
        cmmnEngineConfiguration.setEnableTaskRelationshipCounts(processEngineConfiguration.getPerformanceSettings().isEnableTaskRelationshipCounts());
        cmmnEngineConfiguration.setTaskQueryLimit(processEngineConfiguration.getTaskQueryLimit());
        cmmnEngineConfiguration.setHistoricTaskQueryLimit(processEngineConfiguration.getHistoricTaskQueryLimit());
        // use the same query limit for executions and cases
        cmmnEngineConfiguration.setCaseQueryLimit(processEngineConfiguration.getExecutionQueryLimit());

        initCmmnEngine();

        initServiceConfigurations(engineConfiguration, cmmnEngineConfiguration);
    }

    protected void initProcessInstanceService(ProcessEngineConfigurationImpl processEngineConfiguration) {
        cmmnEngineConfiguration.setProcessInstanceService(new DefaultProcessInstanceService(processEngineConfiguration.getRuntimeService()));
    }

    protected void initProcessInstanceStateChangedCallbacks(ProcessEngineConfigurationImpl processEngineConfiguration) {
        if (processEngineConfiguration.getProcessInstanceStateChangedCallbacks() == null) {
            processEngineConfiguration.setProcessInstanceStateChangedCallbacks(new HashMap<String, List<RuntimeInstanceStateChangeCallback>>());
        }
        Map<String, List<RuntimeInstanceStateChangeCallback>> callbacks = processEngineConfiguration.getProcessInstanceStateChangedCallbacks();
        if (!callbacks.containsKey(PlanItemInstanceCallbackType.CHILD_PROCESS)) {
            callbacks.put(PlanItemInstanceCallbackType.CHILD_PROCESS, new ArrayList<RuntimeInstanceStateChangeCallback>());
        }
        callbacks.get(PlanItemInstanceCallbackType.CHILD_PROCESS).add(new ChildProcessInstanceStateChangeCallback(cmmnEngineConfiguration));
    }

    @Override
    protected List<Class<? extends Entity>> getEntityInsertionOrder() {
        return EntityDependencyOrder.INSERT_ORDER;
    }

    @Override
    protected List<Class<? extends Entity>> getEntityDeletionOrder() {
        return EntityDependencyOrder.DELETE_ORDER;
    }

    protected synchronized CmmnEngine initCmmnEngine() {
        if (cmmnEngineConfiguration == null) {
            throw new FlowableException("CmmnEngineConfiguration is required");
        }

        return cmmnEngineConfiguration.buildCmmnEngine();
    }

    @Override
    protected void initIdGenerator(AbstractEngineConfiguration engineConfiguration, AbstractEngineConfiguration targetEngineConfiguration) {
        super.initIdGenerator(engineConfiguration, targetEngineConfiguration);
        if (targetEngineConfiguration instanceof  CmmnEngineConfiguration) {
            CmmnEngineConfiguration targetCmmnEngineConfiguration = (CmmnEngineConfiguration) targetEngineConfiguration;
            if (targetCmmnEngineConfiguration.getTaskIdGenerator() == null) {
                if (engineConfiguration instanceof ProcessEngineConfiguration) {
                    targetCmmnEngineConfiguration.setTaskIdGenerator(((ProcessEngineConfiguration) engineConfiguration).getTaskIdGenerator());
                } else if (engineConfiguration instanceof CmmnEngineConfiguration) {
                    targetCmmnEngineConfiguration.setTaskIdGenerator(((CmmnEngineConfiguration) engineConfiguration).getTaskIdGenerator());
                } else {
                    targetCmmnEngineConfiguration.setTaskIdGenerator(engineConfiguration.getIdGenerator());
                }
            }
        }
    }

    public CmmnEngineConfiguration getCmmnEngineConfiguration() {
        return cmmnEngineConfiguration;
    }

    public CmmnEngineConfigurator setCmmnEngineConfiguration(CmmnEngineConfiguration cmmnEngineConfiguration) {
        this.cmmnEngineConfiguration = cmmnEngineConfiguration;
        return this;
    }
}

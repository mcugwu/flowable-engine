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
package org.flowable.cmmn.engine.impl.persistence.entity.data.impl;

import org.flowable.cmmn.api.history.HistoricPlanItemInstance;
import org.flowable.cmmn.engine.CmmnEngineConfiguration;
import org.flowable.cmmn.engine.impl.history.HistoricPlanItemInstanceQueryImpl;
import org.flowable.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntity;
import org.flowable.cmmn.engine.impl.persistence.entity.HistoricPlanItemInstanceEntityImpl;
import org.flowable.cmmn.engine.impl.persistence.entity.data.AbstractCmmnDataManager;
import org.flowable.cmmn.engine.impl.persistence.entity.data.HistoricPlanItemInstanceDataManager;

import java.util.List;

/**
 * @author Dennis Federico
 */
public class MybatisHistoricPlanItemInstanceDataManager extends AbstractCmmnDataManager<HistoricPlanItemInstanceEntity> implements HistoricPlanItemInstanceDataManager {

    public MybatisHistoricPlanItemInstanceDataManager(CmmnEngineConfiguration cmmnEngineConfiguration) {
        super(cmmnEngineConfiguration);
    }

    @Override
    public List<HistoricPlanItemInstance> findByCriteria(HistoricPlanItemInstanceQueryImpl query) {
        return getDbSqlSession().selectList("somethingWithVariables", query);
    }

    @Override
    public List<HistoricPlanItemInstance> findByCriteriaWithVariables(HistoricPlanItemInstanceQueryImpl query) {
        return getDbSqlSession().selectList("somethingWithVariables", query);
    }

    @Override
    public long countByCriteria(HistoricPlanItemInstanceQueryImpl query) {
        return (Long) getDbSqlSession().selectOne("somethingCount", query);
    }

    @Override
    public void deleteByCaseDefinitionId(String caseDefinitionId) {
        getDbSqlSession().delete("deleteSomething", caseDefinitionId, getManagedEntityClass());
    }

    @Override
    public Class<? extends HistoricPlanItemInstanceEntity> getManagedEntityClass() {
        return HistoricPlanItemInstanceEntityImpl.class;
    }

    @Override
    public HistoricPlanItemInstanceEntity create() {
        return new HistoricPlanItemInstanceEntityImpl();
    }

}

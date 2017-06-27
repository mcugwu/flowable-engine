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

package org.flowable.engine.impl.asyncexecutor.multitenant;

import org.flowable.engine.impl.asyncexecutor.ExecuteAsyncRunnable;
import org.flowable.engine.impl.cfg.ProcessEngineConfigurationImpl;
import org.flowable.engine.impl.cfg.multitenant.TenantInfoHolder;
import org.flowable.engine.runtime.JobInfo;

/**
 * Extends the default {@link ExecuteAsyncRunnable} by setting the 'tenant' context before executing.
 * 
 * @author Joram Barrez
 */
public class TenantAwareExecuteAsyncRunnable extends ExecuteAsyncRunnable {

    protected TenantInfoHolder tenantInfoHolder;
    protected String tenantId;

    public TenantAwareExecuteAsyncRunnable(JobInfo job, ProcessEngineConfigurationImpl processEngineConfiguration, TenantInfoHolder tenantInfoHolder, String tenantId) {
        super(job, processEngineConfiguration, processEngineConfiguration.getJobEntityManager(), null);
        this.tenantInfoHolder = tenantInfoHolder;
        this.tenantId = tenantId;
    }

    @Override
    public void run() {
        tenantInfoHolder.setCurrentTenantId(tenantId);
        super.run();
        tenantInfoHolder.clearCurrentTenantId();
    }

}

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
package org.flowable.engine.impl.cmd;

import java.util.Map;

import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.job.service.JobServiceConfiguration;
import org.flowable.job.service.impl.persistence.entity.ExternalWorkerJobEntity;

/**
 * @author Filip Hrisafov
 */
public class ExternalWorkerJobBpmnErrorCmd extends AbstractExternalWorkerJobCmd implements Command<Void> {

    protected Map<String, Object> variables;
    protected String errorCode;

    public ExternalWorkerJobBpmnErrorCmd(String externalJobId, String workerId, Map<String, Object> variables, String errorCode) {
        super(externalJobId, workerId);
        this.errorCode = errorCode;
    }

    @Override
    protected void runJobLogic(ExternalWorkerJobEntity externalWorkerJob, CommandContext commandContext) {
        JobServiceConfiguration jobServiceConfiguration = CommandContextUtil.getJobServiceConfiguration(commandContext);
        // We need to remove the job handler configuration
        String errorHandlerConfiguration;
        if (errorCode != null) {
            errorHandlerConfiguration = "error:" + errorCode;
        } else {
            errorHandlerConfiguration = "error:";
        }
        externalWorkerJob.setJobHandlerConfiguration(errorHandlerConfiguration);
        //TODO handle variables

        jobServiceConfiguration.getJobManager().moveExternalWorkerJobToExecutableJob(externalWorkerJob);
    }
}

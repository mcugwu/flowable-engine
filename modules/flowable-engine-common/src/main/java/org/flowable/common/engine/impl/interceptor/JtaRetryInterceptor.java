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
package org.flowable.common.engine.impl.interceptor;

import jakarta.transaction.Status;
import jakarta.transaction.SystemException;
import jakarta.transaction.TransactionManager;

import org.flowable.common.engine.api.FlowableException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * We cannot perform a retry if we are called in an existing transaction. In that case, the transaction will be marked "rollback-only" after the first FlowableOptimisticLockingException.
 * 
 * @author Daniel Meyer
 */
public class JtaRetryInterceptor extends RetryInterceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(JtaRetryInterceptor.class);

    protected final TransactionManager transactionManager;

    public JtaRetryInterceptor(TransactionManager transactionManager) {
        this.transactionManager = transactionManager;
    }

    @Override
    public <T> T execute(CommandConfig config, Command<T> command, CommandExecutor commandExecutor) {
        if (calledInsideTransaction()) {
            LOGGER.trace("Called inside transaction, skipping the retry interceptor.");
            return next.execute(config, command, commandExecutor);
        } else {
            return super.execute(config, command, commandExecutor);
        }
    }

    protected boolean calledInsideTransaction() {
        try {
            return transactionManager.getStatus() != Status.STATUS_NO_TRANSACTION;
        } catch (SystemException e) {
            throw new FlowableException("Could not determine the current status of the transaction manager: " + e.getMessage(), e);
        }
    }

}

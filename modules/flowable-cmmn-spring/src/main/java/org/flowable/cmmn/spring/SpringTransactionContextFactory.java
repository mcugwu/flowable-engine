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

package org.flowable.cmmn.spring;

import org.flowable.engine.common.impl.cfg.TransactionContext;
import org.flowable.engine.common.impl.cfg.TransactionContextFactory;
import org.flowable.engine.common.impl.interceptor.CommandContext;
import org.springframework.transaction.PlatformTransactionManager;

/**
 * @author Tijs Rademakers
 */
public class SpringTransactionContextFactory implements TransactionContextFactory {

    protected PlatformTransactionManager transactionManager;
    protected Integer transactionSynchronizationAdapterOrder;

    public SpringTransactionContextFactory(PlatformTransactionManager transactionManager) {
        this(transactionManager, null);
    }

    public SpringTransactionContextFactory(PlatformTransactionManager transactionManager, Integer transactionSynchronizationAdapterOrder) {
        this.transactionManager = transactionManager;
        this.transactionSynchronizationAdapterOrder = transactionSynchronizationAdapterOrder;
    }

    @Override
    public TransactionContext openTransactionContext(CommandContext commandContext) {
        return new SpringTransactionContext(transactionManager, commandContext, transactionSynchronizationAdapterOrder);
    }

}

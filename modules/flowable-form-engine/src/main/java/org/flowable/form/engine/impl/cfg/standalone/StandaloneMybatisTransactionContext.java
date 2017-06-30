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
package org.flowable.form.engine.impl.cfg.standalone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.ibatis.session.SqlSession;
import org.flowable.engine.common.impl.cfg.TransactionPropagation;
import org.flowable.engine.common.impl.cfg.TransactionState;
import org.flowable.engine.common.impl.interceptor.CommandConfig;
import org.flowable.form.engine.impl.cfg.TransactionContext;
import org.flowable.form.engine.impl.cfg.TransactionListener;
import org.flowable.form.engine.impl.db.DbSqlSession;
import org.flowable.form.engine.impl.interceptor.Command;
import org.flowable.form.engine.impl.interceptor.CommandContext;
import org.flowable.form.engine.impl.interceptor.CommandExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 */
public class StandaloneMybatisTransactionContext implements TransactionContext {

    private static final Logger LOGGER = LoggerFactory.getLogger(StandaloneMybatisTransactionContext.class);

    protected CommandContext commandContext;
    protected Map<TransactionState, List<TransactionListener>> stateTransactionListeners;

    public StandaloneMybatisTransactionContext(CommandContext commandContext) {
        this.commandContext = commandContext;
    }

    public void addTransactionListener(TransactionState transactionState, TransactionListener transactionListener) {
        if (stateTransactionListeners == null) {
            stateTransactionListeners = new HashMap<TransactionState, List<TransactionListener>>();
        }
        List<TransactionListener> transactionListeners = stateTransactionListeners.get(transactionState);
        if (transactionListeners == null) {
            transactionListeners = new ArrayList<TransactionListener>();
            stateTransactionListeners.put(transactionState, transactionListeners);
        }
        transactionListeners.add(transactionListener);
    }

    public void commit() {

        LOGGER.debug("firing event committing...");
        fireTransactionEvent(TransactionState.COMMITTING, false);

        LOGGER.debug("committing the ibatis sql session...");
        getDbSqlSession().commit();
        LOGGER.debug("firing event committed...");
        fireTransactionEvent(TransactionState.COMMITTED, true);

    }

    /**
     * Fires the event for the provided {@link TransactionState}.
     * 
     * @param transactionState
     *            The {@link TransactionState} for which the listeners will be called.
     * @param executeInNewContext
     *            If true, the listeners will be called in a new command context. This is needed for example when firing the {@link TransactionState#COMMITTED} event: the transaction is already
     *            committed and executing logic in the same context could lead to strange behaviour (for example doing a {@link SqlSession#update(String)} would actually roll back the update (as the
     *            MyBatis context is already committed and the internal flags have not been correctly set).
     */
    protected void fireTransactionEvent(TransactionState transactionState, boolean executeInNewContext) {
        if (stateTransactionListeners == null) {
            return;
        }
        final List<TransactionListener> transactionListeners = stateTransactionListeners.get(transactionState);
        if (transactionListeners == null) {
            return;
        }

        if (executeInNewContext) {
            CommandExecutor commandExecutor = commandContext.getFormEngineConfiguration().getCommandExecutor();
            CommandConfig commandConfig = new CommandConfig(false, TransactionPropagation.REQUIRES_NEW);
            commandExecutor.execute(commandConfig, new Command<Void>() {
                public Void execute(CommandContext commandContext) {
                    executeTransactionListeners(transactionListeners, commandContext);
                    return null;
                }
            });
        } else {
            executeTransactionListeners(transactionListeners, commandContext);
        }

    }

    protected void executeTransactionListeners(List<TransactionListener> transactionListeners, CommandContext commandContext) {
        for (TransactionListener transactionListener : transactionListeners) {
            transactionListener.execute(commandContext);
        }
    }

    protected DbSqlSession getDbSqlSession() {
        return commandContext.getDbSqlSession();
    }

    public void rollback() {
        try {
            try {
                LOGGER.debug("firing event rolling back...");
                fireTransactionEvent(TransactionState.ROLLINGBACK, false);

            } catch (Throwable exception) {
                LOGGER.info("Exception during transaction: {}", exception.getMessage());
                commandContext.exception(exception);
            } finally {
                LOGGER.debug("rolling back ibatis sql session...");
                getDbSqlSession().rollback();
            }

        } catch (Throwable exception) {
            LOGGER.info("Exception during transaction: {}", exception.getMessage());
            commandContext.exception(exception);

        } finally {
            LOGGER.debug("firing event rolled back...");
            fireTransactionEvent(TransactionState.ROLLED_BACK, true);
        }
    }
}

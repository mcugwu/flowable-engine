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

package org.activiti.dmn.engine.impl.interceptor;

import org.activiti.dmn.engine.DmnEngineConfiguration;
import org.activiti.dmn.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Tijs Rademakers
 */
public class CommandContextInterceptor extends AbstractCommandInterceptor {
  private static final Logger log = LoggerFactory.getLogger(CommandContextInterceptor.class);

  protected CommandContextFactory commandContextFactory;
  protected DmnEngineConfiguration dmnEngineConfiguration;

  public CommandContextInterceptor() {
  }

  public CommandContextInterceptor(CommandContextFactory commandContextFactory, DmnEngineConfiguration dmnEngineConfiguration) {
    this.commandContextFactory = commandContextFactory;
    this.dmnEngineConfiguration = dmnEngineConfiguration;
  }

  public <T> T execute(CommandConfig config, Command<T> command) {
    CommandContext context = Context.getCommandContext();

    boolean contextReused = false;
    // We need to check the exception, because the transaction can be in a
    // rollback state, and some other command is being fired to compensate (eg. decrementing job retries)
    if (!config.isContextReusePossible() || context == null || context.getException() != null) {
      context = commandContextFactory.createCommandContext(command);
    } else {
      log.debug("Valid context found. Reusing it for the current command '{}'", command.getClass().getCanonicalName());
      contextReused = true;
      context.setReused(true);
    }

    try {
      // Push on stack
      Context.setCommandContext(context);
      Context.setDmnEngineConfiguration(dmnEngineConfiguration);

      return next.execute(config, command);

    } catch (Exception e) {

      context.exception(e);
      
    } finally {
      try {
        if (!contextReused) {
          context.close();
        }
      } finally {
        
        // Pop from stack
        Context.removeCommandContext();
        Context.removeDmnEngineConfiguration();
      }
    }

    return null;
  }

  public CommandContextFactory getCommandContextFactory() {
    return commandContextFactory;
  }

  public void setCommandContextFactory(CommandContextFactory commandContextFactory) {
    this.commandContextFactory = commandContextFactory;
  }

  public DmnEngineConfiguration getDmnEngineConfiguration() {
    return dmnEngineConfiguration;
  }

  public void setDmnEngineConfiguration(DmnEngineConfiguration dmnEngineConfiguration) {
    this.dmnEngineConfiguration = dmnEngineConfiguration;
  }
}

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
package org.activiti.engine.impl.persistence.entity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.activiti.engine.ActivitiException;
import org.activiti.engine.impl.context.Context;
import org.activiti.engine.impl.interceptor.CommandContext;
import org.activiti.engine.impl.javax.el.ELContext;
import org.flowable.variable.api.delegate.VariableScope;
import org.flowable.variable.api.persistence.entity.VariableInstance;
import org.flowable.variable.api.types.VariableType;
import org.flowable.variable.api.types.VariableTypes;

/**
 * @author Tom Baeyens
 * @author Joram Barrez
 * @author Tijs Rademakers
 * @author Saeid Mirzaei
 */
public abstract class VariableScopeImpl implements Serializable, VariableScope {

    private static final long serialVersionUID = 1L;

    // The cache used when fetching all variables
    protected Map<String, VariableInstanceEntity> variableInstances = null; // needs to be null, the logic depends on it for checking if vars were already fetched

    // The cache is used when fetching/setting specific variables
    protected Map<String, VariableInstanceEntity> usedVariablesCache = new HashMap<>();

    protected Map<String, VariableInstance> transientVariabes;

    protected ELContext cachedElContext;

    protected String id;

    protected abstract List<VariableInstanceEntity> loadVariableInstances();

    protected abstract VariableScopeImpl getParentVariableScope();

    protected abstract void initializeVariableInstanceBackPointer(VariableInstanceEntity variableInstance);

    protected void ensureVariableInstancesInitialized() {
        if (variableInstances == null) {
            variableInstances = new HashMap<>();

            CommandContext commandContext = Context.getCommandContext();
            if (commandContext == null) {
                throw new ActivitiException("lazy loading outside command context");
            }
            List<VariableInstanceEntity> variableInstancesList = loadVariableInstances();
            for (VariableInstanceEntity variableInstance : variableInstancesList) {
                variableInstances.put(variableInstance.getName(), variableInstance);
            }
        }
    }

    @Override
    public Map<String, Object> getVariables() {
        return collectVariables(new HashMap<String, Object>());
    }

    @Override
    public Map<String, VariableInstance> getVariableInstances() {
        return collectVariableInstances(new HashMap<String, VariableInstance>());
    }

    @Override
    public Map<String, Object> getVariables(Collection<String> variableNames) {
        return getVariables(variableNames, true);
    }

    @Override
    public Map<String, VariableInstance> getVariableInstances(Collection<String> variableNames) {
        return getVariableInstances(variableNames, true);
    }

    @Override
    public Map<String, Object> getVariables(Collection<String> variableNames, boolean fetchAllVariables) {

        Map<String, Object> requestedVariables = new HashMap<>();
        Set<String> variableNamesToFetch = new HashSet<>(variableNames);

        // Transient variables 'shadow' any existing variables.
        // The values in the fetch-cache will be more recent, so they can override any existing ones
        for (String variableName : variableNames) {
            if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
                requestedVariables.put(variableName, transientVariabes.get(variableName).getValue());
                variableNamesToFetch.remove(variableName);
            } else if (usedVariablesCache.containsKey(variableName)) {
                requestedVariables.put(variableName, usedVariablesCache.get(variableName).getValue());
                variableNamesToFetch.remove(variableName);
            }
        }

        if (fetchAllVariables) {

            // getVariables() will go up the execution hierarchy, no need to do it here
            // also, the cached values will already be applied too
            Map<String, Object> allVariables = getVariables();
            for (String variableName : variableNamesToFetch) {
                requestedVariables.put(variableName, allVariables.get(variableName));
            }
            return requestedVariables;

        } else {

            // Fetch variables on this scope
            List<VariableInstanceEntity> variables = getSpecificVariables(variableNamesToFetch);
            for (VariableInstanceEntity variable : variables) {
                requestedVariables.put(variable.getName(), variable.getValue());
            }

            // Go up if needed
            VariableScopeImpl parent = getParentVariableScope();
            if (parent != null) {
                requestedVariables.putAll(parent.getVariables(variableNamesToFetch, fetchAllVariables));
            }

            return requestedVariables;

        }

    }

    @Override
    public Map<String, VariableInstance> getVariableInstances(Collection<String> variableNames, boolean fetchAllVariables) {

        Map<String, VariableInstance> requestedVariables = new HashMap<>();
        Set<String> variableNamesToFetch = new HashSet<>(variableNames);

        // The values in the fetch-cache will be more recent, so they can override any existing ones
        for (String variableName : variableNames) {
            if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
                requestedVariables.put(variableName, transientVariabes.get(variableName));
                variableNamesToFetch.remove(variableName);
            } else if (usedVariablesCache.containsKey(variableName)) {
                requestedVariables.put(variableName, usedVariablesCache.get(variableName));
                variableNamesToFetch.remove(variableName);
            }
        }

        if (fetchAllVariables) {

            // getVariables() will go up the execution hierarchy, no need to do it here
            // also, the cached values will already be applied too
            Map<String, VariableInstance> allVariables = getVariableInstances();
            for (String variableName : variableNamesToFetch) {
                requestedVariables.put(variableName, allVariables.get(variableName));
            }
            return requestedVariables;

        } else {

            // Fetch variables on this scope
            List<VariableInstanceEntity> variables = getSpecificVariables(variableNamesToFetch);
            for (VariableInstanceEntity variable : variables) {
                requestedVariables.put(variable.getName(), variable);
            }

            // Go up if needed
            VariableScopeImpl parent = getParentVariableScope();
            if (parent != null) {
                requestedVariables.putAll(parent.getVariableInstances(variableNamesToFetch, fetchAllVariables));
            }

            return requestedVariables;

        }

    }

    protected Map<String, Object> collectVariables(HashMap<String, Object> variables) {
        ensureVariableInstancesInitialized();
        VariableScopeImpl parentScope = getParentVariableScope();
        if (parentScope != null) {
            variables.putAll(parentScope.collectVariables(variables));
        }

        for (VariableInstanceEntity variableInstance : variableInstances.values()) {
            variables.put(variableInstance.getName(), variableInstance.getValue());
        }

        for (String variableName : usedVariablesCache.keySet()) {
            variables.put(variableName, usedVariablesCache.get(variableName).getValue());
        }

        if (transientVariabes != null) {
            for (String variableName : transientVariabes.keySet()) {
                variables.put(variableName, transientVariabes.get(variableName).getValue());
            }
        }

        return variables;
    }

    protected Map<String, VariableInstance> collectVariableInstances(HashMap<String, VariableInstance> variables) {
        ensureVariableInstancesInitialized();
        VariableScopeImpl parentScope = getParentVariableScope();
        if (parentScope != null) {
            variables.putAll(parentScope.collectVariableInstances(variables));
        }

        for (VariableInstance variableInstance : variableInstances.values()) {
            variables.put(variableInstance.getName(), variableInstance);
        }

        for (String variableName : usedVariablesCache.keySet()) {
            variables.put(variableName, usedVariablesCache.get(variableName));
        }

        if (transientVariabes != null) {
            variables.putAll(transientVariabes);
        }

        return variables;
    }

    @Override
    public Object getVariable(String variableName) {
        return getVariable(variableName, true);
    }

    @Override
    public VariableInstance getVariableInstance(String variableName) {
        return getVariableInstance(variableName, true);
    }

    /**
     * The same operation as {@link VariableScopeImpl#getVariable(String)}, but with an extra parameter to indicate whether or not all variables need to be fetched.
     * <p>
     * Note that the default Activiti way (because of backwards compatibility) is to fetch all the variables when doing a get/set of variables. So this means 'true' is the default value for this
     * method, and in fact it will simply delegate to {@link #getVariable(String)}. This can also be the most performant, if you're doing a lot of variable gets in the same transaction (eg in service
     * tasks).
     * <p>
     * In case 'false' is used, only the specific variable will be fetched.
     */
    @Override
    public Object getVariable(String variableName, boolean fetchAllVariables) {
        Object value = null;
        VariableInstance variable = getVariableInstance(variableName, fetchAllVariables);
        if (variable != null) {
            value = variable.getValue();
        }
        return value;
    }

    @Override
    public VariableInstance getVariableInstance(String variableName, boolean fetchAllVariables) {

        // Transient variable
        if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
            return transientVariabes.get(variableName);
        }

        // Check the local single-fetch cache
        if (usedVariablesCache.containsKey(variableName)) {
            return usedVariablesCache.get(variableName);
        }

        if (fetchAllVariables) {

            ensureVariableInstancesInitialized();
            VariableInstanceEntity variableInstance = variableInstances.get(variableName);
            if (variableInstance != null) {
                return variableInstance;
            }

            // Go up the hierarchy
            VariableScope parentScope = getParentVariableScope();
            if (parentScope != null) {
                return parentScope.getVariableInstance(variableName, true);
            }

            return null;

        } else {

            if (usedVariablesCache.containsKey(variableName)) {
                return usedVariablesCache.get(variableName);
            }

            if (variableInstances != null && variableInstances.containsKey(variableName)) {
                return variableInstances.get(variableName);
            }

            VariableInstanceEntity variable = getSpecificVariable(variableName);
            if (variable != null) {
                usedVariablesCache.put(variableName, variable);
                return variable;
            }

            // Go up the hierarchy
            VariableScope parentScope = getParentVariableScope();
            if (parentScope != null) {
                return parentScope.getVariableInstance(variableName, false);
            }

            return null;

        }
    }

    protected abstract VariableInstanceEntity getSpecificVariable(String variableName);

    @Override
    public Object getVariableLocal(String variableName) {
        return getVariableLocal(variableName, true);
    }

    @Override
    public VariableInstance getVariableInstanceLocal(String variableName) {
        return getVariableInstanceLocal(variableName, true);
    }

    @Override
    public Object getVariableLocal(String variableName, boolean fetchAllVariables) {
        Object value = null;
        VariableInstance variable = getVariableInstanceLocal(variableName, fetchAllVariables);
        if (variable != null) {
            value = variable.getValue();
        }
        return value;
    }

    @Override
    public VariableInstance getVariableInstanceLocal(String variableName, boolean fetchAllVariables) {

        if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
            return transientVariabes.get(variableName);
        }

        if (usedVariablesCache.containsKey(variableName)) {
            return usedVariablesCache.get(variableName);
        }

        if (fetchAllVariables) {

            ensureVariableInstancesInitialized();

            VariableInstanceEntity variableInstance = variableInstances.get(variableName);
            if (variableInstance != null) {
                return variableInstance;
            }
            return null;

        } else {

            if (usedVariablesCache.containsKey(variableName)) {
                VariableInstanceEntity variable = usedVariablesCache.get(variableName);
                if (variable != null) {
                    return variable;
                }
            }

            if (variableInstances != null && variableInstances.containsKey(variableName)) {
                VariableInstanceEntity variable = variableInstances.get(variableName);
                if (variable != null) {
                    return variableInstances.get(variableName);
                }
            }

            VariableInstanceEntity variable = getSpecificVariable(variableName);
            if (variable != null) {
                usedVariablesCache.put(variableName, variable);
                return variable;
            }

            return null;
        }
    }

    @Override
    public boolean hasVariables() {
        if (transientVariabes != null && !transientVariabes.isEmpty()) {
            return true;
        }

        ensureVariableInstancesInitialized();
        if (!variableInstances.isEmpty()) {
            return true;
        }
        VariableScope parentScope = getParentVariableScope();
        if (parentScope != null) {
            return parentScope.hasVariables();
        }
        return false;
    }

    @Override
    public boolean hasVariablesLocal() {
        if (transientVariabes != null && !transientVariabes.isEmpty()) {
            return true;
        }
        ensureVariableInstancesInitialized();
        return !variableInstances.isEmpty();
    }

    @Override
    public boolean hasVariable(String variableName) {
        if (hasVariableLocal(variableName)) {
            return true;
        }
        VariableScope parentScope = getParentVariableScope();
        if (parentScope != null) {
            return parentScope.hasVariable(variableName);
        }
        return false;
    }

    @Override
    public boolean hasVariableLocal(String variableName) {
        if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
            return true;
        }
        ensureVariableInstancesInitialized();
        return variableInstances.containsKey(variableName);
    }

    protected Set<String> collectVariableNames(Set<String> variableNames) {
        if (transientVariabes != null) {
            variableNames.addAll(transientVariabes.keySet());
        }

        ensureVariableInstancesInitialized();
        VariableScopeImpl parentScope = getParentVariableScope();
        if (parentScope != null) {
            variableNames.addAll(parentScope.collectVariableNames(variableNames));
        }
        for (VariableInstanceEntity variableInstance : variableInstances.values()) {
            variableNames.add(variableInstance.getName());
        }
        return variableNames;
    }

    @Override
    public Set<String> getVariableNames() {
        return collectVariableNames(new HashSet<String>());
    }

    @Override
    public Map<String, Object> getVariablesLocal() {
        Map<String, Object> variables = new HashMap<>();
        ensureVariableInstancesInitialized();
        for (VariableInstanceEntity variableInstance : variableInstances.values()) {
            variables.put(variableInstance.getName(), variableInstance.getValue());
        }
        for (String variableName : usedVariablesCache.keySet()) {
            variables.put(variableName, usedVariablesCache.get(variableName).getValue());
        }
        if (transientVariabes != null) {
            for (String variableName : transientVariabes.keySet()) {
                variables.put(variableName, transientVariabes.get(variableName).getValue());
            }
        }
        return variables;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstancesLocal() {
        Map<String, VariableInstance> variables = new HashMap<>();
        ensureVariableInstancesInitialized();
        for (VariableInstanceEntity variableInstance : variableInstances.values()) {
            variables.put(variableInstance.getName(), variableInstance);
        }
        for (String variableName : usedVariablesCache.keySet()) {
            variables.put(variableName, usedVariablesCache.get(variableName));
        }
        if (transientVariabes != null) {
            variables.putAll(transientVariabes);
        }
        return variables;
    }

    @Override
    public Map<String, Object> getVariablesLocal(Collection<String> variableNames) {
        return getVariablesLocal(variableNames, true);
    }

    @Override
    public Map<String, VariableInstance> getVariableInstancesLocal(Collection<String> variableNames) {
        return getVariableInstancesLocal(variableNames, true);
    }

    @Override
    public Map<String, Object> getVariablesLocal(Collection<String> variableNames, boolean fetchAllVariables) {
        Map<String, Object> requestedVariables = new HashMap<>();

        // The values in the fetch-cache will be more recent, so they can override any existing ones
        Set<String> variableNamesToFetch = new HashSet<>(variableNames);
        for (String variableName : variableNames) {
            if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
                requestedVariables.put(variableName, transientVariabes.get(variableName).getValue());
                variableNamesToFetch.remove(variableName);
            } else if (usedVariablesCache.containsKey(variableName)) {
                requestedVariables.put(variableName, usedVariablesCache.get(variableName).getValue());
                variableNamesToFetch.remove(variableName);
            }
        }

        if (fetchAllVariables) {

            Map<String, Object> allVariables = getVariablesLocal();
            for (String variableName : variableNamesToFetch) {
                requestedVariables.put(variableName, allVariables.get(variableName));
            }

        } else {

            List<VariableInstanceEntity> variables = getSpecificVariables(variableNamesToFetch);
            for (VariableInstanceEntity variable : variables) {
                requestedVariables.put(variable.getName(), variable.getValue());
            }

        }

        return requestedVariables;
    }

    @Override
    public Map<String, VariableInstance> getVariableInstancesLocal(Collection<String> variableNames, boolean fetchAllVariables) {
        Map<String, VariableInstance> requestedVariables = new HashMap<>();

        // The values in the fetch-cache will be more recent, so they can override any existing ones
        Set<String> variableNamesToFetch = new HashSet<>(variableNames);
        for (String variableName : variableNames) {
            if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
                requestedVariables.put(variableName, transientVariabes.get(variableName));
                variableNamesToFetch.remove(variableName);
            } else if (usedVariablesCache.containsKey(variableName)) {
                requestedVariables.put(variableName, usedVariablesCache.get(variableName));
                variableNamesToFetch.remove(variableName);
            }
        }

        if (fetchAllVariables) {

            Map<String, VariableInstance> allVariables = getVariableInstancesLocal();
            for (String variableName : variableNamesToFetch) {
                requestedVariables.put(variableName, allVariables.get(variableName));
            }

        } else {

            List<VariableInstanceEntity> variables = getSpecificVariables(variableNamesToFetch);
            for (VariableInstanceEntity variable : variables) {
                requestedVariables.put(variable.getName(), variable);
            }

        }

        return requestedVariables;
    }

    protected abstract List<VariableInstanceEntity> getSpecificVariables(Collection<String> variableNames);

    @Override
    public Set<String> getVariableNamesLocal() {
        Set<String> variableNames = new HashSet<>();
        if (transientVariabes != null) {
            variableNames.addAll(transientVariabes.keySet());
        }
        ensureVariableInstancesInitialized();
        variableNames.addAll(variableInstances.keySet());
        return variableNames;
    }

    public Map<String, VariableInstanceEntity> getVariableInstanceEntities() {
        ensureVariableInstancesInitialized();
        return Collections.unmodifiableMap(variableInstances);
    }

    public Map<String, VariableInstanceEntity> getUsedVariablesCache() {
        return usedVariablesCache;
    }

    public void createVariablesLocal(Map<String, ? extends Object> variables) {
        if (variables != null) {
            for (Map.Entry<String, ? extends Object> entry : variables.entrySet()) {
                createVariableLocal(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public void setVariables(Map<String, ? extends Object> variables) {
        if (variables != null) {
            for (String variableName : variables.keySet()) {
                setVariable(variableName, variables.get(variableName));
            }
        }
    }

    @Override
    public void setVariablesLocal(Map<String, ? extends Object> variables) {
        if (variables != null) {
            for (String variableName : variables.keySet()) {
                setVariableLocal(variableName, variables.get(variableName));
            }
        }
    }

    @Override
    public void removeVariables() {
        ensureVariableInstancesInitialized();
        Set<String> variableNames = new HashSet<>(variableInstances.keySet());
        for (String variableName : variableNames) {
            removeVariable(variableName);
        }
    }

    @Override
    public void removeVariablesLocal() {
        List<String> variableNames = new ArrayList<>(getVariableNamesLocal());
        for (String variableName : variableNames) {
            removeVariableLocal(variableName);
        }
    }

    public void deleteVariablesInstanceForLeavingScope() {
        ensureVariableInstancesInitialized();

        for (VariableInstanceEntity variableInstance : variableInstances.values()) {
            Context.getCommandContext().getHistoryManager().recordVariableUpdate(variableInstance);
            variableInstance.delete();
        }
    }

    @Override
    public void removeVariables(Collection<String> variableNames) {
        if (variableNames != null) {
            for (String variableName : variableNames) {
                removeVariable(variableName);
            }
        }
    }

    @Override
    public void removeVariablesLocal(Collection<String> variableNames) {
        if (variableNames != null) {
            for (String variableName : variableNames) {
                removeVariableLocal(variableName);
            }
        }
    }

    @Override
    public void setVariable(String variableName, Object value) {
        setVariable(variableName, value, getSourceActivityExecution(), true);
    }

    /**
     * The default {@link #setVariable(String, Object)} fetches all variables (for historical and backwards compatible reasons) while setting the variables.
     * <p>
     * Setting the fetchAllVariables parameter to true is the default behaviour (ie fetching all variables) Setting the fetchAllVariables parameter to false does not do that.
     */
    @Override
    public void setVariable(String variableName, Object value, boolean fetchAllVariables) {
        setVariable(variableName, value, getSourceActivityExecution(), fetchAllVariables);
    }

    protected void setVariable(String variableName, Object value,
                               ExecutionEntity sourceActivityExecution, boolean fetchAllVariables) {

        if (fetchAllVariables) {

            // If it's in the cache, it's more recent
            if (usedVariablesCache.containsKey(variableName)) {
                updateVariableInstance(usedVariablesCache.get(variableName), value, sourceActivityExecution);
            }

            // If the variable exists on this scope, replace it
            if (hasVariableLocal(variableName)) {
                setVariableLocal(variableName, value, sourceActivityExecution, true);
                return;
            }

            // Otherwise, go up the hierarchy (we're trying to put it as high as possible)
            VariableScopeImpl parentVariableScope = getParentVariableScope();
            if (parentVariableScope != null) {
                if (sourceActivityExecution == null) {
                    parentVariableScope.setVariable(variableName, value);
                } else {
                    parentVariableScope.setVariable(variableName, value, sourceActivityExecution, true);
                }
                return;
            }

            // We're as high as possible and the variable doesn't exist yet, so we're creating it
            createVariableLocal(variableName, value);

        } else {

            // Check local cache first
            if (usedVariablesCache.containsKey(variableName)) {

                updateVariableInstance(usedVariablesCache.get(variableName), value, sourceActivityExecution);

            } else if (variableInstances != null && variableInstances.containsKey(variableName)) {

                updateVariableInstance(variableInstances.get(variableName), value, sourceActivityExecution);

            } else {

                // Not in local cache, check if defined on this scope
                // Create it if it doesn't exist yet
                VariableInstanceEntity variable = getSpecificVariable(variableName);
                if (variable != null) {
                    updateVariableInstance(variable, value, sourceActivityExecution);
                    usedVariablesCache.put(variableName, variable);
                } else {

                    VariableScopeImpl parent = getParentVariableScope();
                    if (parent != null) {
                        parent.setVariable(variableName, value, sourceActivityExecution, fetchAllVariables);
                        return;
                    }

                    variable = createVariableInstance(variableName, value, sourceActivityExecution);
                    usedVariablesCache.put(variableName, variable);

                }

            }

        }

    }

    @Override
    public Object setVariableLocal(String variableName, Object value) {
        return setVariableLocal(variableName, value, getSourceActivityExecution(), true);
    }

    /**
     * The default {@link #setVariableLocal(String, Object)} fetches all variables (for historical and backwards compatible reasons) while setting the variables.
     * <p>
     * Setting the fetchAllVariables parameter to true is the default behaviour (ie fetching all variables) Setting the fetchAllVariables parameter to false does not do that.
     */
    @Override
    public Object setVariableLocal(String variableName, Object value, boolean fetchAllVariables) {
        return setVariableLocal(variableName, value, getSourceActivityExecution(), fetchAllVariables);
    }

    public Object setVariableLocal(String variableName, Object value, ExecutionEntity sourceActivityExecution, boolean fetchAllVariables) {

        if (fetchAllVariables) {

            // If it's in the cache, it's more recent
            if (usedVariablesCache.containsKey(variableName)) {
                updateVariableInstance(usedVariablesCache.get(variableName), value, sourceActivityExecution);
            }

            ensureVariableInstancesInitialized();

            VariableInstanceEntity variableInstance = variableInstances.get(variableName);
            if (variableInstance == null) {
                variableInstance = usedVariablesCache.get(variableName);
            }

            if (variableInstance == null) {
                createVariableLocal(variableName, value);
            } else {
                updateVariableInstance(variableInstance, value, sourceActivityExecution);
            }

            return null;

        } else {

            if (usedVariablesCache.containsKey(variableName)) {
                updateVariableInstance(usedVariablesCache.get(variableName), value, sourceActivityExecution);
            } else if (variableInstances != null && variableInstances.containsKey(variableName)) {
                updateVariableInstance(variableInstances.get(variableName), value, sourceActivityExecution);
            } else {

                VariableInstanceEntity variable = getSpecificVariable(variableName);
                if (variable != null) {
                    updateVariableInstance(variable, value, sourceActivityExecution);
                } else {
                    variable = createVariableInstance(variableName, value, sourceActivityExecution);
                }
                usedVariablesCache.put(variableName, variable);

            }

            return null;

        }
    }

    public void createVariableLocal(String variableName, Object value) {
        createVariableLocal(variableName, value, getSourceActivityExecution());
    }

    /**
     * only called when a new variable is created on this variable scope. This method is also responsible for propagating the creation of this variable to the history.
     */
    protected void createVariableLocal(String variableName, Object value, ExecutionEntity sourceActivityExecution) {
        ensureVariableInstancesInitialized();

        if (variableInstances.containsKey(variableName)) {
            throw new ActivitiException("variable '" + variableName + "' already exists. Use setVariableLocal if you want to overwrite the value");
        }

        createVariableInstance(variableName, value, sourceActivityExecution);
    }

    @Override
    public void removeVariable(String variableName) {
        removeVariable(variableName, getSourceActivityExecution());
    }

    protected void removeVariable(String variableName, ExecutionEntity sourceActivityExecution) {
        ensureVariableInstancesInitialized();
        if (variableInstances.containsKey(variableName)) {
            removeVariableLocal(variableName);
            return;
        }
        VariableScopeImpl parentVariableScope = getParentVariableScope();
        if (parentVariableScope != null) {
            if (sourceActivityExecution == null) {
                parentVariableScope.removeVariable(variableName);
            } else {
                parentVariableScope.removeVariable(variableName, sourceActivityExecution);
            }
        }
    }

    @Override
    public void removeVariableLocal(String variableName) {
        removeVariableLocal(variableName, getSourceActivityExecution());
    }

    protected ExecutionEntity getSourceActivityExecution() {
        return null;
    }

    protected void removeVariableLocal(String variableName, ExecutionEntity sourceActivityExecution) {
        ensureVariableInstancesInitialized();
        VariableInstanceEntity variableInstance = variableInstances.remove(variableName);
        if (variableInstance != null) {
            deleteVariableInstanceForExplicitUserCall(variableInstance, sourceActivityExecution);
        }
    }

    protected void deleteVariableInstanceForExplicitUserCall(VariableInstanceEntity variableInstance, ExecutionEntity sourceActivityExecution) {
        variableInstance.delete();
        variableInstance.setValue(null);

        // Record historic variable deletion
        Context.getCommandContext().getHistoryManager()
                .recordVariableRemoved(variableInstance);

        // Record historic detail
        Context.getCommandContext().getHistoryManager()
                .recordHistoricDetailVariableCreate(variableInstance, sourceActivityExecution, isActivityIdUsedForDetails());
    }

    protected void updateVariableInstance(VariableInstanceEntity variableInstance, Object value, ExecutionEntity sourceActivityExecution) {

        // Always check if the type should be altered. It's possible that the previous type is lower in the type
        // checking chain (e.g. serializable) and will return true on isAbleToStore(), even though another type
        // higher in the chain is eligible for storage.

        VariableTypes variableTypes = Context
                .getProcessEngineConfiguration()
                .getVariableTypes();

        VariableType newType = variableTypes.findVariableType(value);

        if (newType != null && !newType.equals(variableInstance.getType())) {
            variableInstance.setValue(null);
            variableInstance.setType(newType);
            variableInstance.forceUpdate();
            variableInstance.setValue(value);
        } else {
            variableInstance.setValue(value);
        }

        Context.getCommandContext().getHistoryManager()
                .recordHistoricDetailVariableCreate(variableInstance, sourceActivityExecution, isActivityIdUsedForDetails());

        Context.getCommandContext().getHistoryManager()
                .recordVariableUpdate(variableInstance);
    }

    protected VariableInstanceEntity createVariableInstance(String variableName, Object value, ExecutionEntity sourceActivityExecution) {
        VariableTypes variableTypes = Context
                .getProcessEngineConfiguration()
                .getVariableTypes();

        VariableType type = variableTypes.findVariableType(value);

        VariableInstanceEntity variableInstance = VariableInstanceEntity.createAndInsert(variableName, type, value);
        initializeVariableInstanceBackPointer(variableInstance);

        if (variableInstances != null) {
            variableInstances.put(variableName, variableInstance);
        }

        // Record historic variable
        Context.getCommandContext().getHistoryManager()
                .recordVariableCreate(variableInstance);

        // Record historic detail
        Context.getCommandContext().getHistoryManager()
                .recordHistoricDetailVariableCreate(variableInstance, sourceActivityExecution, isActivityIdUsedForDetails());

        return variableInstance;
    }

    /**
     * Execution variable updates have activity instance ids, but historic task variable updates don't.
     */
    protected boolean isActivityIdUsedForDetails() {
        return true;
    }

    /*
     * Transient variables
     */

    @Override
    public void setTransientVariablesLocal(Map<String, Object> transientVariables) {
        for (String variableName : transientVariables.keySet()) {
            setTransientVariableLocal(variableName, transientVariables.get(variableName));
        }
    }

    @Override
    public void setTransientVariableLocal(String variableName, Object variableValue) {
        if (transientVariabes == null) {
            transientVariabes = new HashMap<>();
        }
        transientVariabes.put(variableName, new TransientVariableInstance(variableName, variableValue));
    }

    @Override
    public void setTransientVariables(Map<String, Object> transientVariables) {
        for (String variableName : transientVariables.keySet()) {
            setTransientVariable(variableName, transientVariables.get(variableName));
        }
    }

    @Override
    public void setTransientVariable(String variableName, Object variableValue) {
        VariableScopeImpl parentVariableScope = getParentVariableScope();
        if (parentVariableScope != null) {
            parentVariableScope.setTransientVariable(variableName, variableValue);
            return;
        }
        setTransientVariableLocal(variableName, variableValue);
    }

    @Override
    public Object getTransientVariableLocal(String variableName) {
        if (transientVariabes != null) {
            return transientVariabes.get(variableName).getValue();
        }
        return null;
    }

    @Override
    public Map<String, Object> getTransientVariablesLocal() {
        if (transientVariabes != null) {
            Map<String, Object> variables = new HashMap<>();
            for (String variableName : transientVariabes.keySet()) {
                variables.put(variableName, transientVariabes.get(variableName).getValue());
            }
            return variables;
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public Object getTransientVariable(String variableName) {
        if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
            return transientVariabes.get(variableName).getValue();
        }

        VariableScopeImpl parentScope = getParentVariableScope();
        if (parentScope != null) {
            return parentScope.getTransientVariable(variableName);
        }

        return null;
    }

    @Override
    public Map<String, Object> getTransientVariables() {
        return collectTransientVariables(new HashMap<String, Object>());
    }

    protected Map<String, Object> collectTransientVariables(HashMap<String, Object> variables) {
        VariableScopeImpl parentScope = getParentVariableScope();
        if (parentScope != null) {
            variables.putAll(parentScope.collectVariables(variables));
        }

        if (transientVariabes != null) {
            for (String variableName : transientVariabes.keySet()) {
                variables.put(variableName, transientVariabes.get(variableName).getValue());
            }
        }

        return variables;
    }

    @Override
    public void removeTransientVariableLocal(String variableName) {
        if (transientVariabes != null) {
            transientVariabes.remove(variableName);
        }
    }

    @Override
    public void removeTransientVariablesLocal() {
        if (transientVariabes != null) {
            transientVariabes.clear();
        }
    }

    @Override
    public void removeTransientVariable(String variableName) {
        if (transientVariabes != null && transientVariabes.containsKey(variableName)) {
            removeTransientVariableLocal(variableName);
            return;
        }
        VariableScopeImpl parentVariableScope = getParentVariableScope();
        if (parentVariableScope != null) {
            parentVariableScope.removeTransientVariable(variableName);
        }
    }

    @Override
    public void removeTransientVariables() {
        removeTransientVariablesLocal();
        VariableScopeImpl parentVariableScope = getParentVariableScope();
        if (parentVariableScope != null) {
            parentVariableScope.removeTransientVariablesLocal();
        }
    }

    // getters and setters //////////////////////////////////////////////////////

    public ELContext getCachedElContext() {
        return cachedElContext;
    }

    public void setCachedElContext(ELContext cachedElContext) {
        this.cachedElContext = cachedElContext;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public <T> T getVariable(String variableName, Class<T> variableClass) {
        return variableClass.cast(getVariable(variableName));
    }

    @Override
    public <T> T getVariableLocal(String variableName, Class<T> variableClass) {
        return variableClass.cast(getVariableLocal(variableName));
    }
}

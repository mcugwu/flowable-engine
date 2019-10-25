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
package org.flowable.cmmn.engine.impl.util;

import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.ACTIVE_STATES;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.AVAILABLE;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.COMPLETED;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.ENABLED;
import static org.flowable.cmmn.api.runtime.PlanItemInstanceState.END_STATES;
import static org.flowable.cmmn.model.ParentCompletionRule.ALWAYS_IGNORE;
import static org.flowable.cmmn.model.ParentCompletionRule.ALWAYS_IGNORE_AFTER_FIRST_COMPLETION;
import static org.flowable.cmmn.model.ParentCompletionRule.IGNORE_AFTER_FIRST_COMPLETION_IF_AVAILABLE_OR_ENABLED;
import static org.flowable.cmmn.model.ParentCompletionRule.IGNORE_IF_AVAILABLE;
import static org.flowable.cmmn.model.ParentCompletionRule.IGNORE_IF_AVAILABLE_OR_ENABLED;

import java.util.Collection;
import java.util.List;

import org.flowable.cmmn.api.runtime.PlanItemDefinitionType;
import org.flowable.cmmn.engine.impl.persistence.entity.PlanItemInstanceContainer;
import org.flowable.cmmn.engine.impl.persistence.entity.PlanItemInstanceEntity;
import org.flowable.cmmn.model.ParentCompletionRule;
import org.flowable.cmmn.model.Stage;
import org.flowable.common.engine.impl.interceptor.CommandContext;

/**
 * Utility methods around a plan item container (most likely a stage or case plan model).
 *
 * @author Micha Kiener
 * @author Joram Barrez
 */
public class PlanItemInstanceContainerUtil {

    public static CompletionEvaluationResult shouldPlanItemContainerComplete(PlanItemInstanceContainer planItemInstanceContainer, boolean containerIsAutocomplete) {
        return shouldPlanItemContainerComplete(CommandContextUtil.getCommandContext(), planItemInstanceContainer, containerIsAutocomplete);
    }

    public static CompletionEvaluationResult shouldPlanItemContainerComplete(CommandContext commandContext, PlanItemInstanceContainer planItemInstanceContainer,
        boolean containerIsAutocomplete) {
        return shouldPlanItemContainerComplete(commandContext, planItemInstanceContainer, null, containerIsAutocomplete);
    }

    public static CompletionEvaluationResult shouldPlanItemContainerComplete(PlanItemInstanceContainer planItemInstanceContainer, Collection<String> planItemInstanceIdsToIgnore,
        boolean containerIsAutocomplete) {
        return shouldPlanItemContainerComplete(CommandContextUtil.getCommandContext(), planItemInstanceContainer, planItemInstanceIdsToIgnore, containerIsAutocomplete);
    }

    /**
     * Method to check a plan item container (most likely a stage or case plan model) if it should be completed according its child plan item states, their
     * combined behavior rules (e.g. repetition, if-part, manual activation, required, etc). The method returns two results: whether the plan item itself
     * is completable, which is the case when there is no more active or required work to be done, but it might still have optional work to do and a second
     * one which represents whether the plan item should in fact be completed (the difference being the state of the autocomplete mode, where if turned off,
     * only returns true, if there is no more work to be done and no more further options to activate some optional work).
     *
     * @param commandContext the command context in which the method is to be executed
     * @param planItemInstanceContainer the plan item container to evaluate is completable state (most likely a stage or case plan model)
     * @param planItemInstanceIdsToIgnore an optional list of plan item ids to be ignored for evaluating the parent completing state, might be null or empty
     * @param containerIsAutocomplete true, if the plan item container is in autocomplete mode, false, if not
     * @return two flags representing whether the plan item might be completable and whether it should actually be completed
     */
    public static CompletionEvaluationResult shouldPlanItemContainerComplete(CommandContext commandContext, PlanItemInstanceContainer planItemInstanceContainer,
        Collection<String> planItemInstanceIdsToIgnore, boolean containerIsAutocomplete) {
        // this might become false as we go through the plan items
        boolean shouldBeCompleted = true;

        if (planItemInstanceContainer.getChildPlanItemInstances() != null) {
            for (PlanItemInstanceEntity planItem : planItemInstanceContainer.getChildPlanItemInstances()) {

                // check, if the plan item should be ignored as its id is part of the list of plan items to be ignored
                if (planItemInstanceIdsToIgnore == null || !planItemInstanceIdsToIgnore.contains(planItem.getId())) {
                    Boolean alreadyCompleted = null;

                    // continue, if the plan item is in one of the end states or it is configured to be ignored for parent completion
                    if (END_STATES.contains(planItem.getState()) || isParentCompletionRuleForPlanItemEqualToType(planItem, ALWAYS_IGNORE)) {
                        continue;
                    }

                    // if the plan item is active and not to be ignored, we can directly stop to look any further as it prevents the parent from being completed
                    if (ACTIVE_STATES.contains(planItem.getState())) {
                        return new CompletionEvaluationResult(false, false);
                    }

                    // if the plan item is required and not yet in an end state or active, we need to check the special parent completion rule to determine
                    // if we need to prevent completion
                    if (ExpressionUtil.isRequiredPlanItemInstance(commandContext, planItem)) {
                        alreadyCompleted = getAlreadyCompletedFlagIfPlanItemIsIgnoredForCompletion(commandContext, planItem, null);
                        // check, if this plan item has repetition
                        if (alreadyCompleted != null && alreadyCompleted) {
                            continue;
                        }
                        return new CompletionEvaluationResult(false, false);
                    }

                    // same thing, if we're not in autocomplete mode, but the parent completion mode of the plan item says to ignore if available or enabled
                    if (isParentCompletionRuleForPlanItemEqualToType(planItem, IGNORE_IF_AVAILABLE_OR_ENABLED) &&
                        (ENABLED.equals(planItem.getState()) || AVAILABLE.equals(planItem.getState()))) {
                        continue;
                    }

                    // same for the available state
                    if ((isParentCompletionRuleForPlanItemEqualToType(planItem, IGNORE_IF_AVAILABLE) || ExpressionUtil.isCompletionNeutralPlanItemInstance(commandContext, planItem))
                        && AVAILABLE.equals(planItem.getState())) {
                        continue;
                    }

                    // special care if the plan item is repeatable
                    if (ExpressionUtil.evaluateRepetitionRule(commandContext, planItem)) {
                        alreadyCompleted = getAlreadyCompletedFlagIfPlanItemIsIgnoredForCompletion(commandContext, planItem, alreadyCompleted);
                        if (alreadyCompleted != null && alreadyCompleted) {
                            continue;
                        }
                    }

                    // if the plan item is in available or enabled state, we ignore it, if we look at it with autocompletion in mind
                    if (AVAILABLE.equals(planItem.getState()) || ENABLED.equals(planItem.getState())) {
                        shouldBeCompleted = shouldBeCompleted && containerIsAutocomplete;
                    }

                    // recursively invoke this method again with the current child plan item to check its children
                    if (planItem.getChildPlanItemInstances() != null) {
                        boolean childContainerIsAutocomplete = false;
                        if (PlanItemDefinitionType.STAGE.equals(planItem.getPlanItemDefinitionType())) {
                            Stage stage = (Stage) planItem.getPlanItem().getPlanItemDefinition();
                            childContainerIsAutocomplete = stage.isAutoComplete();
                        }
                        CompletionEvaluationResult completionEvaluationResult = shouldPlanItemContainerComplete(commandContext, planItem, null,
                            childContainerIsAutocomplete);
                        if (!completionEvaluationResult.isCompletable) {
                            return new CompletionEvaluationResult(false, false);
                        }
                        shouldBeCompleted = shouldBeCompleted & completionEvaluationResult.shouldBeCompleted;
                    }
                }
            }
        }
        return new CompletionEvaluationResult(true, shouldBeCompleted);
    }

    /**
     * Evaluates the plan item for being ignored for completion, if it was at least completed once before. The result is returned as a flag with lazy
     * initialization, as its evaluation is quite expensive.
     *
     * @param commandContext the command context under which this method is invoked
     * @param planItem the plan item to evaluate its completed state
     * @param alreadyCompleted the optional flag, if plan item completion was evaluated before as it involves a DB query and thus we want to avoid it to be
     *      invoked more than once
     * @return true, if the plan item was completed before and its parent completion mode is in ignored, if completed state, false, if it was never completed
     *      before, null, if there was no need to evaluate the flag
     */
    public static Boolean getAlreadyCompletedFlagIfPlanItemIsIgnoredForCompletion(CommandContext commandContext, PlanItemInstanceEntity planItem, Boolean alreadyCompleted) {
        // a required plan item with repetition might need special treatment
        if (isParentCompletionRuleForPlanItemEqualToType(planItem, ALWAYS_IGNORE_AFTER_FIRST_COMPLETION)) {
            // we're not (yet) in active state here and have repetition, so we need to check, whether that plan item was completed at least
            // once already in the past
            if (alreadyCompleted == null) {
                alreadyCompleted = isPlanItemAlreadyCompleted(commandContext, planItem);
            }
            return alreadyCompleted;
        } else if (isParentCompletionRuleForPlanItemEqualToType(planItem, IGNORE_AFTER_FIRST_COMPLETION_IF_AVAILABLE_OR_ENABLED) &&
            (AVAILABLE.equals(planItem.getState()) || ENABLED.equals(planItem.getState()))) {
            if (alreadyCompleted == null) {
                alreadyCompleted = isPlanItemAlreadyCompleted(commandContext, planItem);
            }
            return alreadyCompleted;
        }
        return null;
    }

    /**
     * Searches for completed plan items with the same plan item id as the given one.
     *
     * @param commandContext the command context under which this method is invoked
     * @param planItem the plan item instance to search for already completed instances
     * @return true, if there is at least one completed instance found, false otherwise
     */
    public static boolean isPlanItemAlreadyCompleted(CommandContext commandContext, PlanItemInstanceEntity planItem) {
        List<PlanItemInstanceEntity> planItems = CommandContextUtil.getPlanItemInstanceEntityManager(commandContext)
            .findByCaseInstanceIdAndPlanItemId(planItem.getCaseInstanceId(), planItem.getPlanItem().getId());
        if (planItems != null && planItems.size() > 0) {
            for (PlanItemInstanceEntity item : planItems) {
                if (COMPLETED.equals(item.getState())) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Checks the plan items parent completion mode to be equal to a given type and returns true if so.
     *
     * @param planItem the plan item to check for a parent completion mode
     * @param parentCompletionRuleType the parent completion type to check against
     * @return true, if there is a parent completion mode set on the plan item equal to the given one
     */
    public static boolean isParentCompletionRuleForPlanItemEqualToType(PlanItemInstanceEntity planItem, String parentCompletionRuleType) {
        if (planItem.getPlanItem().getItemControl() != null && planItem.getPlanItem().getItemControl().getParentCompletionRule() != null) {
            ParentCompletionRule parentCompletionRule = planItem.getPlanItem().getItemControl().getParentCompletionRule();
            if (parentCompletionRuleType.equals(parentCompletionRule.getType())) {
                return true;
            }
        }
        return false;
    }

}

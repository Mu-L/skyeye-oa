/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.cmd.multiinstanceexecution;

import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.ReflexUtil;
import com.skyeye.common.util.ToolUtil;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.task.api.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: GetMultiInstanceExecutionMation
 * @Description: 获取多实例节点的信息
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/1 16:22
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class GetMultiInstanceExecutionMation extends AbstractCountersignCmd implements Command<Map> {

    private static Logger LOGGER = LoggerFactory.getLogger(GetMultiInstanceExecutionMation.class);

    /**
     * 当前任务ID
     */
    private String taskId;

    public GetMultiInstanceExecutionMation(String taskId) {
        super();
        if (ObjectUtils.isEmpty(taskId)) {
            throw new RuntimeException("taskId 不能为空!");
        }
        this.taskId = taskId;
    }

    @Override
    public Map<String, Object> execute(CommandContext commandContext) {
        UserTask currentTaskNode = activitiTaskService.getCurrentUserTaskByTaskId(taskId);
        Map<String, Object> result = new HashMap<>();
        Task task = taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        int nrOfInstances = (int) runtimeService.getVariable(execution.getId(), RollbackConstants.MultiInstanceConstants.NR_OF_INSTANCE);
        int nrOfActiveInstances = (int) runtimeService.getVariable(execution.getId(), RollbackConstants.MultiInstanceConstants.NR_OF_ACTIVE_INSTANCES);
        int nrOfCompletedInstances = (int) runtimeService.getVariable(execution.getId(), RollbackConstants.MultiInstanceConstants.NR_OF_COMPLETE_INSTANCES);
        // 支持人不参与会签，所以总数和正在执行的会签数需要减1
        result.put("nrOfInstances", nrOfInstances - 1);
        result.put("nrOfActiveInstances", nrOfActiveInstances - 1);
        result.put("nrOfCompletedInstances", nrOfCompletedInstances);
        result.put("completionCondition", currentTaskNode.getLoopCharacteristics().getCompletionCondition());
        Object behavior = currentTaskNode.getBehavior();
        // multilnStanceExecttionChild：是否是子实例
        if (behavior instanceof ParallelMultiInstanceBehavior) {
            // 并行会签
            // 判断是否是子实例
            if (execution.getVariableLocal(ActivitiConstants.PARALLEL_MULTILN_STANCE_EXECTTION_CHILD) != null) {
                // 子实例
                LOGGER.info("ParallelMultiInstance task id [{}] is child ExecutionEntity", task.getId());
                result.put("multilnStanceExecttionChild", true);
            } else {
                LOGGER.info("ParallelMultiInstance task id [{}] is parent ExecutionEntity", task.getId());
                result.put("multilnStanceExecttionChild", false);
            }
        } else if (behavior instanceof SequentialMultiInstanceBehavior) {
            // 串行会签
            ExecutionEntityImpl parentNode = execution.getParent();
            Object obj = runtimeService.getVariable(parentNode.getId(), ActivitiConstants.ASSIGNEE_USER_LIST);
            if (obj == null) {
                result.put("multilnStanceExecttionChild", false);
            } else {
                List<String> assigneeList = (List<String>) obj;
                String assignee = task.getAssignee();
                int index = getCurrentTaskAssigneeIndex(assigneeList, assignee);
                if (index != assigneeList.size() - 1) {
                    result.put("nrOfActiveInstances", assigneeList.size() - 1 - index);
                    result.put("nrOfCompletedInstances", index);
                    LOGGER.info("SequentialMultiInstance task id [{}] is child ExecutionEntity", task.getId());
                    result.put("multilnStanceExecttionChild", true);
                } else {
                    result.put("nrOfActiveInstances", 0);
                    result.put("nrOfCompletedInstances", assigneeList.size() - 1);
                    LOGGER.info("SequentialMultiInstance task id [{}] is parent ExecutionEntity", task.getId());
                    result.put("multilnStanceExecttionChild", false);
                }
            }
        }
        getApprovalResult(currentTaskNode, result);
        return result;
    }

    private void getApprovalResult(UserTask currentTaskNode, Map<String, Object> result) {
        // true：串行多实例节点，false：并行多实例节点
        Boolean isSequential = currentTaskNode.getLoopCharacteristics().isSequential();
        if (isSequential) {
            int nrOfActiveInstances = Integer.parseInt(result.get("nrOfActiveInstances").toString());
            if (nrOfActiveInstances != 0) {
                return;
            }
        }
        List<Map<String, Object>> assigneeList = managementService.executeCommand(new FindMultiInstanceExecutionUserCmd(taskId, isSequential));
        // 获取必选评审人的活动节点，如果没有活动的必选评审人节点，则去做表达式的校验，判断该节点的结果是否通过
        assigneeList = assigneeList.stream().filter(bean -> (Boolean) bean.get("isActive") && bean.containsKey("isMandatory")).collect(Collectors.toList());
        if (CollectionUtils.isEmpty(assigneeList)) {
            Map<String, String> newMap = result.entrySet().stream()
                .collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue())));
            Boolean approvalResult = (Boolean) ReflexUtil.convertToCode(
                regexCompletionCondition(currentTaskNode.getLoopCharacteristics().getCompletionCondition()), newMap);
            result.put("approvalResult", approvalResult);
        }
    }

    private String regexCompletionCondition(String completionCondition) {
        if (ToolUtil.isBlank(completionCondition)) {
            return "nrOfInstances == nrOfCompletedInstances";
        }
        return completionCondition.replace("${", "").replace("}", "");
    }

    private int getCurrentTaskAssigneeIndex(List<String> assigneeStrList, String assignee) {
        for (int i = 0; i < assigneeStrList.size(); i++) {
            if (assignee.equals(assigneeStrList.get(i))) {
                return i;
            }
        }
        return 0;
    }

}

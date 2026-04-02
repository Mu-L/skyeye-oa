/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.cmd.multiinstanceexecution;

import com.skyeye.activiti.cmd.rollback.RollbackConstants;
import com.skyeye.common.constans.ActivitiConstants;
import org.flowable.bpmn.model.BpmnModel;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.bpmn.behavior.ParallelMultiInstanceBehavior;
import org.flowable.engine.impl.bpmn.behavior.SequentialMultiInstanceBehavior;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: AddMultiInstanceExecutionCmd
 * @Description: 进行会签加签命令 flowable:org.flowable.engine.impl.cmd.AddMultiInstanceExecutionCmd
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/26 19:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class AddMultiInstanceExecutionCmd extends AbstractCountersignCmd implements Command<String> {

    private static Logger LOGGER = LoggerFactory.getLogger(AddMultiInstanceExecutionCmd.class);

    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * 审核人
     */
    private List<Map<String, Object>> assigneeList;

    private List<Map<String, Object>> allAssigneeList;

    public AddMultiInstanceExecutionCmd(String taskId, List<Map<String, Object>> assigneeList, List<Map<String, Object>> allAssigneeList) {
        super();
        if (ObjectUtils.isEmpty(assigneeList)) {
            throw new RuntimeException("assigneeList 不能为空!");
        }
        this.taskId = taskId;
        this.assigneeList = assigneeList;
        this.allAssigneeList = allAssigneeList;
    }

    @Override
    public String execute(CommandContext commandContext) {
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        BpmnModel bpmnModel = repositoryService.getBpmnModel(task.getProcessDefinitionId());
        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        UserTask userTask = (UserTask) process.getFlowElement(task.getTaskDefinitionKey());
        if (userTask.getLoopCharacteristics() == null) {
            LOGGER.info("task:[" + task.getId() + "] 不是会签节任务");
        }

        // 获取父级
        ExecutionEntityImpl parentNode = execution.getParent();

        // 获取流程变量
        int nrOfInstances = (int) runtimeService.getVariable(parentNode.getId(), RollbackConstants.MultiInstanceConstants.NR_OF_INSTANCE);
        int nrOfActiveInstances = (int) runtimeService.getVariable(parentNode.getId(), RollbackConstants.MultiInstanceConstants.NR_OF_ACTIVE_INSTANCES);

        // 获取管理器
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);

        Object behavior = userTask.getBehavior();
        if (behavior instanceof ParallelMultiInstanceBehavior) {
            LOGGER.info("task:[" + task.getId() + "] 并行会签 加签 任务");
            addParallelMultiInstance(commandContext, userTask, parentNode, nrOfInstances, nrOfActiveInstances, executionEntityManager);
        } else if (behavior instanceof SequentialMultiInstanceBehavior) {
            LOGGER.info("task:[" + task.getId() + "] 串行会签 加签 任务");
            addSequentialMultiInstance(task, execution, parentNode);
        }

        return "加签成功";
    }

    private void addParallelMultiInstance(CommandContext commandContext, UserTask userTask, ExecutionEntityImpl parentNode,
                                          int nrOfInstances, int nrOfActiveInstances, ExecutionEntityManager executionEntityManager) {
        // 并行会签也记录主持人（当前任务审批人），便于“我主持的会签”查询
        TaskEntityImpl currentTask = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        if (currentTask != null) {
            ExecutionEntityImpl currentExecution = (ExecutionEntityImpl) runtimeService.createExecutionQuery()
                .executionId(currentTask.getExecutionId()).singleResult();
            String hostAssignee = (String) runtimeService.getVariableLocal(currentExecution.getId(),
                ActivitiConstants.MULTI_INSTANCE_HOST_ASSIGNEE);
            if (StringUtils.isEmpty(hostAssignee)) {
                runtimeService.setVariableLocal(currentExecution.getId(),
                    ActivitiConstants.MULTI_INSTANCE_HOST_ASSIGNEE, currentTask.getAssignee());
            }
        }

        // 设置循环标志变量
        runtimeService.setVariable(parentNode.getId(), RollbackConstants.MultiInstanceConstants.NR_OF_INSTANCE, nrOfInstances + assigneeList.size());
        runtimeService.setVariable(parentNode.getId(), RollbackConstants.MultiInstanceConstants.NR_OF_ACTIVE_INSTANCES, nrOfActiveInstances + assigneeList.size());

        // 新建任务列表
        for (Map<String, Object> assignee : this.assigneeList) {
            // 创建 子 execution
            ExecutionEntity newExecution = executionEntityManager.createChildExecution(parentNode);
            newExecution.setActive(true);
            newExecution.setVariableLocal(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, nrOfInstances);
            newExecution.setVariableLocal(ActivitiConstants.ASSIGNEE_USER, assignee.get("id").toString());
            newExecution.setVariableLocal(ActivitiConstants.PARALLEL_MULTILN_STANCE_EXECTTION_CHILD, true);
            newExecution.setCurrentFlowElement(userTask);
            if (assignee.containsKey("isMandatory") && "1".equals(assignee.get("isMandatory").toString())) {
                // 必选评审人
                newExecution.setVariableLocal(ActivitiConstants.PARALLEL_MULTILN_STANCE_EXECTTION_ISMANDATORY, true);
            }

            // 任务总数 +1
            nrOfInstances++;

            // 推入时间表序列
            CommandContextUtil.getAgenda(commandContext).planContinueMultiInstanceOperation(newExecution, parentNode, nrOfInstances);
        }
    }

    private void addSequentialMultiInstance(TaskEntityImpl task, ExecutionEntityImpl execution, ExecutionEntityImpl parentNode) {
        String assignee = task.getAssignee();
        // 当前任务执行位置
        int loopCounterIndex = 0;
        List<String> newAllUserIds = allAssigneeList.stream().map(p -> p.get("id").toString()).collect(Collectors.toList());
        for (int i = 0; i < newAllUserIds.size(); i++) {
            String temp = newAllUserIds.get(i);
            if (assignee.equals(temp)) {
                loopCounterIndex = i;
            }
        }

        // 任务主持人
        String hostAssignee = (String) runtimeService.getVariableLocal(execution.getId(), ActivitiConstants.MULTI_INSTANCE_HOST_ASSIGNEE);
        if (StringUtils.isEmpty(hostAssignee)) {
            hostAssignee = task.getAssignee();
            runtimeService.setVariableLocal(execution.getId(), ActivitiConstants.MULTI_INSTANCE_HOST_ASSIGNEE, hostAssignee);
            // 修改当前任务执行人
            taskService.setAssignee(taskId, newAllUserIds.get(0));
            execution.setVariableLocal(ActivitiConstants.ASSIGNEE_USER, newAllUserIds.get(0));
            loopCounterIndex = 0;
        } else {
            // 获取已经结束的评审任务
            List<Map<String, Object>> tem =
                allAssigneeList.stream().filter(bean -> bean.containsKey("isActive") && !(Boolean) bean.get("isActive"))
                    .collect(Collectors.toList());
            taskService.setAssignee(taskId, newAllUserIds.get(tem.size()));
            execution.setVariableLocal(ActivitiConstants.ASSIGNEE_USER, newAllUserIds.get(tem.size()));
        }

        // 修改 计数器位置
        execution.setVariableLocal(RollbackConstants.MultiInstanceConstants.LOOP_COUNTER, loopCounterIndex);

        // 修改全局变量
        Map<String, Object> variables = new HashMap<>();
        variables.put(RollbackConstants.MultiInstanceConstants.NR_OF_INSTANCE, allAssigneeList.size());
        variables.put(RollbackConstants.MultiInstanceConstants.NR_OF_ACTIVE_INSTANCES, allAssigneeList.size() - loopCounterIndex);
        variables.put(RollbackConstants.MultiInstanceConstants.NR_OF_COMPLETE_INSTANCES, loopCounterIndex);
        variables.put(ActivitiConstants.ASSIGNEE_USER_LIST, newAllUserIds);

        runtimeService.setVariables(parentNode.getId(), variables);
    }

}

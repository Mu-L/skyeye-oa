/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.cmd.nextusertask;

import org.apache.commons.collections.CollectionUtils;
import org.flowable.bpmn.model.*;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.util.condition.ConditionUtil;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName: FindFirstUserTaskByConditionCmd
 * @Description: 根据业务数据查找第一个符合条件的用户任务节点
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class FindFirstUserTaskByConditionCmd implements Command<UserTask> {

    private final BpmnModel bpmnModel;
    private final Map<String, Object> businessData;
    private final String processDefinitionId;

    /**
     * 返回第一个符合条件的用户任务节点
     */
    private UserTask firstUserTask;

    /**
     * @param bpmnModel BPMN 模型
     * @param businessData 业务数据，用于判断条件表达式
     * @param processDefinitionId 流程定义ID
     */
    public FindFirstUserTaskByConditionCmd(BpmnModel bpmnModel, Map<String, Object> businessData, String processDefinitionId) {
        this.bpmnModel = bpmnModel;
        this.businessData = businessData;
        this.processDefinitionId = processDefinitionId;
    }

    @Override
    public UserTask execute(CommandContext commandContext) {
        if (bpmnModel == null || bpmnModel.getProcesses().isEmpty()) {
            return null;
        }

        org.flowable.bpmn.model.Process process = bpmnModel.getProcesses().get(0);
        
        // 找到开始节点
        StartEvent startEvent = null;
        for (FlowElement flowElement : process.getFlowElements()) {
            if (flowElement instanceof StartEvent) {
                startEvent = (StartEvent) flowElement;
                break;
            }
        }

        if (startEvent == null) {
            return null;
        }

        // 创建临时的 ExecutionEntity 用于评估条件表达式
        ExecutionEntityImpl tempExecution = createTempExecution(commandContext, processDefinitionId, businessData);
        
        // 从开始节点开始查找第一个符合条件的用户任务节点
        firstUserTask = findFirstUserTaskByCondition(startEvent, process, tempExecution, new HashSet<>());
        
        return firstUserTask;
    }

    /**
     * 递归查找第一个符合条件的用户任务节点
     * 
     * @param currentNode 当前节点
     * @param process 流程对象
     * @param execution 执行对象（用于评估条件表达式）
     * @param visitedNodes 已访问的节点集合，用于防止循环
     * @return 第一个符合条件的用户任务节点
     */
    private UserTask findFirstUserTaskByCondition(FlowElement currentNode, org.flowable.bpmn.model.Process process, 
                                                   ExecutionEntity execution, Set<String> visitedNodes) {
        if (currentNode == null) {
            return null;
        }

        // 防止循环访问
        if (visitedNodes.contains(currentNode.getId())) {
            return null;
        }
        visitedNodes.add(currentNode.getId());

        // 如果当前节点是用户任务，直接返回
        if (currentNode instanceof UserTask) {
            return (UserTask) currentNode;
        }

        // 如果当前节点是流程节点（FlowNode），获取其出线
        if (currentNode instanceof FlowNode) {
            FlowNode flowNode = (FlowNode) currentNode;
            List<SequenceFlow> outgoingFlows = flowNode.getOutgoingFlows();

            if (CollectionUtils.isEmpty(outgoingFlows)) {
                return null;
            }

            // 处理默认流（如果存在）
            String defaultFlow = null;
            if (flowNode instanceof Activity) {
                defaultFlow = ((Activity) flowNode).getDefaultFlow();
            } else if (flowNode instanceof Gateway) {
                defaultFlow = ((Gateway) flowNode).getDefaultFlow();
            }

            // 遍历所有出线
            for (SequenceFlow sequenceFlow : outgoingFlows) {
                // 检查条件表达式
                boolean conditionMet = hasTrueCondition(sequenceFlow, execution);
                
                // 如果是默认流且没有其他条件满足，也使用默认流
                boolean isDefaultFlow = defaultFlow != null && defaultFlow.equals(sequenceFlow.getId());
                if (!conditionMet && !isDefaultFlow) {
                    continue;
                }

                // 获取目标节点
                FlowElement targetElement = sequenceFlow.getTargetFlowElement();
                if (targetElement == null) {
                    // 如果目标元素为空，尝试从流程中获取
                    String targetRef = sequenceFlow.getTargetRef();
                    targetElement = process.getFlowElement(targetRef);
                }

                if (targetElement != null) {
                    // 递归查找
                    UserTask userTask = findFirstUserTaskByCondition(targetElement, process, execution, visitedNodes);
                    if (userTask != null) {
                        return userTask;
                    }
                }
            }
        }

        return null;
    }

    /**
     * 判断顺序流条件是否为真
     * 
     * @param sequenceFlow 顺序流
     * @param execution 执行对象
     * @return true 如果条件为真或没有条件
     */
    private boolean hasTrueCondition(SequenceFlow sequenceFlow, ExecutionEntity execution) {
        // 如果没有条件表达式，默认返回 true
        String conditionExpression = sequenceFlow.getConditionExpression();
        if (conditionExpression == null || conditionExpression.trim().isEmpty()) {
            return true;
        }

        try {
            // 使用 Flowable 的 ConditionUtil 评估条件
            return ConditionUtil.hasTrueCondition(sequenceFlow, execution);
        } catch (Exception e) {
            // 如果评估失败，默认返回 false（保守策略）
            return false;
        }
    }

    /**
     * 创建临时的 ExecutionEntity 用于评估条件表达式
     * 
     * @param commandContext Command 上下文
     * @param processDefinitionId 流程定义ID
     * @param businessData 业务数据
     * @return 临时执行对象
     */
    private ExecutionEntityImpl createTempExecution(CommandContext commandContext, String processDefinitionId, Map<String, Object> businessData) {
        ExecutionEntityImpl execution = new ExecutionEntityImpl();
        execution.setProcessDefinitionId(processDefinitionId);
        execution.setVariables(businessData);
        // 设置必要的属性，以便 ConditionUtil 可以正常工作
        execution.setProcessInstanceId("temp_" + System.currentTimeMillis());
        execution.setId("temp_execution_" + System.currentTimeMillis());
        return execution;
    }
}


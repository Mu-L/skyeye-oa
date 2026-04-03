/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.cmd.multiinstanceexecution;

import com.skyeye.common.constans.ActivitiConstants;
import org.flowable.bpmn.model.UserTask;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.springframework.util.ObjectUtils;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 根据会签设定提交的最终人员列表，同步「必选评审人」标记。
 * <p>
 * 并行：写入各子 execution 的 {@link ActivitiConstants#PARALLEL_MULTILN_STANCE_EXECTTION_ISMANDATORY}；
 * 串行：写入父 execution 上的 {@link ActivitiConstants#MULTI_INSTANCE_MANDATORY_ASSIGNEE_IDS}。
 */
public class SyncMultiInstanceMandatoryFlagsCmd extends AbstractCountersignCmd implements Command<Void> {

    private final String taskId;
    private final List<Map<String, Object>> fullAssigneeList;

    public SyncMultiInstanceMandatoryFlagsCmd(String taskId, List<Map<String, Object>> fullAssigneeList) {
        super();
        if (ObjectUtils.isEmpty(taskId)) {
            throw new RuntimeException("taskId 不能为空!");
        }
        this.taskId = taskId;
        this.fullAssigneeList = fullAssigneeList == null ? new ArrayList<>() : fullAssigneeList;
    }

    @Override
    public Void execute(CommandContext commandContext) {
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        if (task == null) {
            return null;
        }
        UserTask userTask = activitiTaskService.getCurrentUserTaskByTaskId(taskId);
        if (userTask == null || userTask.getLoopCharacteristics() == null) {
            return null;
        }

        Map<String, Boolean> mandatoryByUserId = new LinkedHashMap<>();
        for (Map<String, Object> u : fullAssigneeList) {
            if (u == null || u.get("id") == null) {
                continue;
            }
            String uid = u.get("id").toString();
            Object im = u.get("isMandatory");
            boolean isMandatory = im != null && "1".equals(im.toString());
            mandatoryByUserId.put(uid, isMandatory);
        }

        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery()
            .executionId(task.getExecutionId()).singleResult();
        if (execution == null) {
            return null;
        }
        ExecutionEntityImpl parentNode = execution.getParent();
        if (parentNode == null) {
            return null;
        }

        boolean isSequential = userTask.getLoopCharacteristics().isSequential();
        if (isSequential) {
            List<String> mandatoryIds = mandatoryByUserId.entrySet().stream()
                .filter(Map.Entry::getValue)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
            runtimeService.setVariable(parentNode.getId(), ActivitiConstants.MULTI_INSTANCE_MANDATORY_ASSIGNEE_IDS, mandatoryIds);
        } else {
            ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);
            List<ExecutionEntity> children = executionEntityManager.findChildExecutionsByParentExecutionId(parentNode.getId());
            for (ExecutionEntity child : children) {
                Object assigneeVar = child.getVariableLocal(ActivitiConstants.ASSIGNEE_USER);
                if (assigneeVar == null) {
                    continue;
                }
                String aid = assigneeVar.toString().trim();
                if (aid.isEmpty()) {
                    continue;
                }
                boolean m = Boolean.TRUE.equals(mandatoryByUserId.get(aid));
                if (m) {
                    runtimeService.setVariableLocal(child.getId(), ActivitiConstants.PARALLEL_MULTILN_STANCE_EXECTTION_ISMANDATORY, true);
                } else {
                    runtimeService.removeVariableLocal(child.getId(), ActivitiConstants.PARALLEL_MULTILN_STANCE_EXECTTION_ISMANDATORY);
                }
            }
        }
        return null;
    }
}

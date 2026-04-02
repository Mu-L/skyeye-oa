/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.cmd.multiinstanceexecution;

import com.skyeye.common.constans.ActivitiConstants;
import com.skyeye.common.util.SpringUtils;
import com.skyeye.eve.service.IAuthUserService;
import org.flowable.common.engine.impl.interceptor.Command;
import org.flowable.common.engine.impl.interceptor.CommandContext;
import org.flowable.engine.impl.persistence.entity.ExecutionEntity;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityImpl;
import org.flowable.engine.impl.persistence.entity.ExecutionEntityManager;
import org.flowable.engine.impl.util.CommandContextUtil;
import org.flowable.task.service.impl.persistence.entity.TaskEntityImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ObjectUtils;

import java.util.*;

/**
 * @ClassName: FindMultiInstanceExecutionUserCmd
 * @Description: 查找多实例节点的执行人
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/30 22:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class FindMultiInstanceExecutionUserCmd extends AbstractCountersignCmd implements Command<List<Map<String, Object>>> {

    private static Logger LOGGER = LoggerFactory.getLogger(FindMultiInstanceExecutionUserCmd.class);

    /**
     * 当前任务ID
     */
    private String taskId;

    /**
     * true：串行多实例节点，false：并行多实例节点
     */
    private Boolean isSequential;

    private IAuthUserService iAuthUserService;

    public FindMultiInstanceExecutionUserCmd(String taskId, Boolean isSequential) {
        super();
        if (ObjectUtils.isEmpty(taskId)) {
            throw new RuntimeException("taskId 不能为空!");
        }
        this.taskId = taskId;
        this.isSequential = isSequential;
        iAuthUserService = SpringUtils.getBean(IAuthUserService.class);
    }


    @Override
    public List<Map<String, Object>> execute(CommandContext commandContext) {
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        ExecutionEntityImpl parentNode = execution.getParent();

        /**
         *  获取管理器
         */
        ExecutionEntityManager executionEntityManager = CommandContextUtil.getExecutionEntityManager(commandContext);

        List<Map<String, Object>> assigneeList;
        // true：串行多实例节点，false：并行多实例节点
        if (isSequential) {
            LOGGER.info("get SequentialMultiInstanceAssigeeList, taskId is {}", taskId);
            assigneeList = getSequentialMultiInstanceAssigeeList(taskId);
        } else {
            LOGGER.info("get ParallelMultiInstanceAssigeeList, taskId is {}", taskId);
            assigneeList = getParallelMultiInstanceAssigeeList(executionEntityManager, parentNode, task);
        }

        return assigneeList;
    }

    /**
     * 并行多实例节点获取参与人
     *
     * @param executionEntityManager 管理器
     * @param task                   任务
     */
    private List<Map<String, Object>> getParallelMultiInstanceAssigeeList(ExecutionEntityManager executionEntityManager, ExecutionEntityImpl parentNode,
                                                                          TaskEntityImpl task) {
        List<ExecutionEntity> executionEntitys = executionEntityManager.findChildExecutionsByParentExecutionId(parentNode.getId());
        List<Map<String, Object>> assigneeList = new ArrayList<>();
        executionEntitys.forEach(obj -> {
            String assignee = String.valueOf(obj.getVariableLocal(ActivitiConstants.ASSIGNEE_USER));
            Map<String, Object> user = iAuthUserService.queryDataMationById(assignee);
            // 参与人
            user.put("type", 0);
            if (assignee.equals(task.getAssignee())) {
                // 主持人
                user.put("noDelete", true);
                user.put("type", 1);
            }
            // 判断该实例是否还在运行，如果已经结束，则不能删除
            if (!obj.isActive()) {
                user.put("noDelete", true);
            }
            // 运行状态
            user.put("isActive", obj.isActive());
            if (obj.getVariableLocal(ActivitiConstants.PARALLEL_MULTILN_STANCE_EXECTTION_ISMANDATORY) != null) {
                user.put("isMandatory", 1);
            }
            // 标识是回显的数据
            user.put("echo", true);
            assigneeList.add(user);
        });
        Collections.sort(assigneeList, new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> p1, Map<String, Object> p2) {
                int a = Integer.parseInt(p1.get("type").toString());
                int b = Integer.parseInt(p2.get("type").toString());
                if (a < b) {
                    return 1;
                }
                if (a == b) {
                    return 0;
                }
                return -1;
            }
        });
        return assigneeList;
    }

    /**
     * 串行多实例节点获取参与人
     *
     * @param taskId 任务id
     */
    private List<Map<String, Object>> getSequentialMultiInstanceAssigeeList(String taskId) {
        TaskEntityImpl task = (TaskEntityImpl) taskService.createTaskQuery().taskId(taskId).singleResult();
        ExecutionEntityImpl execution = (ExecutionEntityImpl) runtimeService.createExecutionQuery().executionId(task.getExecutionId()).singleResult();
        ExecutionEntityImpl parentNode = execution.getParent();
        // 主持人应来自会签主持人变量，而不是当前审批人
        String hostAssignee = (String) runtimeService.getVariableLocal(execution.getId(), ActivitiConstants.MULTI_INSTANCE_HOST_ASSIGNEE);
        Object obj = parentNode.getVariable(ActivitiConstants.ASSIGNEE_USER_LIST);
        if (obj == null || !(obj instanceof ArrayList)) {
            LOGGER.info("not find task Executor List, task id is {}, initialization list", taskId);
            List<String> temp = new ArrayList<>();
            temp.add(task.getAssignee());
            obj = temp;
        }
        ArrayList<String> assigneeStrList = (ArrayList) obj;
        // 去重并保持原有顺序，避免重复会签人导致展示和统计异常
        assigneeStrList = new ArrayList<>(new LinkedHashSet<>(assigneeStrList));
        List<Map<String, Object>> assigneeList = new ArrayList<>();
        int index = getCurrentTaskAssigneeIndex(assigneeStrList, task.getAssignee());
        for (int i = 0; i < assigneeStrList.size(); i++) {
            String userId = assigneeStrList.get(i);
            Map<String, Object> user = iAuthUserService.queryDataMationById(userId);
            // 参与人
            user.put("type", 0);
            if (userId.equals(hostAssignee)) {
                // 主持人
                user.put("noDelete", true);
                user.put("type", 1);
            }
            // 运行状态
            user.put("isActive", true);
            if (i < index) {
                user.put("noDelete", true);
                user.put("isActive", false);
            } else if (i == index) {
                user.put("noDelete", true);
            }
            // 标识是回显的数据
            user.put("echo", true);
            assigneeList.add(user);
        }
        return assigneeList;
    }

    private int getCurrentTaskAssigneeIndex(ArrayList<String> assigneeStrList, String assignee) {
        for (int i = 0; i < assigneeStrList.size(); i++) {
            if (assignee.equals(assigneeStrList.get(i))) {
                return i;
            }
        }
        return 0;
    }

}

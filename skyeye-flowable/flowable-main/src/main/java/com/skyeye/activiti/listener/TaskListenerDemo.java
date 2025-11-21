/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.listener;

import org.flowable.engine.delegate.DelegateTask;
import org.flowable.engine.delegate.TaskListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: TaskListenerDemo
 * @Description: 任务监听器Demo（需要代码实现）
 * 
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>任务创建时自动指派负责人</li>
 *   <li>任务创建时发送通知</li>
 *   <li>任务完成时更新业务状态</li>
 *   <li>任务分配时记录日志</li>
 *   <li>任务超时时自动处理</li>
 * </ul>
 * 
 * <p><b>配置方法（在流程设计器中）：</b></p>
 * <ol>
 *   <li>打开流程设计器，选择用户任务节点</li>
 *   <li>在右侧属性面板中找到"任务监听器"（Task Listeners）</li>
 *   <li>点击"+"添加监听器，配置如下：</li>
 *   <li>
 *     <ul>
 *       <li><b>事件类型（Event）：</b>选择以下之一：</li>
 *       <li>
 *         <ul>
 *           <li><code>create</code> - 任务创建时触发</li>
 *           <li><code>assignment</code> - 任务分配时触发</li>
 *           <li><code>complete</code> - 任务完成时触发</li>
 *           <li><code>delete</code> - 任务删除时触发</li>
 *         </ul>
 *       </li>
 *       <li><b>监听器类型（Listener Type）：</b>选择 <code>Java class</code></li>
 *       <li><b>类名（Class）：</b>填写 <code>com.skyeye.activiti.listener.TaskListenerDemo</code></li>
 *     </ul>
 *   </li>
 *   <li>保存流程模型并发布</li>
 * </ol>
 * 
 * <p><b>任务监听器事件类型说明：</b></p>
 * <ul>
 *   <li><b>create：</b>任务创建后立即触发，此时任务还没有分配给任何人</li>
 *   <li><b>assignment：</b>任务分配给某人时触发（包括创建时分配和后续重新分配）</li>
 *   <li><b>complete：</b>任务完成时触发，在任务完成之前执行</li>
 *   <li><b>delete：</b>任务删除时触发（任务完成或取消时）</li>
 * </ul>
 * 
 * <p><b>重要提示：</b></p>
 * <ul>
 *   <li>任务监听器类需要在建模器中配置，但监听器类本身需要编写代码实现</li>
 *   <li>任务监听器可以访问任务信息（DelegateTask），而不是执行信息（DelegateExecution）</li>
 *   <li>任务监听器主要用于任务相关的操作，执行监听器主要用于流程执行相关的操作</li>
 * </ul>
 * 
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class TaskListenerDemo implements TaskListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(TaskListenerDemo.class);

    /**
     * 任务监听器通知方法
     * 
     * @param delegateTask 任务对象，包含任务相关信息
     * 
     * <p><b>DelegateTask 常用方法：</b></p>
     * <ul>
     *   <li><code>delegateTask.getId()</code> - 获取任务ID</li>
     *   <li><code>delegateTask.getName()</code> - 获取任务名称</li>
     *   <li><code>delegateTask.getAssignee()</code> - 获取任务负责人</li>
     *   <li><code>delegateTask.setAssignee("userId")</code> - 设置任务负责人</li>
     *   <li><code>delegateTask.getProcessInstanceId()</code> - 获取流程实例ID</li>
     *   <li><code>delegateTask.getVariable("变量名")</code> - 获取任务变量</li>
     *   <li><code>delegateTask.setVariable("变量名", 值)</code> - 设置任务变量</li>
     *   <li><code>delegateTask.getEventName()</code> - 获取事件类型（create/assignment/complete/delete）</li>
     * </ul>
     */
    @Override
    public void notify(DelegateTask delegateTask) {
        try {
            String eventName = delegateTask.getEventName();
            String taskId = delegateTask.getId();
            String taskName = delegateTask.getName();
            String assignee = delegateTask.getAssignee();
            String processInstanceId = delegateTask.getProcessInstanceId();
            
            LOGGER.info("========== 任务监听器触发 ==========");
            LOGGER.info("事件类型: {}", eventName);
            LOGGER.info("任务ID: {}", taskId);
            LOGGER.info("任务名称: {}", taskName);
            LOGGER.info("任务负责人: {}", assignee);
            LOGGER.info("流程实例ID: {}", processInstanceId);
            
            // 根据事件类型执行不同的业务逻辑
            switch (eventName) {
                case "create":
                    handleTaskCreate(delegateTask);
                    break;
                case "assignment":
                    handleTaskAssignment(delegateTask);
                    break;
                case "complete":
                    handleTaskComplete(delegateTask);
                    break;
                case "delete":
                    handleTaskDelete(delegateTask);
                    break;
                default:
                    LOGGER.warn("未知的事件类型: {}", eventName);
            }
            
            LOGGER.info("========== 任务监听器处理完成 ==========");
            
        } catch (Exception e) {
            LOGGER.error("任务监听器执行异常", e);
            // 注意：任务监听器异常可能会影响任务操作，建议处理异常
        }
    }

    /**
     * 处理任务创建事件
     * 
     * @param delegateTask 任务对象
     */
    private void handleTaskCreate(DelegateTask delegateTask) {
        LOGGER.info("任务创建事件处理 - 任务ID: {}", delegateTask.getId());
        
        // 1. 自动指派负责人（示例）
        // String autoAssignee = calculateAssignee(delegateTask);
        // delegateTask.setAssignee(autoAssignee);
        // LOGGER.info("自动指派负责人: {}", autoAssignee);
        
        // 2. 设置任务优先级（示例）
        // delegateTask.setPriority(50); // 设置优先级
        
        // 3. 设置任务到期时间（示例）
        // Date dueDate = calculateDueDate();
        // delegateTask.setDueDate(dueDate);
        
        // 4. 发送任务创建通知（示例）
        sendTaskCreateNotification(delegateTask);
        
        // 5. 记录任务创建日志（示例）
        recordTaskCreateLog(delegateTask);
    }

    /**
     * 处理任务分配事件
     * 
     * @param delegateTask 任务对象
     */
    private void handleTaskAssignment(DelegateTask delegateTask) {
        String assignee = delegateTask.getAssignee();
        LOGGER.info("任务分配事件处理 - 任务ID: {}, 负责人: {}", delegateTask.getId(), assignee);
        
        // 1. 发送任务分配通知（示例）
        sendTaskAssignmentNotification(delegateTask, assignee);
        
        // 2. 记录任务分配日志（示例）
        recordTaskAssignmentLog(delegateTask, assignee);
        
        // 3. 更新任务统计信息（示例）
        updateTaskStatistics(delegateTask, assignee);
    }

    /**
     * 处理任务完成事件
     * 
     * @param delegateTask 任务对象
     */
    private void handleTaskComplete(DelegateTask delegateTask) {
        LOGGER.info("任务完成事件处理 - 任务ID: {}", delegateTask.getId());
        
        // 1. 获取任务完成结果（示例）
        String result = (String) delegateTask.getVariable("result");
        LOGGER.info("任务完成结果: {}", result);
        
        // 2. 更新业务状态（示例）
        updateBusinessStatusAfterComplete(delegateTask, result);
        
        // 3. 发送任务完成通知（示例）
        sendTaskCompleteNotification(delegateTask, result);
        
        // 4. 记录任务完成日志（示例）
        recordTaskCompleteLog(delegateTask, result);
        
        // 5. 清理任务相关数据（示例）
        cleanupTaskData(delegateTask);
    }

    /**
     * 处理任务删除事件
     * 
     * @param delegateTask 任务对象
     */
    private void handleTaskDelete(DelegateTask delegateTask) {
        LOGGER.info("任务删除事件处理 - 任务ID: {}", delegateTask.getId());
        
        // 1. 记录任务删除日志（示例）
        recordTaskDeleteLog(delegateTask);
        
        // 2. 清理任务相关资源（示例）
        cleanupTaskResources(delegateTask);
    }

    // ========== 业务方法示例 ==========

    /**
     * 发送任务创建通知（示例方法）
     */
    private void sendTaskCreateNotification(DelegateTask delegateTask) {
        LOGGER.info("发送任务创建通知 - 任务: {}", delegateTask.getName());
        // TODO: 实现通知发送逻辑
    }

    /**
     * 记录任务创建日志（示例方法）
     */
    private void recordTaskCreateLog(DelegateTask delegateTask) {
        LOGGER.info("记录任务创建日志 - 任务ID: {}", delegateTask.getId());
        // TODO: 实现日志记录逻辑
    }

    /**
     * 发送任务分配通知（示例方法）
     */
    private void sendTaskAssignmentNotification(DelegateTask delegateTask, String assignee) {
        LOGGER.info("发送任务分配通知 - 任务: {}, 负责人: {}", delegateTask.getName(), assignee);
        // TODO: 实现通知发送逻辑
    }

    /**
     * 记录任务分配日志（示例方法）
     */
    private void recordTaskAssignmentLog(DelegateTask delegateTask, String assignee) {
        LOGGER.info("记录任务分配日志 - 任务ID: {}, 负责人: {}", delegateTask.getId(), assignee);
        // TODO: 实现日志记录逻辑
    }

    /**
     * 更新任务统计信息（示例方法）
     */
    private void updateTaskStatistics(DelegateTask delegateTask, String assignee) {
        LOGGER.info("更新任务统计信息 - 负责人: {}", assignee);
        // TODO: 实现统计信息更新逻辑
    }

    /**
     * 更新业务状态（示例方法）
     */
    private void updateBusinessStatusAfterComplete(DelegateTask delegateTask, String result) {
        LOGGER.info("更新业务状态 - 任务ID: {}, 结果: {}", delegateTask.getId(), result);
        // TODO: 实现业务状态更新逻辑
    }

    /**
     * 发送任务完成通知（示例方法）
     */
    private void sendTaskCompleteNotification(DelegateTask delegateTask, String result) {
        LOGGER.info("发送任务完成通知 - 任务: {}, 结果: {}", delegateTask.getName(), result);
        // TODO: 实现通知发送逻辑
    }

    /**
     * 记录任务完成日志（示例方法）
     */
    private void recordTaskCompleteLog(DelegateTask delegateTask, String result) {
        LOGGER.info("记录任务完成日志 - 任务ID: {}, 结果: {}", delegateTask.getId(), result);
        // TODO: 实现日志记录逻辑
    }

    /**
     * 清理任务数据（示例方法）
     */
    private void cleanupTaskData(DelegateTask delegateTask) {
        LOGGER.info("清理任务数据 - 任务ID: {}", delegateTask.getId());
        // TODO: 实现数据清理逻辑
    }

    /**
     * 记录任务删除日志（示例方法）
     */
    private void recordTaskDeleteLog(DelegateTask delegateTask) {
        LOGGER.info("记录任务删除日志 - 任务ID: {}", delegateTask.getId());
        // TODO: 实现日志记录逻辑
    }

    /**
     * 清理任务资源（示例方法）
     */
    private void cleanupTaskResources(DelegateTask delegateTask) {
        LOGGER.info("清理任务资源 - 任务ID: {}", delegateTask.getId());
        // TODO: 实现资源清理逻辑
    }
}


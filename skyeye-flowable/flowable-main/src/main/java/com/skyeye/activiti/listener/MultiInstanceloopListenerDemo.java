/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.listener;

import com.alibaba.fastjson.JSON;
import org.flowable.engine.delegate.DelegateExecution;
import org.flowable.engine.delegate.ExecutionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @ClassName: MultiInstanceloopListenerDemo
 * @Description: 会签节点监听器（Demo示例），用于监听会签节点的执行情况
 * 
 * <p><b>使用场景：</b></p>
 * <ul>
 *   <li>监听会签节点的开始、结束事件</li>
 *   <li>统计会签节点的执行进度（已完成数量/总数量）</li>
 *   <li>记录会签节点的执行日志</li>
 *   <li>在会签节点完成时执行自定义业务逻辑（如发送通知、更新状态等）</li>
 * </ul>
 * 
 * <p><b>配置方法（在流程设计器中）：</b></p>
 * <ol>
 *   <li>打开流程设计器，选择会签节点（多实例任务）</li>
 *   <li>在右侧属性面板中找到"执行监听器"（Execution Listeners）</li>
 *   <li>点击"+"添加监听器，配置如下：</li>
 *   <li>
 *     <ul>
 *       <li><b>事件类型（Event）：</b>选择 start（开始）或 end（结束）</li>
 *       <li><b>监听器类型（Listener Type）：</b>选择 "Java class"</li>
 *       <li><b>类名（Class）：</b>填写 <code>com.skyeye.activiti.listener.MultiInstanceloopListener</code></li>
 *     </ul>
 *   </li>
 *   <li>保存流程模型并发布</li>
 * </ol>
 * 
 * <p><b>监听器事件类型说明：</b></p>
 * <ul>
 *   <li><b>start：</b>会签节点开始执行时触发（每个实例开始都会触发）</li>
 *   <li><b>end：</b>会签节点执行结束时触发（每个实例结束都会触发）</li>
 *   <li><b>take：</b>顺序流被触发时执行（较少使用）</li>
 * </ul>
 * 
 * <p><b>会签相关变量说明：</b></p>
 * <ul>
 *   <li><b>nrOfInstances：</b>会签实例总数</li>
 *   <li><b>nrOfActiveInstances：</b>当前活跃的会签实例数量</li>
 *   <li><b>nrOfCompletedInstances：</b>已完成的会签实例数量</li>
 *   <li><b>loopCounter：</b>当前实例的循环计数器（从0开始）</li>
 * </ul>
 * 
 * <p><b>示例代码扩展：</b></p>
 * <pre>{@code
 * // 获取会签实例总数
 * Integer nrOfInstances = (Integer) execution.getVariable("nrOfInstances");
 * 
 * // 获取已完成实例数
 * Integer nrOfCompletedInstances = (Integer) execution.getVariable("nrOfCompletedInstances");
 * 
 * // 计算完成进度
 * double progress = (double) nrOfCompletedInstances / nrOfInstances * 100;
 * 
 * // 判断是否全部完成
 * if (nrOfCompletedInstances.equals(nrOfInstances)) {
 *     // 所有会签实例已完成，执行后续业务逻辑
 * }
 * }</pre>
 * 
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/14 22:37
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public class MultiInstanceloopListenerDemo implements ExecutionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiInstanceloopListenerDemo.class);

    /**
     * 监听器通知方法，当会签节点触发配置的事件时会被调用
     * 
     * @param execution 执行对象，包含流程实例、执行实例、流程变量等信息
     * 
     * <p><b>DelegateExecution 常用方法：</b></p>
     * <ul>
     *   <li><code>execution.getProcessInstanceId()</code> - 获取流程实例ID</li>
     *   <li><code>execution.getId()</code> - 获取当前执行实例ID</li>
     *   <li><code>execution.getCurrentActivityId()</code> - 获取当前活动节点ID</li>
     *   <li><code>execution.getVariable("变量名")</code> - 获取流程变量</li>
     *   <li><code>execution.setVariable("变量名", 值)</code> - 设置流程变量</li>
     * </ul>
     */
    @Override
    public void notify(DelegateExecution execution) {
        try {
            // 获取基本信息
            String processInstanceId = execution.getProcessInstanceId();
            String executionId = execution.getId();
            String activityId = execution.getCurrentActivityId();
            String eventName = execution.getEventName(); // start 或 end
            
            LOGGER.info("========== 会签节点监听器触发 ==========");
            LOGGER.info("流程实例ID: {}", processInstanceId);
            LOGGER.info("执行实例ID: {}", executionId);
            LOGGER.info("活动节点ID: {}", activityId);
            LOGGER.info("事件类型: {}", eventName);
            
            // 获取会签相关变量（如果存在）
            Object nrOfInstances = execution.getVariable("nrOfInstances");
            Object nrOfActiveInstances = execution.getVariable("nrOfActiveInstances");
            Object nrOfCompletedInstances = execution.getVariable("nrOfCompletedInstances");
            Object loopCounter = execution.getVariable("loopCounter");
            
            if (nrOfInstances != null) {
                LOGGER.info("会签实例总数: {}", nrOfInstances);
                LOGGER.info("活跃实例数: {}", nrOfActiveInstances);
                LOGGER.info("已完成实例数: {}", nrOfCompletedInstances);
                LOGGER.info("当前循环计数: {}", loopCounter);
                
                // 计算完成进度（示例）
                if (nrOfInstances instanceof Integer && nrOfCompletedInstances instanceof Integer) {
                    int total = (Integer) nrOfInstances;
                    int completed = (Integer) nrOfCompletedInstances;
                    if (total > 0) {
                        double progress = (double) completed / total * 100;
                        LOGGER.info("会签完成进度: {}%", String.format("%.2f", progress));
                        
                        // 如果全部完成，可以在这里执行自定义业务逻辑
                        if (completed == total && "end".equals(eventName)) {
                            LOGGER.info("所有会签实例已完成！可以在这里执行后续业务逻辑");
                            // 在这里添加你的业务逻辑，例如：
                            // - 发送通知
                            // - 更新业务状态
                            // - 调用外部服务
                            // - 记录日志到数据库
                        }
                    }
                }
            }
            
            // 打印完整的执行对象信息（用于调试，生产环境建议注释掉）
            LOGGER.debug("执行对象详细信息: {}", JSON.toJSONString(execution));
            
            LOGGER.info("========== 会签节点监听器执行完成 ==========");
            
        } catch (Exception e) {
            LOGGER.error("会签节点监听器执行异常", e);
            // 注意：监听器异常不会中断流程执行，但建议处理异常避免影响业务
        }
    }

}

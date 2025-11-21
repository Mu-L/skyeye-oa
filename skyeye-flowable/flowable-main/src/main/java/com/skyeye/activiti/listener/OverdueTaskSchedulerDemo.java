/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.activiti.listener;

import org.flowable.engine.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @ClassName: OverdueTaskSchedulerDemo
 * @Description: 超期任务定时查询调度器Demo
 * 
 * <p><b>使用说明：</b></p>
 * <ul>
 *   <li>这是一个定时任务调度器，用于定期查询和处理超期任务</li>
 *   <li>需要在 Spring Boot 启动类上添加 <code>@EnableScheduling</code> 注解</li>
 *   <li>定时任务会自动执行，无需手动调用</li>
 * </ul>
 * 
 * <p><b>配置定时任务执行频率：</b></p>
 * <ul>
 *   <li>修改 <code>@Scheduled</code> 注解中的参数来调整执行频率</li>
 *   <li><code>fixedRate = 300000</code> - 每5分钟执行一次（单位：毫秒）</li>
 *   <li><code>fixedDelay = 300000</code> - 上次执行完成后延迟5分钟再执行</li>
 *   <li><code>cron = "0 0/5 * * * ?"</code> - 使用 Cron 表达式（每5分钟执行一次）</li>
 * </ul>
 * 
 * <p><b>Cron 表达式示例：</b></p>
 * <ul>
 *   <li><code>"0 0/5 * * * ?"</code> - 每5分钟执行一次</li>
 *   <li><code>"0 0 * * * ?"</code> - 每小时执行一次</li>
 *   <li><code>"0 0 9 * * ?"</code> - 每天上午9点执行</li>
 *   <li><code>"0 0 9,17 * * ?"</code> - 每天上午9点和下午5点执行</li>
 * </ul>
 * 
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class OverdueTaskSchedulerDemo {

    private static final Logger LOGGER = LoggerFactory.getLogger(OverdueTaskSchedulerDemo.class);

    @Autowired(required = false)
    private TaskService taskService;

    @Autowired(required = false)
    private OverdueTaskQueryHandlerDemo overdueTaskHandler;

    /**
     * 定时查询和处理超期任务
     * 
     * <p>执行频率：每5分钟执行一次</p>
     * <p>可以根据实际需求调整执行频率</p>
     */
    @Scheduled(fixedRate = 300000) // 每5分钟执行一次（300000毫秒 = 5分钟）
    // @Scheduled(cron = "0 0/5 * * * ?") // 使用 Cron 表达式，每5分钟执行一次
    public void checkOverdueTasks() {
        try {
            LOGGER.info("定时任务开始执行 - 查询超期任务");
            
            // 检查依赖是否注入
            if (overdueTaskHandler == null) {
                LOGGER.warn("OverdueTaskQueryHandlerDemo 未注入，跳过执行");
                return;
            }
            
            // 调用处理器查询和处理超期任务
            overdueTaskHandler.queryAndProcessOverdueTasks();
            
            LOGGER.info("定时任务执行完成");
            
        } catch (Exception e) {
            LOGGER.error("定时任务执行异常", e);
        }
    }

    /**
     * 生成超期任务统计报告
     * 
     * <p>执行频率：每天上午9点执行</p>
     */
    @Scheduled(cron = "0 0 9 * * ?") // 每天上午9点执行
    public void generateDailyReport() {
        try {
            LOGGER.info("定时任务开始执行 - 生成超期任务统计报告");
            
            if (overdueTaskHandler == null) {
                LOGGER.warn("OverdueTaskQueryHandlerDemo 未注入，跳过执行");
                return;
            }
            
            // 生成统计报告
            overdueTaskHandler.generateOverdueTaskReport();
            
            LOGGER.info("统计报告生成完成");
            
        } catch (Exception e) {
            LOGGER.error("生成统计报告异常", e);
        }
    }
}


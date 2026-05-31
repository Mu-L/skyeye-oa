/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jmx.export.annotation.ManagedResource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

/**
 * @ClassName: ExecutorConfig
 * @Description: 报表模块异步任务配置
 */
@Configuration
@ManagedResource
public class ExecutorConfig {

    /**
     * 报表预览批量取数线程池。
     * <p>
     * 配置风格与 promote 模块 codeRuleExecutor 保持一致；
     * IO 密集（REST/SQL 外部调用），核心线程数不宜过大，避免打满下游服务。
     */
    @Bean(name = "reportDataBatchExecutor")
    public Executor getReportDataBatchExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        // 预览页单次批量条目有限，大队列用于削峰、避免 CallerRuns 阻塞 Tomcat 线程
        executor.setQueueCapacity(1000000);
        executor.setThreadNamePrefix("reportDataBatchExecutor-");
        executor.initialize();
        return executor;
    }
}

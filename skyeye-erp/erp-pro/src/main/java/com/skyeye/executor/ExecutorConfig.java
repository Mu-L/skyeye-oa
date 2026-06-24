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
 * 派工通知发送邮件异步任务配置类
 */
@Configuration
@ManagedResource
public class ExecutorConfig {

    @Bean(name = "watiWorkerSendEmailExecutor")
    public Executor getDetailsAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10);
        executor.setQueueCapacity(1000000);
        executor.setThreadNamePrefix("watiWorkerSendEmailExecutor-");
        executor.initialize();
        return executor;
    }

}

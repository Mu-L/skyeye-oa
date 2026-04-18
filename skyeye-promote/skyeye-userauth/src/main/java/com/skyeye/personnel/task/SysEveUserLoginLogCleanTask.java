package com.skyeye.personnel.task;

import com.skyeye.personnel.service.SysEveUserLoginLogService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 用户登录日志定时清理任务
 */
@Slf4j
@Component
public class SysEveUserLoginLogCleanTask {

    /**
     * 单批删除量，避免一次性删除太多行导致长事务和锁等待。
     */
    private static final int DELETE_BATCH_SIZE = 500;

    /**
     * 单次任务最大批次数，避免任务运行时间过长。
     */
    private static final int MAX_BATCHES_PER_RUN = 200;

    /**
     * 仅保留最近 6 个月登录日志。
     */
    private static final int RETAIN_MONTHS = 6;

    @Autowired
    private SysEveUserLoginLogService sysEveUserLoginLogService;

    /**
     * 用户登录日志清理任务（由 XXL-Job 调度中心配置执行策略/cron）。
     */
    @XxlJob("sysEveUserLoginLogCleanTask")
    public void cleanExpiredUserLoginLogs() {
        int totalDeleted = 0;
        for (int i = 0; i < MAX_BATCHES_PER_RUN; i++) {
            int deleted = sysEveUserLoginLogService.cleanExpiredLoginLogs(DELETE_BATCH_SIZE, RETAIN_MONTHS);
            totalDeleted += deleted;
            if (deleted < DELETE_BATCH_SIZE) {
                break;
            }
        }
        log.info("用户登录日志定时清理完成，本次删除数量：{}", totalDeleted);
    }
}

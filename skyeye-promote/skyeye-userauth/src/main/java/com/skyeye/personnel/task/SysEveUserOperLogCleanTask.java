package com.skyeye.personnel.task;

import com.skyeye.personnel.service.SysEveUserOperLogService;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 系统操作日志定时清理任务
 */
@Slf4j
@Component
public class SysEveUserOperLogCleanTask {

    /**
     * 单批删除量，避免一次性删除太多行导致长事务和锁等待。
     */
    private static final int DELETE_BATCH_SIZE = 500;

    /**
     * 单次任务最大批次数，避免任务运行时间过长。
     */
    private static final int MAX_BATCHES_PER_RUN = 200;

    /**
     * 仅保留最近 6 个月操作日志。
     */
    private static final int RETAIN_MONTHS = 6;

    @Autowired
    private SysEveUserOperLogService sysEveUserOperLogService;

    /**
     * 系统操作日志清理任务（由 XXL-Job 调度中心配置执行策略/cron）。
     */
    @XxlJob("sysEveUserOperLogCleanTask")
    public void cleanExpiredOperLogs() {
        int totalDeleted = 0;
        for (int i = 0; i < MAX_BATCHES_PER_RUN; i++) {
            int deleted = sysEveUserOperLogService.cleanExpiredOperationLogs(DELETE_BATCH_SIZE, RETAIN_MONTHS);
            totalDeleted += deleted;
            if (deleted < DELETE_BATCH_SIZE) {
                break;
            }
        }
        log.info("系统操作日志定时清理完成，本次删除数量：{}", totalDeleted);
    }
}

package com.skyeye.office.task;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.office.entity.DocumentOnlineUser;
import com.skyeye.office.service.DocumentOnlineUserService;
import com.skyeye.office.websocket.WebSocketSessionManager;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: DocumentOnlineUserCleanTask
 * @Description: 清理不活跃在线用户的定时任务
 * 1. 清理超时的WebSocket会话
 * 2. 清理数据库中的不活跃用户记录
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Slf4j
@Component
public class DocumentOnlineUserCleanTask {

    @Autowired
    private DocumentOnlineUserService documentOnlineUserService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    /**
     * 清理不活跃用户任务
     * 通过XXL-JOB调度执行，任务名：documentOnlineUserCleanTask
     * 清理规则：
     * 1. 清理超过10分钟未活动的WebSocket会话
     * 2. 清理超过10分钟未更新的在线用户记录
     */
    @XxlJob("documentOnlineUserCleanTask")
    public void cleanInactiveUsers() {
        try {
            log.info("开始清理不活跃用户...");
            
            // 清理超时的WebSocket会话（10分钟）
            sessionManager.cleanTimeoutSessions(TimeUnit.MINUTES.toMillis(10));
            
            // 获取10分钟前的时间点
            Date inactiveTime = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10));

            // 删除超过10分钟未活跃的用户
            QueryWrapper<DocumentOnlineUser> wrapper = new QueryWrapper<>();
            wrapper.lt("last_active_time", inactiveTime);

            int count = documentOnlineUserService.deleteByWrapper(wrapper);
            log.info("清理完成，共清理{}个不活跃用户", count);
        } catch (Exception e) {
            log.error("清理不活跃用户失败", e);
        }
    }
} 
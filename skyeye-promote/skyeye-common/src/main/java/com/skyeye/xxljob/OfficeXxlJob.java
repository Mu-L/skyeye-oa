package com.skyeye.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.office.service.DocumentOnlineUserService;
import com.skyeye.office.websocket.WebSocketSessionManager;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: DocumentXxlJob
 * @Description: 清理不活跃在线用户的定时任务
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/11 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
public class OfficeXxlJob {
    @Autowired
    private DocumentOnlineUserService documentOnlineUserService;

    @Autowired
    private WebSocketSessionManager sessionManager;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;


    /**
     * 清理不活跃用户任务
     * 通过XXL-JOB调度执行，任务名：documentOnlineUserCleanTask
     * 清理规则：
     * 1. 清理超过10分钟未活动的WebSocket会话
     * 2. 清理超过10分钟未更新的在线用户记录
     */
    @XxlJob("deleteOfficeDocumentOnlineUserService")
    public void cleanInactiveUsers() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            log.info("开始清理不活跃用户...");
            // 清理超时的WebSocket会话（10分钟）
            sessionManager.cleanTimeoutSessions(TimeUnit.MINUTES.toMillis(10));
            // 清理超过10分钟未更新的在线用户记录
            int count = documentOnlineUserService.deleteOverTimeUser();
            log.info("清理完成，共清理{}个不活跃用户", count);
        } catch (Exception e) {
            log.error("清理不活跃用户失败", e);
        }
    }
}


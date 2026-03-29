/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.patrol.service.PatrolTaskPlanSyncService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 由 SysQuartz 按巡检计划注册的 XXL 子任务：仅根据 objectId(计划 id) 生成待执行巡检任务。
 * 与日程 {@code MyScheduleDayMationService} 相同，任务参数为 JSON：objectId、userId、tenantId。
 *
 * @author skyeye云系列--卫志强
 * @date 2026/03/28
 */
@Slf4j
@Component
public class PatrolPlanTaskGenerateService {

    @Autowired
    private PatrolTaskPlanSyncService patrolTaskPlanSyncService;

    @Value("${skyeye.tenant.enable:false}")
    private boolean tenantEnable;

    @XxlJob("patrolPlanTaskGenerateService")
    public void generatePatrolTasks() {
        String param = XxlJobHelper.getJobParam();
        if (StrUtil.isBlank(param)) {
            log.warn("巡检计划任务生成：执行参数为空");
            return;
        }
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String planId = paramMap.get("objectId");
        if (StrUtil.isBlank(planId)) {
            log.warn("巡检计划任务生成：objectId 为空");
            return;
        }
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        try {
            patrolTaskPlanSyncService.generatePatrolTasksForPlan(planId);
        } catch (Exception e) {
            log.warn("巡检计划[{}]定时生成任务失败", planId, e);
        }
    }
}

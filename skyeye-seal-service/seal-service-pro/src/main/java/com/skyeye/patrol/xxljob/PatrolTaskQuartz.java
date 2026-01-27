/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.patrol.classenum.PatrolTaskState;
import com.skyeye.patrol.dao.PatrolTaskDao;
import com.skyeye.patrol.entity.PatrolTask;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PatrolTaskQuartz
 * @Description: 巡检任务定时任务，检查并处理超时任务
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
public class PatrolTaskQuartz {

    @Autowired
    private PatrolTaskDao patrolTaskDao;

    @Autowired
    private ITenantService iTenantService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 检查并处理超时的巡检任务
     * 建议执行频率：每小时执行一次
     */
    @XxlJob("patrolTaskTimeoutQuartz")
    public void checkTimeoutTasks() {
        log.info("开始检查超时的巡检任务");
        try {
            if (tenantEnable) {
                // 开启多租户
                List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
                if (CollectionUtil.isEmpty(tenantList)) {
                    return;
                }
                tenantList.forEach(tenant -> {
                    String tenantId = tenant.get("id").toString();
                    TenantContext.setTenantId(tenantId);
                    checkTimeoutTasksForTenant();
                });
            } else {
                // 未开启多租户
                checkTimeoutTasksForTenant();
            }
        } catch (Exception e) {
            log.warn("检查超时巡检任务失败", e);
        }
        log.info("检查超时的巡检任务结束");
    }

    /**
     * 检查并处理当前租户的超时任务
     */
    private void checkTimeoutTasksForTenant() {
        String currentTime = DateUtil.getTimeAndToString();
        // 查询所有待执行状态且计划开始执行时间已过的任务
        QueryWrapper<PatrolTask> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTask::getState), PatrolTaskState.PENDING.getKey());
        queryWrapper.lt(MybatisPlusUtil.toColumns(PatrolTask::getPlannedStartTime), currentTime);
        List<PatrolTask> timeoutTasks = patrolTaskDao.selectList(queryWrapper);

        if (CollectionUtil.isEmpty(timeoutTasks)) {
            log.info("当前租户没有超时的待执行任务");
            return;
        }

        log.info("发现 {} 个超时的待执行任务，开始标记为已超时", timeoutTasks.size());

        // 批量更新为已超时状态
        List<String> taskIds = timeoutTasks.stream().map(PatrolTask::getId).collect(java.util.stream.Collectors.toList());
        UpdateWrapper<PatrolTask> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(MybatisPlusUtil.toColumns(PatrolTask::getId), taskIds);
        updateWrapper.set(MybatisPlusUtil.toColumns(PatrolTask::getState), PatrolTaskState.TIMEOUT.getKey());
        patrolTaskDao.update(null, updateWrapper);

        log.info("成功将 {} 个任务标记为已超时", timeoutTasks.size());
    }
}


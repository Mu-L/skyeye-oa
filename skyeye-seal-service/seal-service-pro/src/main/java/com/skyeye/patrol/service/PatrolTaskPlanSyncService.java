/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service;

/**
 * 巡检计划与系统生成任务的同步：由 XXL 子任务按 planId 生成实例；计划保存/删除时回收任务。
 */
public interface PatrolTaskPlanSyncService {

    /**
     * 按指定计划与频次，在滚动时间窗内生成待执行任务（幂等）。由动态注册的 XXL 任务调用。
     */
    void generatePatrolTasksForPlan(String planId);

}

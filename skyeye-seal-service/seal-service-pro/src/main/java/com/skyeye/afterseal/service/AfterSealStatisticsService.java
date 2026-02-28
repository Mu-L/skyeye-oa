/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 工单统计服务接口：售后工单维度统计（状态、完成率、区域、紧急程度、项目等）
 */
public interface AfterSealStatisticsService {

    /**
     * 工单按状态统计：各状态数量（待派工、待接单、待签到、待完工、待评价、待审核、已完工）
     */
    void queryOrderStateStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 工单完成率统计：时间范围内总工单数、已完工数、完成率
     */
    void queryOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 工单按区域统计：按省/市维度统计工单数量
     */
    void queryOrderStatsByRegion(InputObject inputObject, OutputObject outputObject);

    /**
     * 工单按紧急程度统计：按 urgencyId 分组数量
     */
    void queryOrderStatsByUrgency(InputObject inputObject, OutputObject outputObject);

    /**
     * 工单按项目统计：按 projectId 分组数量
     */
    void queryOrderStatsByProject(InputObject inputObject, OutputObject outputObject);
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.service;

import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;

/**
 * 报修维修统计服务接口层（结构与 seal 模块 {@link com.skyeye.patrol.service.PatrolStatisticsService} 对齐：多接口、各接口独立 {@code setBean}）
 *
 * @author skyeye云系列--卫志强
 */
public interface EquipmentRepairStatisticsService {



    /**
     * 按派工时间的月度趋势（xAxisData + seriesData + total）
     */
    void queryRepairMonthlyTrendStats(InputObject inputObject, OutputObject outputObject);

    /**
     * 按设备名称统计维修单数（全量，不按起止时间筛选；xAxisData + seriesData + total）
     */
    void queryRepairStatsByEquipmentName(InputObject inputObject, OutputObject outputObject);

    /**
     * 按设备名称分页查询维修单（入参 {@link com.skyeye.repair.entity.EquipmentRepairOrderByNamePageInfo}）
     */
    void queryRepairOrderPageListByEquipmentName(InputObject inputObject, OutputObject outputObject);
}

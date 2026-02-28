/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.controller;

import com.skyeye.afterseal.service.AfterSealStatisticsService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 工单统计控制层：售后工单维度统计接口（状态、完成率、区域、紧急程度、项目）
 */
@RestController
@Api(value = "工单统计", tags = "工单统计", modelName = "工单统计")
public class AfterSealStatisticsController {

    @Autowired
    private AfterSealStatisticsService afterSealStatisticsService;

    @ApiOperation(id = "queryOrderStateStats", value = "工单按状态统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealStatisticsController/queryOrderStateStats")
    public void queryOrderStateStats(InputObject inputObject, OutputObject outputObject) {
        afterSealStatisticsService.queryOrderStateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderCompletionRateStats", value = "工单完成率统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealStatisticsController/queryOrderCompletionRateStats")
    public void queryOrderCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        afterSealStatisticsService.queryOrderCompletionRateStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderStatsByRegion", value = "工单按区域统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealStatisticsController/queryOrderStatsByRegion")
    public void queryOrderStatsByRegion(InputObject inputObject, OutputObject outputObject) {
        afterSealStatisticsService.queryOrderStatsByRegion(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderStatsByUrgency", value = "工单按紧急程度统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealStatisticsController/queryOrderStatsByUrgency")
    public void queryOrderStatsByUrgency(InputObject inputObject, OutputObject outputObject) {
        afterSealStatisticsService.queryOrderStatsByUrgency(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOrderStatsByProject", value = "工单按项目统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AfterSealStatisticsController/queryOrderStatsByProject")
    public void queryOrderStatsByProject(InputObject inputObject, OutputObject outputObject) {
        afterSealStatisticsService.queryOrderStatsByProject(inputObject, outputObject);
    }
}

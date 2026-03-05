/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.service.PatrolStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolStatisticsController
 * @Description: 巡检统计控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检统计", tags = "巡检统计", modelName = "巡检统计")
public class PatrolStatisticsController {

    @Autowired
    private PatrolStatisticsService patrolStatisticsService;

    @ApiOperation(id = "queryTaskCompletionStats", value = "任务完成情况统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryTaskCompletionStats")
    public void queryTaskCompletionStats(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryTaskCompletionStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAbnormalStats", value = "异常情况统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryAbnormalStats")
    public void queryAbnormalStats(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryAbnormalStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStatsByTeam", value = "按班组统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryStatsByTeam")
    public void queryStatsByTeam(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryStatsByTeam(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStatsByPoint", value = "按点位统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryStatsByPoint")
    public void queryStatsByPoint(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryStatsByPoint(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStatsByItem", value = "按项目统计巡检记录", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryStatsByItem")
    public void queryStatsByItem(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryStatsByItem(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStatsByExecutor", value = "按执行人统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryStatsByExecutor")
    public void queryStatsByExecutor(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryStatsByExecutor(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTimeTrendStats", value = "时间维度统计总任务数，已完成任务数（趋势）", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryTimeTrendStats")
    public void queryTimeTrendStats(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryTimeTrendStats(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCompletionRateStats", value = "完成率统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/PatrolStatisticsController/queryCompletionRateStats")
    public void queryCompletionRateStats(InputObject inputObject, OutputObject outputObject) {
        patrolStatisticsService.queryCompletionRateStats(inputObject, outputObject);
    }

}


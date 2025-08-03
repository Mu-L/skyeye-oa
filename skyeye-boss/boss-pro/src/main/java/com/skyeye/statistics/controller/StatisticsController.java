/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.statistics.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StatisticsController
 * @Description: BOSS统计模块
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/3 15:17
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "BOSS统计模块", tags = "BOSS统计模块", modelName = "BOSS统计模块")
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @ApiOperation(id = "getRecruitmentOverview", value = "获取招聘数据概览统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startDate", name = "startDate", value = "开始日期，格式：yyyy-MM-dd"),
        @ApiImplicitParam(id = "endDate", name = "endDate", value = "结束日期，格式：yyyy-MM-dd")
    })
    @RequestMapping("/post/StatisticsController/getRecruitmentOverview")
    public void getRecruitmentOverview(InputObject inputObject, OutputObject outputObject) {
        statisticsService.getRecruitmentOverview(inputObject, outputObject);
    }

    @ApiOperation(id = "getIntervieweeStatusDistribution", value = "获取面试者状态分布统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startDate", name = "startDate", value = "开始日期，格式：yyyy-MM-dd"),
        @ApiImplicitParam(id = "endDate", name = "endDate", value = "结束日期，格式：yyyy-MM-dd")
    })
    @RequestMapping("/post/StatisticsController/getIntervieweeStatusDistribution")
    public void getIntervieweeStatusDistribution(InputObject inputObject, OutputObject outputObject) {
        statisticsService.getIntervieweeStatusDistribution(inputObject, outputObject);
    }

    @ApiOperation(id = "getPersonRequireCompletion", value = "获取人员需求完成情况统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "departmentId", name = "departmentId", value = "部门ID"),
        @ApiImplicitParam(id = "startDate", name = "startDate", value = "开始日期，格式：yyyy-MM-dd"),
        @ApiImplicitParam(id = "endDate", name = "endDate", value = "结束日期，格式：yyyy-MM-dd")
    })
    @RequestMapping("/post/StatisticsController/getPersonRequireCompletion")
    public void getPersonRequireCompletion(InputObject inputObject, OutputObject outputObject) {
        statisticsService.getPersonRequireCompletion(inputObject, outputObject);
    }

    @ApiOperation(id = "getRecruitmentChannelEffect", value = "获取招聘渠道效果统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startDate", name = "startDate", value = "开始日期，格式：yyyy-MM-dd"),
        @ApiImplicitParam(id = "endDate", name = "endDate", value = "结束日期，格式：yyyy-MM-dd")
    })
    @RequestMapping("/post/StatisticsController/getRecruitmentChannelEffect")
    public void getRecruitmentChannelEffect(InputObject inputObject, OutputObject outputObject) {
        statisticsService.getRecruitmentChannelEffect(inputObject, outputObject);
    }

    @ApiOperation(id = "getDepartmentRecruitmentStats", value = "获取部门招聘统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startDate", name = "startDate", value = "开始日期，格式：yyyy-MM-dd"),
        @ApiImplicitParam(id = "endDate", name = "endDate", value = "结束日期，格式：yyyy-MM-dd")
    })
    @RequestMapping("/post/StatisticsController/getDepartmentRecruitmentStats")
    public void getDepartmentRecruitmentStats(InputObject inputObject, OutputObject outputObject) {
        statisticsService.getDepartmentRecruitmentStats(inputObject, outputObject);
    }

    @ApiOperation(id = "getMonthlyRecruitmentTrend", value = "获取月度招聘趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "year", name = "year", value = "指定年份，格式：yyyy", required = "required"),
    })
    @RequestMapping("/post/StatisticsController/getMonthlyRecruitmentTrend")
    public void getMonthlyRecruitmentTrend(InputObject inputObject, OutputObject outputObject) {
        statisticsService.getMonthlyRecruitmentTrend(inputObject, outputObject);
    }

    @ApiOperation(id = "getRegularAndQuitStats", value = "获取转正和离职统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startDate", name = "startDate", value = "开始日期，格式：yyyy-MM-dd"),
        @ApiImplicitParam(id = "endDate", name = "endDate", value = "结束日期，格式：yyyy-MM-dd")
    })
    @RequestMapping("/post/StatisticsController/getRegularAndQuitStats")
    public void getRegularAndQuitStats(InputObject inputObject, OutputObject outputObject) {
        statisticsService.getRegularAndQuitStats(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.afterseal.controller;

import com.skyeye.afterseal.service.ProjectInstallerCommissionService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProjectInstallerCommissionController
 * @Description: 安装员提成控制类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24 12:00
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "安装员提成", tags = "安装员提成", modelName = "售后工单")
public class ProjectInstallerCommissionController {

    @Autowired
    private ProjectInstallerCommissionService projectInstallerCommissionService;

    @ApiOperation(id = "queryProjectInstallerCommissionList", value = "获取安装员提成列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProjectInstallerCommissionController/queryProjectInstallerCommissionList")
    public void queryProjectInstallerCommissionList(InputObject inputObject, OutputObject outputObject) {
        projectInstallerCommissionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCommissionStatistics", value = "获取提成统计数据（根据项目ID）", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "projectId", name = "projectId", value = "项目ID", required = "required")})
    @RequestMapping("/post/ProjectInstallerCommissionController/queryCommissionStatistics")
    public void queryCommissionStatistics(InputObject inputObject, OutputObject outputObject) {
        projectInstallerCommissionService.queryCommissionStatistics(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCommissionStatisticsByDispatchId", value = "根据工单ID查询提成统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "dispatchId", name = "dispatchId", value = "工单ID", required = "required")})
    @RequestMapping("/post/ProjectInstallerCommissionController/queryCommissionStatisticsByDispatchId")
    public void queryCommissionStatisticsByDispatchId(InputObject inputObject, OutputObject outputObject) {
        projectInstallerCommissionService.queryCommissionStatisticsByDispatchId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryInstallerDashboard", value = "查询安装员考核大屏数据", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "startDate", name = "startDate", value = "开始日期", required = "required"),
        @ApiImplicitParam(id = "endDate", name = "endDate", value = "结束日期", required = "required"),
        @ApiImplicitParam(id = "status", name = "status", value = "项目状态：all-全部，completed-已完成，uncompleted-未完成", required = "required")})
    @RequestMapping("/post/ProjectInstallerCommissionController/queryInstallerDashboard")
    public void queryInstallerDashboard(InputObject inputObject, OutputObject outputObject) {
        projectInstallerCommissionService.queryInstallerDashboard(inputObject, outputObject);
    }

}


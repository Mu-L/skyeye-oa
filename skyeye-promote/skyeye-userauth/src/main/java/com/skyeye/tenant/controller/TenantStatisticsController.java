/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.service.TenantStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 租户统计分析控制层：9 个统计接口，便于报表 3x3 等布局调整
 */
@RestController
@Api(value = "租户统计分析", tags = "租户统计分析", modelName = "租户管理")
public class TenantStatisticsController {

    @Autowired
    private TenantStatisticsService tenantStatisticsService;

    @ApiOperation(id = "queryTenantTotal", value = "租户总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantTotal")
    public void queryTenantTotal(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantUserTotal", value = "租户用户总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantUserTotal")
    public void queryTenantUserTotal(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantUserTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantOrderTotal", value = "租户订单总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantOrderTotal")
    public void queryTenantOrderTotal(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantOrderTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantInviteTotal", value = "租户邀请总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantInviteTotal")
    public void queryTenantInviteTotal(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantInviteTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantAppTotal", value = "租户应用总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantAppTotal")
    public void queryTenantAppTotal(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantAppTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantStatsByCreateTime", value = "租户按创建时间趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantStatsByCreateTime")
    public void queryTenantStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantStatsByCreateTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantUserStatsByState", value = "租户用户按在职状态统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantUserStatsByState")
    public void queryTenantUserStatsByState(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantUserStatsByState(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantOrderStatsByTenant", value = "租户订单按租户统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantOrderStatsByTenant")
    public void queryTenantOrderStatsByTenant(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantOrderStatsByTenant(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantInviteStatsByUsed", value = "租户邀请按是否使用统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/TenantStatisticsController/queryTenantInviteStatsByUsed")
    public void queryTenantInviteStatsByUsed(InputObject inputObject, OutputObject outputObject) {
        tenantStatisticsService.queryTenantInviteStatsByUsed(inputObject, outputObject);
    }
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.tenant.entity.TenantApp;
import com.skyeye.tenant.service.TenantAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: TenantAppController
 * @Description: 租户应用管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 16:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "租户应用管理", tags = "租户应用管理", modelName = "租户管理")
public class TenantAppController {

    @Autowired
    private TenantAppService tenantAppService;

    @ApiOperation(id = "queryTenantAppList", value = "获取租户应用列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/TenantAppController/queryTenantAppList")
    public void queryTenantAppList(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeTenantApp", value = "新增/编辑租户应用", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = TenantApp.class)
    @RequestMapping("/post/TenantAppController/writeTenantApp")
    public void writeTenantApp(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantAppById", value = "根据id查询租户应用信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "租户应用ID", required = "required")})
    @RequestMapping("/post/TenantAppController/queryTenantAppById")
    public void queryTenantAppById(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteTenantAppById", value = "删除租户应用", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "租户应用ID", required = "required")})
    @RequestMapping("/post/TenantAppController/deleteTenantAppById")
    public void deleteTenantAppById(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantAppBandMenuList", value = "获取所有模块(桌面)/菜单/权限点/分组/数据权限列表，只有平台租户才能调用", method = "GET", allUse = "2")
    @RequestMapping("/post/TenantAppController/queryTenantAppBandMenuList")
    public void queryTenantAppBandMenuList(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.queryTenantAppBandMenuList(inputObject, outputObject);
    }

    @ApiOperation(id = "editTenantAppPCAuth", value = "编辑租户应用PC端权限", method = "PUT", allUse = "1")
    @ApiImplicitParams(classBean = TenantApp.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/TenantAppController/editTenantAppPCAuth")
    public void editTenantAppPCAuth(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.editTenantAppPCAuth(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTenantAppBandAppMenuList", value = "获取租户应用需要绑定的手机端菜单列表", method = "GET", allUse = "2")
    @RequestMapping("/post/TenantAppController/queryTenantAppBandAppMenuList")
    public void queryTenantAppBandAppMenuList(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.queryTenantAppBandAppMenuList(inputObject, outputObject);
    }

    @ApiOperation(id = "editTenantAppAppMenuById", value = "手机端菜单授权", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TenantApp.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/TenantAppController/editTenantAppAppMenuById")
    public void editTenantAppAppMenuById(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.editTenantAppAppMenuById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllTenantAppList", value = "获取所有租户应用列表", method = "GET", allUse = "2")
    @RequestMapping("/post/TenantAppController/queryAllTenantAppList")
    public void queryAllTenantAppList(InputObject inputObject, OutputObject outputObject) {
        tenantAppService.queryAllTenantAppList(inputObject, outputObject);
    }

}

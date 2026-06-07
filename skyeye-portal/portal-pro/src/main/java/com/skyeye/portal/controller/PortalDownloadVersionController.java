/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.portal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.portal.entity.PortalDownloadVersion;
import com.skyeye.portal.service.PortalDownloadVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 官网下载中心版本接口
 */
@RestController
@Api(value = "官网下载中心", tags = "官网下载中心", modelName = "门户管理")
public class PortalDownloadVersionController {

    @Autowired
    private PortalDownloadVersionService portalDownloadVersionService;

    @ApiOperation(id = "queryPortalDownloadVersionList", value = "分页查询下载中心版本", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PortalDownloadVersionController/queryPortalDownloadVersionList")
    public void queryPortalDownloadVersionList(InputObject inputObject, OutputObject outputObject) {
        portalDownloadVersionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePortalDownloadVersion", value = "新增/编辑下载中心版本", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PortalDownloadVersion.class)
    @RequestMapping("/post/PortalDownloadVersionController/writePortalDownloadVersion")
    public void writePortalDownloadVersion(InputObject inputObject, OutputObject outputObject) {
        portalDownloadVersionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePortalDownloadVersionById", value = "删除下载中心版本", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PortalDownloadVersionController/deletePortalDownloadVersionById")
    public void deletePortalDownloadVersionById(InputObject inputObject, OutputObject outputObject) {
        portalDownloadVersionService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPortalDownloadVersionById", value = "根据id获取下载中心版本", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PortalDownloadVersionController/queryPortalDownloadVersionById")
    public void queryPortalDownloadVersionById(InputObject inputObject, OutputObject outputObject) {
        portalDownloadVersionService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnabledPortalDownloadVersionList", value = "获取已启用下载中心版本（官网展示）", method = "POST", allUse = "0")
    @RequestMapping("/post/PortalDownloadVersionController/queryEnabledPortalDownloadVersionList")
    public void queryEnabledPortalDownloadVersionList(InputObject inputObject, OutputObject outputObject) {
        portalDownloadVersionService.queryEnabledPortalDownloadVersionList(inputObject, outputObject);
    }
}

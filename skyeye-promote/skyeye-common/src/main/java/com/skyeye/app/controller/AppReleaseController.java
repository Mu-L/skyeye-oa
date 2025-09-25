/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.app.service.AppReleaseService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppReleaseController
 * @Description: APP发布信息
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 14:56
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "APP发布信息", tags = "APP发布信息", modelName = "APP版本发布模块")
public class AppReleaseController {

    @Autowired
    private AppReleaseService appReleaseService;

    @ApiOperation(id = "queryAppReleaseList", value = "查询项目APP发布列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AppReleaseController/queryAppReleaseList")
    public void queryAppReleaseList(InputObject inputObject, OutputObject outputObject) {
        appReleaseService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "updateAppReleaseStateById", value = "修改APP发布状态", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "状态", required = "required")})
    @RequestMapping("/post/AppReleaseController/updateAppReleaseStateById")
    public void updateAppReleaseStateById(InputObject inputObject, OutputObject outputObject) {
        appReleaseService.updateAppReleaseStateById(inputObject, outputObject);
    }

    @ApiOperation(id = "getLatestVersion", value = "获取项目最新版本信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "projectKey", name = "projectKey", value = "项目唯一标识", required = "required"),
        @ApiImplicitParam(id = "platform", name = "platform", value = "平台类型", required = "required"),
        @ApiImplicitParam(id = "storeKey", name = "storeKey", value = "商店唯一标识", required = "required")})
    @RequestMapping("/post/AppReleaseController/getLatestVersion")
    public void getLatestVersion(InputObject inputObject, OutputObject outputObject) {
        appReleaseService.getLatestVersion(inputObject, outputObject);
    }

}

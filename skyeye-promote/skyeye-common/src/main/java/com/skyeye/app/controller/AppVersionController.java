/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.service.AppVersionService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppVersionController
 * @Description: APP版本管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "APP版本管理", tags = "APP版本管理", modelName = "APP版本发布模块")
public class AppVersionController {

    @Autowired
    private AppVersionService appVersionService;

    @ApiOperation(id = "queryAppVersionList", value = "查询项目版本列表", method = "GET", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AppVersionController/queryAppVersionList")
    public void queryAppVersionList(InputObject inputObject, OutputObject outputObject) {
        appVersionService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAppVersion", value = "新增/编辑APP版本", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AppVersion.class)
    @RequestMapping("/post/AppVersionController/writeAppVersion")
    public void writeAppVersion(InputObject inputObject, OutputObject outputObject) {
        appVersionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAppVersionById", value = "根据ID查询APP版本详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppVersionController/queryAppVersionById")
    public void queryAppVersionById(InputObject inputObject, OutputObject outputObject) {
        appVersionService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAppVersionById", value = "删除APP版本", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppVersionController/deleteAppVersionById")
    public void deleteAppVersionById(InputObject inputObject, OutputObject outputObject) {
        appVersionService.deleteById(inputObject, outputObject);
    }

}

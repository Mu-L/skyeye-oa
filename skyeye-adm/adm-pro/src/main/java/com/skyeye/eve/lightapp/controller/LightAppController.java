/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.lightapp.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.lightapp.entity.LightApp;
import com.skyeye.eve.lightapp.service.LightAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LightAppController
 * @Description: 轻应用管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/4 23:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "轻应用管理", tags = "轻应用管理", modelName = "轻应用管理")
public class LightAppController {

    @Autowired
    private LightAppService lightAppService;

    @ApiOperation(id = "queryLightAppList", value = "获取轻应用列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LightAppController/queryLightAppList")
    public void queryLightAppList(InputObject inputObject, OutputObject outputObject) {
        lightAppService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeLightApp", value = "新增/编辑轻应用", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = LightApp.class)
    @RequestMapping("/post/LightAppController/writeLightApp")
    public void writeLightApp(InputObject inputObject, OutputObject outputObject) {
        lightAppService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLightAppById", value = "删除轻应用", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LightAppController/deleteLightAppById")
    public void deleteLightAppById(InputObject inputObject, OutputObject outputObject) {
        lightAppService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLightAppUpList", value = "获取启用的轻应用列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "typeId", name = "typeId", value = "类型id")})
    @RequestMapping("/post/LightAppController/queryLightAppUpList")
    public void queryLightAppUpList(InputObject inputObject, OutputObject outputObject) {
        lightAppService.queryLightAppUpList(inputObject, outputObject);
    }

    @ApiOperation(id = "insertLightAppToWin", value = "添加轻应用到桌面", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LightAppController/insertLightAppToWin")
    public void insertLightAppToWin(InputObject inputObject, OutputObject outputObject) {
        lightAppService.insertLightAppToWin(inputObject, outputObject);
    }

}

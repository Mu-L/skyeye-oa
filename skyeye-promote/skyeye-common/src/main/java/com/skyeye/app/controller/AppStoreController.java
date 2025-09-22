/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.service.AppStoreService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppStoreController
 * @Description: 应用商店控制器
 * @author: skyeye云系列--卫志强
 * @date: 2025/9/20 14:23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "应用商店", tags = "应用商店", modelName = "APP版本发布模块")
public class AppStoreController {

    @Autowired
    private AppStoreService appStoreService;

    @ApiOperation(id = "queryAppStoreList", value = "查询应用商店列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AppStoreController/queryAppStoreList")
    public void queryAppStoreList(InputObject inputObject, OutputObject outputObject) {
        appStoreService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAppStore", value = "新增/编辑应用商店", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AppStore.class)
    @RequestMapping("/post/AppStoreController/writeAppStore")
    public void writeAppStore(InputObject inputObject, OutputObject outputObject) {
        appStoreService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAppStoreById", value = "根据ID查询应用商店详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppStoreController/queryAppStoreById")
    public void queryAppStoreById(InputObject inputObject, OutputObject outputObject) {
        appStoreService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAppStoreById", value = "删除应用商店", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppStoreController/deleteAppStoreById")
    public void deleteAppStoreById(InputObject inputObject, OutputObject outputObject) {
        appStoreService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEnableAppStoreList", value = "查询所有启用应用商店列表", method = "GET", allUse = "2")
    @RequestMapping("/post/AppStoreController/queryAllEnableAppStoreList")
    public void queryAllEnableAppStoreList(InputObject inputObject, OutputObject outputObject) {
        appStoreService.queryAllEnableAppStoreList(inputObject, outputObject);
    }

}

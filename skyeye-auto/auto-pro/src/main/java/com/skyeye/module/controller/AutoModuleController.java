/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.module.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.module.entity.AutoModule;
import com.skyeye.module.service.AutoModuleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoModuleController
 * @Description: 项目模块管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/19 8:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "项目模块管理", tags = "项目模块管理", modelName = "项目模块管理")
public class AutoModuleController {

    @Autowired
    private AutoModuleService autoModuleService;

    @ApiOperation(id = "queryAutoModuleList", value = "获取模块信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoModuleController/queryAutoModuleList")
    public void queryAutoModuleList(InputObject inputObject, OutputObject outputObject) {
        autoModuleService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAutoModuleForTree", value = "一次性获取所有的模块为树结构", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id", required = "required"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "所属第三方业务数据的key", required = "required")})
    @RequestMapping("/post/AutoModuleController/queryAutoModuleForTree")
    public void queryAutoModuleForTree(InputObject inputObject, OutputObject outputObject) {
        autoModuleService.queryAutoModuleForTree(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoModule", value = "新增/编辑项目模块", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = AutoModule.class)
    @RequestMapping("/post/AutoModuleController/writeAutoModule")
    public void writeAutoModule(InputObject inputObject, OutputObject outputObject) {
        autoModuleService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoModuleById", value = "删除项目模块信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoModuleController/deleteAutoModuleById")
    public void deleteAutoModuleById(InputObject inputObject, OutputObject outputObject) {
        autoModuleService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFirstAutoModuleList", value = "获取模块一级列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id"),
        @ApiImplicitParam(id = "objectKey", name = "objectKey", value = "所属第三方业务数据的key")})
    @RequestMapping("/post/AutoModuleController/queryFirstAutoModuleList")
    public void queryFirstAutoModuleList(InputObject inputObject, OutputObject outputObject) {
        autoModuleService.queryFirstAutoModuleList(inputObject, outputObject);
    }

}

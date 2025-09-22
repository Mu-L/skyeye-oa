/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.app.entity.AppProject;
import com.skyeye.app.service.AppProjectService;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppProjectController
 * @Description: APP项目管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "APP项目管理", tags = "APP项目管理", modelName = "APP版本发布模块")
public class AppProjectController {

    @Autowired
    private AppProjectService appProjectService;

    @ApiOperation(id = "queryAppProjectList", value = "查询APP项目列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/AppProjectController/queryAppProjectList")
    public void queryAppProjectList(InputObject inputObject, OutputObject outputObject) {
        appProjectService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAppProject", value = "新增/编辑APP项目", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AppProject.class)
    @RequestMapping("/post/AppProjectController/writeAppProject")
    public void writeAppProject(InputObject inputObject, OutputObject outputObject) {
        appProjectService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAppProjectById", value = "根据ID查询APP项目详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppProjectController/queryAppProjectById")
    public void queryAppProjectById(InputObject inputObject, OutputObject outputObject) {
        appProjectService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAppProjectById", value = "删除APP项目", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppProjectController/deleteAppProjectById")
    public void deleteAppProjectById(InputObject inputObject, OutputObject outputObject) {
        appProjectService.deleteById(inputObject, outputObject);
    }

}

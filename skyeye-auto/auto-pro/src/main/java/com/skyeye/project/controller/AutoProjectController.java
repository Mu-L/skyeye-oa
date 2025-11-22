/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.project.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.project.entity.AutoProject;
import com.skyeye.project.service.AutoProjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AutoProjectController
 * @Description: 项目管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/20 19:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "项目管理", tags = "项目管理", modelName = "项目管理")
public class AutoProjectController {

    @Autowired
    private AutoProjectService autoProjectService;

    @ApiOperation(id = "queryAutoProjectList", value = "获取项目管理列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AutoProjectController/queryAutoProjectList")
    public void queryAutoProjectList(InputObject inputObject, OutputObject outputObject) {
        autoProjectService.queryAutoProjectList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAutoProject", value = "新增/编辑项目信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AutoProject.class)
    @RequestMapping("/post/AutoProjectController/writeAutoProject")
    public void writeAutoProject(InputObject inputObject, OutputObject outputObject) {
        autoProjectService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteAutoProjectById", value = "根据id删除项目信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AutoProjectController/deleteAutoProjectById")
    public void deleteAutoProjectById(InputObject inputObject, OutputObject outputObject) {
        autoProjectService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllAutoProjectList", value = "获取我负责的/我创建的所有项目列表", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "type", name = "type", value = "类型", required = "required")})
    @RequestMapping("/post/AutoProjectController/queryAllAutoProjectList")
    public void queryAllAutoProjectList(InputObject inputObject, OutputObject outputObject) {
        autoProjectService.queryAllAutoProjectList(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.config.CodeGeneratorConfig;
import com.skyeye.eve.service.CodeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CodeGeneratorController
 * @Description: 代码生成器控制器
 * @author: skyeye云系列--卫志强
 * @date: 2024/12/19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 */
@RestController
@Api(value = "代码生成器", tags = "代码生成器", modelName = "代码生成器")
public class CodeGeneratorController {

    @Autowired
    private CodeGeneratorService codeGeneratorService;

    @ApiOperation(id = "codeGeneratorGetDatabaseTables", value = "获取数据库表列表", method = "POST", allUse = "2")
    @RequestMapping("/post/CodeGeneratorController/getDatabaseTables")
    public void getDatabaseTables(InputObject inputObject, OutputObject outputObject) {
        codeGeneratorService.getDatabaseTables(inputObject, outputObject);
    }

    @ApiOperation(id = "codeGeneratorGetTableColumns", value = "获取表字段信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tableName", name = "tableName", value = "表名", required = "required")})
    @RequestMapping("/post/CodeGeneratorController/getTableColumns")
    public void getTableColumns(InputObject inputObject, OutputObject outputObject) {
        codeGeneratorService.getTableColumns(inputObject, outputObject);
    }

    @ApiOperation(id = "codeGeneratorPreviewCode", value = "预览生成的代码", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CodeGeneratorConfig.class)
    @RequestMapping("/post/CodeGeneratorController/previewCode")
    public void previewCode(InputObject inputObject, OutputObject outputObject) {
        codeGeneratorService.previewCode(inputObject, outputObject);
    }

    @ApiOperation(id = "codeGeneratorDownloadCode", value = "下载代码文件", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CodeGeneratorConfig.class)
    @RequestMapping("/post/CodeGeneratorController/downloadCode")
    public void downloadCode(InputObject inputObject, OutputObject outputObject) {
        codeGeneratorService.downloadCode(inputObject, outputObject);
    }

    @ApiOperation(id = "codeGeneratorGetAvailableTemplates", value = "获取可用的模板列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "templateGroup", name = "templateGroup", value = "模板组", required = "required")})
    @RequestMapping("/post/CodeGeneratorController/getAvailableTemplates")
    public void getAvailableTemplates(InputObject inputObject, OutputObject outputObject) {
        codeGeneratorService.getAvailableTemplates(inputObject, outputObject);
    }
}

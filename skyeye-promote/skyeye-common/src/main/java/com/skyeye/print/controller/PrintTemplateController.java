/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.print.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.print.entity.PrintTemplate;
import com.skyeye.print.service.PrintTemplateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PrintTemplateController
 * @Description: 打印模板控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/15 8:34
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "打印模板", tags = "打印模板", modelName = "打印模板")
public class PrintTemplateController {

    @Autowired
    private PrintTemplateService printTemplateService;

    @ApiOperation(id = "writePrintTemplate", value = "新增/编辑打印模版", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = PrintTemplate.class)
    @RequestMapping("/post/PrintTemplateController/writePrintTemplate")
    public void writePrintTemplate(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPrintTemplateList", value = "分页查询打印模版", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PrintTemplateController/queryPrintTemplateList")
    public void queryPrintTemplateList(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePrintTemplateById", value = "删除打印模版", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PrintTemplateController/deletePrintTemplateById")
    public void deletePrintTemplateByIds(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPrintTemplateById", value = "根据id获取打印模版", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PrintTemplateController/queryPrintTemplateById")
    public void queryPrintTemplateById(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPrintTemplateListByPageId", value = "根据布局id获取打印模版管理信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "pageId", name = "pageId", value = "布局id", required = "required")})
    @RequestMapping("/post/PrintTemplateController/queryPrintTemplateListByPageId")
    public void queryPrintTemplateListByPageId(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.queryPrintTemplateListByPageId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPreviewPrintTemplateById", value = "生成打印预览", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PrintTemplateController/queryPreviewPrintTemplateById")
    public void queryPreviewPrintTemplateById(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.queryPreviewPrintTemplateById(inputObject, outputObject);
    }

    @ApiOperation(id = "generatePdfPrintTemplateById", value = "生成PDF", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PrintTemplateController/generatePdfPrintTemplateById")
    public void generatePdfPrintTemplateById(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.generatePdfPrintTemplateById(inputObject, outputObject);
    }

    @ApiOperation(id = "copyPrintTemplateById", value = "复制模板", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PrintTemplateController/copyPrintTemplateById")
    public void copyPrintTemplateById(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.copyPrintTemplateById(inputObject, outputObject);
    }

    @ApiOperation(id = "editConfigContentById", value = "修改模板配置内容(JSON)", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "configContent", name = "configContent", value = "configContent", required = "required,json")})
    @RequestMapping("/post/PrintTemplateController/editConfigContentById")
    public void editConfigContentById(InputObject inputObject, OutputObject outputObject) {
        printTemplateService.editConfigContentById(inputObject, outputObject);
    }

}

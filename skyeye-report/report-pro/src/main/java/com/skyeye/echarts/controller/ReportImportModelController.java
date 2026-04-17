/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.echarts.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.echarts.entity.ImportModel;
import com.skyeye.echarts.entity.ReportModel;
import com.skyeye.echarts.service.ReportImportModelService;
import com.skyeye.echarts.service.ReportModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReportImportModelController
 * @Description: Echarts模型管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@RestController
@Api(value = "Echarts模型管理", tags = "Echarts模型管理", modelName = "Echarts模型管理")
public class ReportImportModelController {

    @Autowired
    private ReportImportModelService reportImportModelService;

    @Autowired
    private ReportModelService reportModelService;

    @ApiOperation(id = "queryReportImportModelList", value = "获取Echarts模型信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReportImportModelController/queryReportImportModelList")
    public void queryReportImportModelList(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeReportImportModel", value = "新增/编辑Echarts模型", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ImportModel.class)
    @RequestMapping("/post/ReportImportModelController/writeReportImportModel")
    public void writeReportImportModel(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "writeReportModel", value = "新增/编辑Echarts模型版本", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ReportModel.class)
    @RequestMapping("/post/ReportImportModelController/writeReportModel")
    public void writeReportModel(InputObject inputObject, OutputObject outputObject) {
        reportModelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "delReportImportModelById", value = "根据id删除Echarts模型信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportImportModelController/delReportImportModelById")
    public void delReportImportModelById(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllMaxVersionReportModel", value = "获取所有版本最大的echarts模型信息", method = "GET", allUse = "2")
    @RequestMapping({"/post/ReportImportModelController/queryAllMaxVersionReportModel"})
    public void queryAllMaxVersionReportModel(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.queryAllMaxVersionReportModel(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReportModelVersionList", value = "按模型ID获取版本列表", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "importModelId", name = "importModelId", value = "模型ID", required = "required")})
    @RequestMapping({"/post/ReportImportModelController/queryReportModelVersionList"})
    public void queryReportModelVersionList(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.queryReportModelVersionList(inputObject, outputObject);
    }

    @ApiOperation(id = "enableReportModelVersion", value = "设置模型启用版本", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "reportModelId", name = "reportModelId", value = "模型版本id", required = "required")})
    @RequestMapping({"/post/ReportImportModelController/enableReportModelVersion"})
    public void enableReportModelVersion(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.enableReportModelVersion(inputObject, outputObject);
    }

    @ApiOperation(id = "disableReportModelVersion", value = "禁用模型版本", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "reportModelId", name = "reportModelId", value = "模型版本id", required = "required")})
    @RequestMapping({"/post/ReportImportModelController/disableReportModelVersion"})
    public void disableReportModelVersion(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.disableReportModelVersion(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReportModelVersionById", value = "根据id查询模型版本", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "reportModelId", name = "reportModelId", value = "模型版本id", required = "required")})
    @RequestMapping({"/post/ReportImportModelController/queryReportModelVersionById"})
    public void queryReportModelVersionById(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.queryReportModelVersionById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteReportModelVersionById", value = "根据id删除模型版本", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "reportModelId", name = "reportModelId", value = "模型版本id", required = "required")})
    @RequestMapping({"/post/ReportImportModelController/deleteReportModelVersionById"})
    public void deleteReportModelVersionById(InputObject inputObject, OutputObject outputObject) {
        reportModelService.deleteById(inputObject, outputObject);
    }

}

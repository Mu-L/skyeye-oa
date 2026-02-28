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
import com.skyeye.echarts.service.ReportImportModelService;
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

    @ApiOperation(id = "delReportImportModelById", value = "根据id删除Echarts模型信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportImportModelController/delReportImportModelById")
    public void delReportImportModelById(InputObject inputObject, OutputObject outputObject) {
        reportImportModelService.deleteById(inputObject, outputObject);
    }

}

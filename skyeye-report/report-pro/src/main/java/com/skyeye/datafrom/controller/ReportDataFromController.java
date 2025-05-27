/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.datafrom.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.datafrom.entity.ReportDataFrom;
import com.skyeye.datafrom.service.ReportDataFromService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReportDataFromController
 * @Description: 数据来源控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/3 23:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@RestController
@Api(value = "数据来源", tags = "数据来源", modelName = "数据来源")
public class ReportDataFromController {

    @Autowired
    private ReportDataFromService reportDataFromService;

    @ApiOperation(id = "queryReportDataFromList", value = "获取数据来源列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReportDataFromController/queryReportDataFromList")
    public void queryReportDataFromList(InputObject inputObject, OutputObject outputObject) {
        reportDataFromService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "saveReportDataFrom", value = "新增/编辑数据来源", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ReportDataFrom.class)
    @RequestMapping("/post/ReportDataFromController/saveReportDataFrom")
    public void saveReportDataFrom(InputObject inputObject, OutputObject outputObject) {
        reportDataFromService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "delReportDataFromById", value = "根据id删除数据来源信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportDataFromController/delReportDataFromById")
    public void delReportDataFromById(InputObject inputObject, OutputObject outputObject) {
        reportDataFromService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReportDataFromById", value = "根据id查询数据来源", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportDataFromController/queryReportDataFromById")
    public void queryReportDataFromById(InputObject inputObject, OutputObject outputObject) {
        reportDataFromService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReportDataFromMationById", value = "根据数据来源信息获取要取的数据", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "needGetDataStr", name = "needGetDataStr", value = "需要获取的数据", required = "required,json"),
        @ApiImplicitParam(id = "inputParams", name = "inputParams", value = "入参参数", required = "json")})
    @RequestMapping("/post/ReportDataFromController/queryReportDataFromMationById")
    public void queryReportDataFromMationById(InputObject inputObject, OutputObject outputObject) {
        reportDataFromService.queryReportDataFromMationById(inputObject, outputObject);
    }

}

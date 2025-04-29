/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye-report
 ******************************************************************************/

package com.skyeye.page.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.page.entity.ReportPage;
import com.skyeye.page.service.ReportPageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReportPageController
 * @Description: 报表页面信息控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/26 17:40
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@RestController
@Api(value = "报表页面", tags = "报表页面", modelName = "报表页面")
public class ReportPageController {

    @Autowired
    private ReportPageService reportPageService;

    @ApiOperation(id = "queryReportPageList", value = "获取报表页面信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReportPageController/queryReportPageList")
    public void queryReportPageList(InputObject inputObject, OutputObject outputObject) {
        reportPageService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeReportPage", value = "新增/编辑报表页面", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ReportPage.class)
    @RequestMapping("/post/ReportPageController/writeReportPage")
    public void writeReportPage(InputObject inputObject, OutputObject outputObject) {
        reportPageService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteReportPageById", value = "删除报表页面信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportPageController/deleteReportPageById")
    public void deleteReportPageById(InputObject inputObject, OutputObject outputObject) {
        reportPageService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReportPageById", value = "根据id查询报表页面信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportPageController/queryReportPageById")
    public void queryReportPageById(InputObject inputObject, OutputObject outputObject) {
        reportPageService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "editReportPageContentById", value = "编辑报表页面包含的模型信息", method = "POST", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "content", name = "content", value = "报表页面模型对象", required = "json")})
    @RequestMapping("/post/ReportPageController/editReportPageContentById")
    public void editReportPageContentById(InputObject inputObject, OutputObject outputObject) {
        reportPageService.editReportPageContentById(inputObject, outputObject);
    }

}

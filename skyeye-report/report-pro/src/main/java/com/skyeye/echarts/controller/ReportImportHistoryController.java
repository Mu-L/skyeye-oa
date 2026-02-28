/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.echarts.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.echarts.service.ReportImportHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReportImportHistoryController
 * @Description: Echarts导入历史控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/6/20 14:04
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "Echarts导入历史", tags = "Echarts导入历史", modelName = "Echarts导入历史")
public class ReportImportHistoryController {

    @Autowired
    private ReportImportHistoryService reportImportHistoryService;

    @ApiOperation(id = "queryReportImportHistoryList", value = "获取模型上传导入历史列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReportImportHistoryController/queryReportImportHistoryList")
    public void queryReportImportHistoryList(InputObject inputObject, OutputObject outputObject) {
        reportImportHistoryService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "importReportImportModel", value = "模型上传导入", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "modelId", name = "modelId", value = "主键id", required = "required")})
    @RequestMapping("/post/ReportImportHistoryController/importReportImportModel")
    public void importReportImportModel(InputObject inputObject, OutputObject outputObject) {
        reportImportHistoryService.importReportImportModel(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllMaxVersionReportModel", value = "获取所有版本最大的echarts模型信息", method = "GET", allUse = "2")
    @RequestMapping("/post/ReportImportHistoryController/queryAllMaxVersionReportModel")
    public void queryAllMaxVersionReportModel(InputObject inputObject, OutputObject outputObject) {
        reportImportHistoryService.queryAllMaxVersionReportModel(inputObject, outputObject);
    }

}

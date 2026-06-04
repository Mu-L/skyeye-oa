/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.browse.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.browse.service.DocumentBrowseHistoryStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文档浏览历史统计分析控制层
 */
@RestController
@Api(value = "文档浏览历史统计分析", tags = "文档浏览历史统计分析", modelName = "文档管理")
public class DocumentBrowseHistoryStatisticsController {

    @Autowired
    private DocumentBrowseHistoryStatisticsService documentBrowseHistoryStatisticsService;

    @ApiOperation(id = "queryBrowseHistoryTotal", value = "浏览历史记录总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseHistoryTotal")
    public void queryBrowseHistoryTotal(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseHistoryTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseViewCountTotal", value = "累计浏览次数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseViewCountTotal")
    public void queryBrowseViewCountTotal(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseViewCountTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseMemberTotal", value = "浏览会员数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseMemberTotal")
    public void queryBrowseMemberTotal(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseMemberTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseDocumentTotal", value = "被浏览文档数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseDocumentTotal")
    public void queryBrowseDocumentTotal(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseDocumentTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseRevisitTotal", value = "复访记录数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseRevisitTotal")
    public void queryBrowseRevisitTotal(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseRevisitTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseHistoryStatsByCreateTime", value = "浏览记录按创建时间趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseHistoryStatsByCreateTime")
    public void queryBrowseHistoryStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseHistoryStatsByCreateTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseHistoryStatsByLastViewTime", value = "浏览活跃按最近浏览时间趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseHistoryStatsByLastViewTime")
    public void queryBrowseHistoryStatsByLastViewTime(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseHistoryStatsByLastViewTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseStatsByDocument", value = "浏览按文档统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseStatsByDocument")
    public void queryBrowseStatsByDocument(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseStatsByDocument(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseStatsByCity", value = "浏览按城市统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseStatsByCity")
    public void queryBrowseStatsByCity(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseStatsByCity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryBrowseStatsByMember", value = "浏览按会员统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/DocumentBrowseHistoryStatisticsController/queryBrowseStatsByMember")
    public void queryBrowseStatsByMember(InputObject inputObject, OutputObject outputObject) {
        documentBrowseHistoryStatisticsService.queryBrowseStatsByMember(inputObject, outputObject);
    }

}

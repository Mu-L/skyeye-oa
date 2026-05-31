/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.gitcode.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.doc.gitcode.service.GitCodeIssueStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 问答社区统计分析控制层
 */
@RestController
@Api(value = "问答社区统计分析", tags = "问答社区统计分析", modelName = "GitCode Issue管理")
public class GitCodeIssueStatisticsController {

    @Autowired
    private GitCodeIssueStatisticsService gitCodeIssueStatisticsService;

    @ApiOperation(id = "queryIssueTotal", value = "问答总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueTotal")
    public void queryIssueTotal(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueCommentTotal", value = "回答总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueCommentTotal")
    public void queryIssueCommentTotal(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueCommentTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueBugTotal", value = "Bug标记总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueBugTotal")
    public void queryIssueBugTotal(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueBugTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueRequirementTotal", value = "需求标记总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueRequirementTotal")
    public void queryIssueRequirementTotal(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueRequirementTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueBugCompletedTotal", value = "Bug已完成总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueBugCompletedTotal")
    public void queryIssueBugCompletedTotal(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueBugCompletedTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueRequirementCompletedTotal", value = "需求已完成总数统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueRequirementCompletedTotal")
    public void queryIssueRequirementCompletedTotal(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueRequirementCompletedTotal(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueStatsByCreateTime", value = "问答按创建时间趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueStatsByCreateTime")
    public void queryIssueStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueStatsByCreateTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueCommentStatsByCreateTime", value = "回答按创建时间趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueCommentStatsByCreateTime")
    public void queryIssueCommentStatsByCreateTime(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueCommentStatsByCreateTime(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueStatsByVersion", value = "问答按版本统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueStatsByVersion")
    public void queryIssueStatsByVersion(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueStatsByVersion(inputObject, outputObject);
    }

    @ApiOperation(id = "queryIssueStatsByRecordType", value = "问答按标记类型统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/GitCodeIssueStatisticsController/queryIssueStatsByRecordType")
    public void queryIssueStatsByRecordType(InputObject inputObject, OutputObject outputObject) {
        gitCodeIssueStatisticsService.queryIssueStatsByRecordType(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumReport;
import com.skyeye.eve.forum.service.ForumReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "论坛举报管理", tags = "论坛举报管理", modelName = "论坛举报管理")
public class ForumReportController {

    @Autowired
    private ForumReportService forumReportService;

    /**
     * 添加举报信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertForumReportMation", value = "添加举报信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ForumReport.class)
    @RequestMapping("/post/ForumReportController/insertForumReportMation")
    public void insertForumReportMation(InputObject inputObject, OutputObject outputObject) {
        forumReportService.createEntity(inputObject, outputObject);
    }

    /**
     * 获取论坛举报列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryReportNoCheckList", value = "根据审核状态获取论坛举报列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ForumReportController/queryReportNoCheckList")
    public void queryReportNoCheckList(InputObject inputObject, OutputObject outputObject) {
        forumReportService.queryPageList(inputObject, outputObject);
    }

    /**
     * 举报信息审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkForumReport", value = "举报信息审核", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "审核状态", required = "required"),
        @ApiImplicitParam(id = "reason", name = "reason", value = "审核不通过的原因")})
    @RequestMapping("/post/ForumReportController/checkForumReport")
    public void checkForumReport(InputObject inputObject, OutputObject outputObject) {
        forumReportService.checkForumReport(inputObject, outputObject);
    }

//    /**
//     * 获取论坛举报已审核列表
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @RequestMapping("/post/ForumReportController/queryReportCheckedList")
//    public void queryReportCheckedList(InputObject inputObject, OutputObject outputObject) {
//        forumReportService.queryReportCheckedList(inputObject, outputObject);
//    }

    /**
     * 举报详情
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/ForumReportController/queryForumReportMationToDetails")
    public void queryForumReportMationToDetails(InputObject inputObject, OutputObject outputObject) {
        forumReportService.queryForumReportMationToDetails(inputObject, outputObject);
    }

    @ApiOperation(id = "selectReportById", value = "举报详情", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ForumReportController/selectReportById")
    public void selectReportById(InputObject inputObject, OutputObject outputObject) {
        forumReportService.selectById(inputObject, outputObject);
    }
}

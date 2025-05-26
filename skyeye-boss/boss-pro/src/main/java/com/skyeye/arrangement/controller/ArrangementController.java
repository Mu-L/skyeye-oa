/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.arrangement.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.arrangement.entity.Arrangement;
import com.skyeye.arrangement.service.ArrangementService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @ClassName: ArrangementController
 * @Description: 面试安排控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/14 11:45
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "面试安排", tags = "面试安排", modelName = "面试安排")
public class ArrangementController {

    @Autowired
    private ArrangementService arrangementService;

    @ApiOperation(id = "queryMyEntryBossInterviewArrangementList", value = "获取我录入的面试安排信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ArrangementController/queryMyEntryBossInterviewArrangementList")
    public void queryMyEntryBossInterviewArrangementList(InputObject inputObject, OutputObject outputObject) {
        arrangementService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeArrangement", value = "新增/编辑面试安排", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Arrangement.class)
    @RequestMapping("/post/ArrangementController/writeArrangement")
    public void writeArrangement(InputObject inputObject, OutputObject outputObject) {
        arrangementService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryArrangementById", value = "根据id查询面试安排信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ArrangementController/queryArrangementById")
    public void queryArrangementById(InputObject inputObject, OutputObject outputObject) {
        arrangementService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "nullifyArrangement", value = "作废面试安排信息", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ArrangementController/nullifyArrangement")
    public void nullifyArrangement(InputObject inputObject, OutputObject outputObject) {
        arrangementService.nullifyArrangement(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyEntryBossPersonRequireAboutArrangementList", value = "获取我录入的人员需求关联的面试者信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ArrangementController/queryMyEntryBossPersonRequireAboutArrangementList")
    public void queryMyEntryBossPersonRequireAboutArrangementList(InputObject inputObject, OutputObject outputObject) {
        arrangementService.queryMyEntryBossPersonRequireAboutArrangementList(inputObject, outputObject);
    }

    @ApiOperation(id = "setBossInterviewer", value = "部门经理面试安排信息设置面试官", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "interviewer", name = "interviewer", value = "面试官id", required = "required")})
    @RequestMapping("/post/ArrangementController/setBossInterviewer")
    public void setBossInterviewer(InputObject inputObject, OutputObject outputObject) {
        arrangementService.setBossInterviewer(inputObject, outputObject);
    }

    @ApiOperation(id = "queryArrangementInterviewerIsMyList", value = "获取面试官为当前登录用户的面试者信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ArrangementController/queryArrangementInterviewerIsMyList")
    public void queryArrangementInterviewerIsMyList(InputObject inputObject, OutputObject outputObject) {
        arrangementService.queryArrangementInterviewerIsMyList(inputObject, outputObject);
    }

    @ApiOperation(id = "setBossInterviewResult", value = "设置面试结果", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "4.面试通过  5.面试不通过", required = "required,num"),
        @ApiImplicitParam(id = "evaluation", name = "evaluation", value = "面试评价", required = "required")})
    @RequestMapping("/post/ArrangementController/setBossInterviewResult")
    public void setBossInterviewResult(InputObject inputObject, OutputObject outputObject) {
        arrangementService.setBossInterviewResult(inputObject, outputObject);
    }

    @ApiOperation(id = "setInductionResult", value = "设置入职结果", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "state", name = "state", value = "6.同意入职  7.拒绝入职", required = "required,num"),
        @ApiImplicitParam(id = "reason", name = "reason", value = "拒绝入职的原因，当拒绝入职时必填"),
        @ApiImplicitParam(id = "workTime", name = "workTime", value = "参加工作时间，格式为yyyy-MM-dd。同意入职后必填"),
        @ApiImplicitParam(id = "entryTime", name = "entryTime", value = "入职时间，格式为yyyy-MM-dd。同意入职后必填"),
        @ApiImplicitParam(id = "userIdCard", name = "userIdCard", value = "身份证。同意入职后必填"),
        @ApiImplicitParam(id = "email", name = "email", value = "邮箱。同意入职后必填"),
        @ApiImplicitParam(id = "inductionState", name = "inductionState", value = "员工入职状态"),
        @ApiImplicitParam(id = "trialTime", name = "trialTime", value = "如果有试用期，则为试用期到期时间。当state=4时，该字段必填")})
    @RequestMapping("/post/ArrangementController/setInductionResult")
    public void setInductionResult(InputObject inputObject, OutputObject outputObject) {
        arrangementService.setInductionResult(inputObject, outputObject);
    }

}

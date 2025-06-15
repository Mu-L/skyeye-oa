/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.milestone.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.milestone.entity.Milestone;
import com.skyeye.milestone.service.MilestoneService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MilestoneController
 * @Description: 里程碑管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/14 20:17
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "里程碑管理", tags = "里程碑管理", modelName = "里程碑管理")
public class MilestoneController {

    @Autowired
    private MilestoneService milestoneService;

    /**
     * 获取里程碑列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMilestoneList", value = "获取里程碑列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MilestoneController/queryMilestoneList")
    public void queryMilestoneList(InputObject inputObject, OutputObject outputObject) {
        milestoneService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑里程碑管理
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeMilestone", value = "新增/编辑里程碑管理", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Milestone.class)
    @RequestMapping("/post/MilestoneController/writeMilestone")
    public void writeMilestone(InputObject inputObject, OutputObject outputObject) {
        milestoneService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除里程碑
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteMilestoneById", value = "删除里程碑信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MilestoneController/deleteMilestoneById")
    public void deleteMilestoneById(InputObject inputObject, OutputObject outputObject) {
        milestoneService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销里程碑审批申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeMilestone", value = "撤销里程碑审批申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/MilestoneController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        milestoneService.revoke(inputObject, outputObject);
    }

    /**
     * 里程碑提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitToApprovalMilestone", value = "里程碑提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/MilestoneController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        milestoneService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废里程碑
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "invalidMilestone", value = "作废里程碑", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MilestoneController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        milestoneService.invalid(inputObject, outputObject);
    }

    /**
     * 里程碑开始执行
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "executionMilestone", value = "里程碑开始执行", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MilestoneController/executionMilestone")
    public void executionMilestone(InputObject inputObject, OutputObject outputObject) {
        milestoneService.executionMilestone(inputObject, outputObject);
    }

    /**
     * 里程碑执行完成
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "complateMilestone", value = "里程碑执行完成", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MilestoneController/complateMilestone")
    public void complateMilestone(InputObject inputObject, OutputObject outputObject) {
        milestoneService.complateMilestone(inputObject, outputObject);
    }

    /**
     * 里程碑关闭
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "closeMilestone", value = "里程碑关闭", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MilestoneController/closeMilestone")
    public void closeMilestone(InputObject inputObject, OutputObject outputObject) {
        milestoneService.closeMilestone(inputObject, outputObject);
    }

    /**
     * 根据供应商id获取执行中的里程碑列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllExecutingMilestoneList", value = "根据供应商id获取执行中的里程碑列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id")})
    @RequestMapping("/post/MilestoneController/queryAllExecutingMilestoneList")
    public void queryAllExecutingMilestoneList(InputObject inputObject, OutputObject outputObject) {
        milestoneService.queryAllExecutingMilestoneList(inputObject, outputObject);
    }

    /**
     * 根据供应商id获取所有审批通过之后的里程碑列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllApprovalMilestoneList", value = "根据供应商id获取所有审批通过之后的里程碑列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id", required = "required")})
    @RequestMapping("/post/MilestoneController/queryAllApprovalMilestoneList")
    public void queryAllApprovalMilestoneList(InputObject inputObject, OutputObject outputObject) {
        milestoneService.queryAllApprovalMilestoneList(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.leave.entity.Leave;
import com.skyeye.leave.service.LeaveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LeaveController
 * @Description: 请假申请控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/4/4 13:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "请假申请", tags = "请假申请", modelName = "请假申请")
public class LeaveController {

    @Autowired
    private LeaveService leaveService;

    /**
     * 获取我的请假申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkleave001", value = "获取我的请假申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LeaveController/queryLeaveList")
    public void queryLeaveList(InputObject inputObject, OutputObject outputObject) {
        leaveService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑请假申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeLeave", value = "新增/编辑请假申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Leave.class)
    @RequestMapping("/post/LeaveController/writeLeave")
    public void writeLeave(InputObject inputObject, OutputObject outputObject) {
        leaveService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询请假申请信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryLeaveById", value = "根据id查询请假申请信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LeaveController/queryLeaveById")
    public void queryLeaveById(InputObject inputObject, OutputObject outputObject) {
        leaveService.selectById(inputObject, outputObject);
    }

    /**
     * 请假申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkleave006", value = "请假申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/LeaveController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        leaveService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废请假申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkleave007", value = "作废请假申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LeaveController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        leaveService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销请假申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkleave009", value = "撤销请假申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/LeaveController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        leaveService.revoke(inputObject, outputObject);
    }

    /**
     * 获取基础设置中的请假类型
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "getLeaveTypeList", value = "获取基础设置中的请假类型", method = "GET", allUse = "2")
    @RequestMapping("/post/LeaveController/getLeaveTypeList")
    public void getLeaveTypeList(InputObject inputObject, OutputObject outputObject) {
        leaveService.getLeaveTypeList(inputObject, outputObject);
    }

}

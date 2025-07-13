/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.cancleleave.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.cancleleave.entity.CancelLeave;
import com.skyeye.cancleleave.service.CancelLeaveService;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CancelLeaveController
 * @Description: 销假申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/11 9:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "销假申请", tags = "销假申请", modelName = "销假申请")
public class CancelLeaveController {

    @Autowired
    private CancelLeaveService cancelLeaveService;

    /**
     * 获取我的销假申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkcancelleave001", value = "获取我的销假申请列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CancelLeaveController/queryCancelLeaveList")
    public void queryCancelLeaveList(InputObject inputObject, OutputObject outputObject) {
        cancelLeaveService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑销假申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeCancelLeave", value = "新增/编辑销假申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CancelLeave.class)
    @RequestMapping("/post/CancelLeaveController/writeCancelLeave")
    public void writeCancelLeave(InputObject inputObject, OutputObject outputObject) {
        cancelLeaveService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 销假申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkcancelleave006", value = "销假申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/CancelLeaveController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        cancelLeaveService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废销假申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkcancelleave007", value = "作废销假申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CancelLeaveController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        cancelLeaveService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销销假申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkcancelleave009", value = "撤销出差申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/CancelLeaveController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        cancelLeaveService.revoke(inputObject, outputObject);
    }

}

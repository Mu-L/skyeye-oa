/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.seal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.seal.entity.SealUse;
import com.skyeye.eve.seal.service.SealApplyBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealApplyBorrowController
 * @Description: 印章借用申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 15:56
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "印章借用", tags = "印章借用", modelName = "印章模块")
public class SealApplyBorrowController {

    @Autowired
    private SealApplyBorrowService sealApplyBorrowService;

    /**
     * 获取我发起的印章借用列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealborrow001", value = "获取我发起的印章借用列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealApplyBorrowController/querySealUseList")
    public void querySealUseList(InputObject inputObject, OutputObject outputObject) {
        sealApplyBorrowService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑印章借用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeSealUse", value = "新增/编辑印章借用申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SealUse.class)
    @RequestMapping("/post/SealApplyBorrowController/writeSealUse")
    public void writeSealUse(InputObject inputObject, OutputObject outputObject) {
        sealApplyBorrowService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 印章借用申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealborrow006", value = "印章借用申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/SealApplyBorrowController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        sealApplyBorrowService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废印章借用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealborrow007", value = "作废印章借用申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealApplyBorrowController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        sealApplyBorrowService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销印章借用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealborrow010", value = "撤销印章借用申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/SealApplyBorrowController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        sealApplyBorrowService.revoke(inputObject, outputObject);
    }

}

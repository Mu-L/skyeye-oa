/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.loan.entity.LoanRepay;
import com.skyeye.loan.service.LoanRepayService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LoanRepayController
 * @Description: 还款单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "还款单", tags = "还款单", modelName = "还款单")
public class LoanRepayController {

    @Autowired
    private LoanRepayService loanRepayService;

    /**
     * 查询还款单列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryLoanRepayList", value = "查询还款单列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LoanRepayController/queryLoanRepayList")
    public void queryLoanRepayList(InputObject inputObject, OutputObject outputObject) {
        loanRepayService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑还款单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeLoanRepay", value = "新增/编辑还款单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = LoanRepay.class)
    @RequestMapping("/post/LoanRepayController/writeLoanRepay")
    public void writeLoanRepay(InputObject inputObject, OutputObject outputObject) {
        loanRepayService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除还款单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteLoanRepayById", value = "删除还款单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LoanRepayController/deleteLoanRepayById")
    public void deleteLoanRepayById(InputObject inputObject, OutputObject outputObject) {
        loanRepayService.deleteById(inputObject, outputObject);
    }

    /**
     * 提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitLoanRepayToApproval", value = "还款单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/LoanRepayController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        loanRepayService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeLoanRepay", value = "撤销还款单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/LoanRepayController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        loanRepayService.revoke(inputObject, outputObject);
    }

}

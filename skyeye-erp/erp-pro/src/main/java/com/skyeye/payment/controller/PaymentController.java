/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.payment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.payment.entity.Payment;
import com.skyeye.payment.service.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PaymentController
 * @Description: 供应商付款管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/2 20:38
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "供应商付款管理", tags = "供应商付款管理", modelName = "供应商付款管理")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    /**
     * 获取付款列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPaymentList", value = "获取付款列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PaymentController/queryPaymentList")
    public void queryPaymentCollectionList(InputObject inputObject, OutputObject outputObject) {
        paymentService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑付款信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writePayment", value = "新增/编辑付款信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Payment.class)
    @RequestMapping("/post/PaymentController/writePayment")
    public void writePayment(InputObject inputObject, OutputObject outputObject) {
        paymentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除付款信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deletePaymentById", value = "删除付款信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PaymentController/deletePaymentById")
    public void deletePaymentById(InputObject inputObject, OutputObject outputObject) {
        paymentService.deleteById(inputObject, outputObject);
    }

    /**
     * 付款提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitPaymentToApproval", value = "付款提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "approvalId", name = "approvalId", value = "审批人", required = "required")})
    @RequestMapping("/post/PaymentController/submitPaymentToApproval")
    public void submitPaymentToApproval(InputObject inputObject, OutputObject outputObject) {
        paymentService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废付款信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "invalidPayment", value = "作废付款信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PaymentController/invalidPayment")
    public void invalidPayment(InputObject inputObject, OutputObject outputObject) {
        paymentService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销付款审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokePayment", value = "撤销付款审批", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/PaymentController/revokePayment")
    public void revokePayment(InputObject inputObject, OutputObject outputObject) {
        paymentService.revoke(inputObject, outputObject);
    }

    /**
     * 根据合同id获取付款列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPaymentByContractId", value = "根据合同id获取付款列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "contractId", name = "contractId", value = "合同id")})
    @RequestMapping("/post/PaymentController/queryPaymentByContractId")
    public void queryPaymentByContractId(InputObject inputObject, OutputObject outputObject) {
        paymentService.queryPaymentByContractId(inputObject, outputObject);
    }
}

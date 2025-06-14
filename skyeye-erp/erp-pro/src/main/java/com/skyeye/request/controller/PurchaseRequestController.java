/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.contract.entity.SupplierContract;
import com.skyeye.request.entity.PurchaseRequest;
import com.skyeye.request.service.PurchaseRequestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PurchaseRequestController
 * @Description: 采购申请控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:06
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "采购申请", tags = "采购申请", modelName = "采购申请")
public class PurchaseRequestController {

    @Autowired
    private PurchaseRequestService purchaseRequestService;

    /**
     * 获取采购申请信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPurchaseRequestList", value = "获取采购申请信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PurchaseRequestController/queryPurchaseRequestList")
    public void queryPurchaseRequestList(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑采购申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writePurchaseRequest", value = "新增/编辑采购申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseRequest.class)
    @RequestMapping("/post/PurchaseRequestController/writePurchaseRequest")
    public void writePurchaseRequest(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 采购申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitPurchaseRequestToApproval", value = "采购申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/PurchaseRequestController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除采购申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deletePurchaseRequest", value = "删除采购申请", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseRequestController/deletePurchaseRequest")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销采购申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokePurchaseRequest", value = "撤销采购申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/PurchaseRequestController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.revoke(inputObject, outputObject);
    }

    /**
     * 采购申请询价
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "inquiryPurchaseRequest", value = "采购申请询价", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "purchaseRequestInquiryChildList", name = "purchaseRequestInquiryChildList", value = "采购申请询价明细信息", required = "required,json")})
    @RequestMapping("/post/PurchaseRequestController/inquiryPurchaseRequest")
    public void inquiryPurchaseRequest(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.inquiryPurchaseRequest(inputObject, outputObject);
    }

    /**
     * 采购申请定价
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "fixedPricePurchaseRequest", value = "采购申请定价", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "fixedPriceUserId", name = "fixedPriceUserId", value = "定价人员id", required = "required"),
        @ApiImplicitParam(id = "purchaseRequestFixedChildList", name = "purchaseRequestFixedChildList", value = "采购申请明细定价信息", required = "required,json")})
    @RequestMapping("/post/PurchaseRequestController/fixedPricePurchaseRequest")
    public void fixedPricePurchaseRequest(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.fixedPricePurchaseRequest(inputObject, outputObject);
    }

    /**
     * 采购申请转合同时获取的详情
     *
     * @param inputObject
     * @param outputObject
     */
    @ApiOperation(id = "queryPurchaseRequestTransferContract", value = "采购申请转合同时获取的详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseRequestController/queryPurchaseRequestTransferContract")
    public void queryPurchaseRequestTransferContract(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.queryPurchaseRequestTransferContract(inputObject, outputObject);
    }

    /**
     * 采购申请单转采购合同
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "purchaseRequestToContract", value = "采购申请单转采购合同", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SupplierContract.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseRequestController/purchaseRequestToContract")
    public void purchaseRequestToContract(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestService.purchaseRequestToContract(inputObject, outputObject);
    }

}

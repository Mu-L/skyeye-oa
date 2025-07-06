package com.skyeye.receivepayment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.IncomeOrder;
import com.skyeye.receivepayment.entity.ReceivePayment;
import com.skyeye.receivepayment.service.ReceivePaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReceivePaymentController
 * @Description: 收付款管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "收付款管理", tags = "收付款管理", modelName = "收付款管理")
public class ReceivePaymentController {

    @Autowired
    private ReceivePaymentService receivePaymentService;

    @ApiOperation(id = "writeReceivePayment", value = "新增/编辑收付款", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ReceivePayment.class)
    @RequestMapping("/post/ReceivePaymentController/writeReceivePayment")
    public void writeReceivePayment(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "addReceivePayment", value = "新增收付款", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ReceivePayment.class)
    @RequestMapping("/post/ReceivePaymentController/addReceivePayment")
    public void addReceivePayment(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivePaymentList", value = "根据objectId/objectKey(客户/供应商id)获取应收事项列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReceivePaymentController/queryReceivePaymentList")
    public void queryReceivePaymentList(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteReceivePaymentById", value = "删除收付款信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReceivePaymentController/deleteReceivePaymentById")
    public void deleteReceivePaymentById(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitReceivePaymentToApproval", value = "收付款申请提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ReceivePaymentController/submitReceivePaymentToApproval")
    public void submitReceivePaymentToApproval(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeReceivePayment", value = "撤销收付款", method = "PUT", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ReceivePaymentController/revokeReceivePayment")
    public void revokeReceivePayment(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivePaymentById", value = "根据id获取收付款详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")
    })
    @RequestMapping("/post/ReceivePaymentController/queryReceivePaymentById")
    public void queryReceivePaymentById(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryReceivePaymentByContractId", value = "根据合同id获取应收事项列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "contractId", name = "contractId", value = "合同id")})
    @RequestMapping("/post/ReceivePaymentController/queryReceivePaymentByContractId")
    public void queryReceivePaymentByContractId(InputObject inputObject, OutputObject outputObject) {
        receivePaymentService.queryReceivePaymentByContractId(inputObject, outputObject);
    }
}

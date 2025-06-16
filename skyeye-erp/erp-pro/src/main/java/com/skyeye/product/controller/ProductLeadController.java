package com.skyeye.product.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.contract.entity.SupplierContract;
import com.skyeye.product.entity.ProductLead;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "借出申请", tags = "借出申请", modelName = "借出申请")
public class ProductLeadController {

    @Autowired
    private ProductLeadService productLeadService;

    /**
     * 获取借出申请订单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductLeadList", value = "获取借出申请单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductLeadController/queryProductLeadList")
    public void queryProductLeadList(InputObject inputObject, OutputObject outputObject) {
        productLeadService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑借出出库申请单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProductLead", value = "新增/编辑借出申请单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductLead.class)
    @RequestMapping("/post/ProductLeadController/writeProductLead")
    public void writeProductLead(InputObject inputObject, OutputObject outputObject) {
        productLeadService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 借出出库申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitProductLeadToApproval", value = "借出申请提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductLeadController/submitProductLeadToApproval")
    public void submitProductLeadToApproval(InputObject inputObject, OutputObject outputObject) {
        productLeadService.submitToApproval(inputObject, outputObject);
    }


    /**
     * 删除借出申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteProductLead", value = "删除借出申请", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductLeadController/deleteProductLead")
    public void deleteProductLead(InputObject inputObject, OutputObject outputObject) {
        productLeadService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销借出申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeProductLead", value = "撤销借出申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductLeadController/revokeProductLead")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        productLeadService.revoke(inputObject, outputObject);
    }

    /**
     * 借出申请转借出出库时根据id获取的详情
     *
     * @param inputObject
     * @param outputObject
     */
    @ApiOperation(id = "queryProductLeadWithStock", value = "借出申请转借出出库时获取的详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductLeadController/queryProductLeadWithStock")
    public void queryProductLeadWithStock(InputObject inputObject, OutputObject outputObject) {
        productLeadService.selectById(inputObject, outputObject);
    }

    /**
     * 借出申请单转借出出库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "productLeadToContractOutStock", value = "借出申请单转借出出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductLeadOutStock.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductLeadController/productLeadToContractOutStock")
    public void productLeadToContractOutStock(InputObject inputObject, OutputObject outputObject) {
        productLeadService.productLeadToContractOutStock(inputObject, outputObject);
    }
}

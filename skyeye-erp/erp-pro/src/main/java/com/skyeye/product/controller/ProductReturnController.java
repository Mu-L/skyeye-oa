/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.product.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductReturn;
import com.skyeye.product.entity.ProductReturnInStock;
import com.skyeye.product.service.ProductReturnService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "归还申请", tags = "归还申请", modelName = "归还申请")
public class ProductReturnController {

    @Autowired
    private ProductReturnService productReturnService;

    @ApiOperation(id = "queryProductReturnList", value = "获取归还申请单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductReturnController/queryProductReturnList")
    public void queryProductLeadList(InputObject inputObject, OutputObject outputObject) {
        productReturnService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeProductReturn", value = "新增/编辑归还申请单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductReturn.class)
    @RequestMapping("/post/ProductReturnController/writeProductReturn")
    public void writeProductReturn(InputObject inputObject, OutputObject outputObject) {
        productReturnService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "submitProductReturnToApproval", value = "归还申请提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductReturnController/submitProductReturnToApproval")
    public void submitProductReturnToApproval(InputObject inputObject, OutputObject outputObject) {
        productReturnService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteProductReturn", value = "删除归还申请", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnController/deleteProductReturn")
    public void deleteProductReturn(InputObject inputObject, OutputObject outputObject) {
        productReturnService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeProductReturn", value = "撤销归还申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductReturnController/revokeProductReturn")
    public void revokeProductReturn(InputObject inputObject, OutputObject outputObject) {
        productReturnService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryProductReturnWithStock", value = "归还申请转归还入库单时根据id获取的详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnController/queryProductReturnWithStock")
    public void queryProductReturnWithStock(InputObject inputObject, OutputObject outputObject) {
        productReturnService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "productReturnToContractInStock", value = "归还申请单转归还入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductReturnInStock.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnController/productReturnToContractInStock")
    public void productReturnToContractInStock(InputObject inputObject, OutputObject outputObject) {
        productReturnService.productLeadToContractOutStock(inputObject, outputObject);
    }
}

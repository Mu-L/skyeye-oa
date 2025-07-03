package com.skyeye.product.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductLead;
import com.skyeye.product.entity.ProductLeadOutStock;
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

    /**
     * 获取归还申请单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductReturnList", value = "获取归还申请单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductReturnController/queryProductReturnList")
    public void queryProductLeadList(InputObject inputObject, OutputObject outputObject) {
        productReturnService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑归还入库申请单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProductReturn", value = "新增/编辑归还入库申请单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductReturn.class)
    @RequestMapping("/post/ProductReturnController/writeProductReturn")
    public void writeProductReturn(InputObject inputObject, OutputObject outputObject) {
        productReturnService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 归还入库申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitProductReturnToApproval", value = "归还入库申请提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductReturnController/submitProductReturnToApproval")
    public void submitProductReturnToApproval(InputObject inputObject, OutputObject outputObject) {
        productReturnService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除归还入库申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteProductReturn", value = "删除归还入库申请", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnController/deleteProductReturn")
    public void deleteProductReturn(InputObject inputObject, OutputObject outputObject) {
        productReturnService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销归还入库申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeProductReturn", value = "撤销归还入库申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductReturnController/revokeProductReturn")
    public void revokeProductReturn(InputObject inputObject, OutputObject outputObject) {
        productReturnService.revoke(inputObject, outputObject);
    }

    /**
     * 归还入库申请转归还入库时根据id获取的详情
     *
     * @param inputObject
     * @param outputObject
     */
    @ApiOperation(id = "queryProductReturnWithStock", value = "归还入库申请转归还入库时根据id获取的详情", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnController/queryProductReturnWithStock")
    public void queryProductReturnWithStock(InputObject inputObject, OutputObject outputObject) {
        productReturnService.selectById(inputObject, outputObject);
    }

    /**
     * 归还入库申请单转归还入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "productReturnToContractInStock", value = "归还入库申请单转归还入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductReturnInStock.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductReturnController/productReturnToContractInStock")
    public void productReturnToContractInStock(InputObject inputObject, OutputObject outputObject) {
        productReturnService.productLeadToContractOutStock(inputObject, outputObject);
    }
}

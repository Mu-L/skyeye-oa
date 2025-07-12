package com.skyeye.product.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.product.entity.ProductLeadOutStock;
import com.skyeye.product.service.ProductLeadOutStockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "借出出库单", tags = "借出出库", modelName = "借出出库")
public class ProductLeadOutStockController {

    @Autowired
    private ProductLeadOutStockService productLeadOutStockService;

    /**
     * 获取借出出库单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductLeadOutStockList", value = "获取借出出库单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductLeadOutStockController/queryProductLeadOutStockList")
    public void queryProductLeadOutStockList(InputObject inputObject, OutputObject outputObject) {
        productLeadOutStockService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑借出出库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProductLeadOutStock", value = "新增/编辑借出出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ProductLeadOutStock.class)
    @RequestMapping("/post/ProductLeadOutStockController/writeProductLeadOutStock")
    public void writeProductLeadOutStock(InputObject inputObject, OutputObject outputObject) {
        productLeadOutStockService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 转仓库出库单时，根据id查询借出出库单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductLeadOutStockById", value = "转仓库出库单时，根据id查询借出出库单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductLeadOutStockController/queryProductLeadOutStockById")
    public void queryProductLeadOutStockById(InputObject inputObject, OutputObject outputObject) {
        productLeadOutStockService.selectById(inputObject, outputObject);
    }

    /**
     * 借出出库提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitProductOutStockToApproval", value = "借出出库提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductLeadOutStockController/submitProductOutStockToApproval")
    public void submitProductOutStockToApproval(InputObject inputObject, OutputObject outputObject) {
        productLeadOutStockService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除借出出库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteProductOutStock", value = "删除借出出库", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductLeadOutStockController/deleteProductOutStock")
    public void deleteProductOutStock(InputObject inputObject, OutputObject outputObject) {
        productLeadOutStockService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销借出出库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeProductOutStock", value = "撤销借出出库", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductLeadOutStockController/revokeProductOutStock")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        productLeadOutStockService.revoke(inputObject, outputObject);
    }

    /**
     * 借出出库单信息转仓库出库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertProductLeadOutStockToTurnDepot", value = "借出出库单信息转仓库出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotOut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductLeadOutStockController/insertProductLeadOutStockToTurnDepot")
    public void insertProductLeadOutStockToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        productLeadOutStockService.insertProductLeadOutStockToTurnDepot(inputObject, outputObject);
    }

}

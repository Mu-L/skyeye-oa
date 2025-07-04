/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.purchase.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.purchase.entity.*;
import com.skyeye.purchase.service.PurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PurchaseOrderController
 * @Description: 采购订单管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/14 10:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "采购订单", tags = "采购订单", modelName = "采购模块")
public class PurchaseOrderController {

    @Autowired
    private PurchaseOrderService purchaseOrderService;

    /**
     * 获取采购订单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "purchaseorder001", value = "获取采购订单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PurchaseOrderController/queryPurchaseOrderList")
    public void queryPurchaseOrderToList(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.queryPageList(inputObject, outputObject);
    }

    /**
     * 获取上个月采购订单成本
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryLastMonthPurchaseOrderCost", value = "获取上个月采购订单成本", method = "POST", allUse = "0")
    @RequestMapping("/post/PurchaseOrderController/queryLastMonthPurchaseOrderCost")
    public void queryLastMonthPurchaseOrderCost(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.queryLastMonthPurchaseOrderCost(inputObject, outputObject);
    }

    /**
     * 新增/编辑采购订单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writePurchaseOrder", value = "新增/编辑采购订单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseOrder.class)
    @RequestMapping("/post/PurchaseOrderController/writePurchaseOrder")
    public void writePurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 转采购入库单/到货单/采购退货单时，根据id查询采购订单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPurchaseOrderTransById", value = "转采购入库单/到货单/采购退货单/采购换货单时，根据id查询采购订单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseOrderController/queryPurchaseOrderTransById")
    public void queryPurchaseOrderTransById(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.queryPurchaseOrderTransById(inputObject, outputObject);
    }

    /**
     * 采购单信息转采购入库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "purchaseorder009", value = "采购单信息转采购入库", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchasePut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseOrderController/insertPurchaseOrderToTurnPut")
    public void insertPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.insertPurchaseOrderToTurnPut(inputObject, outputObject);
    }

    /**
     * 采购订单信息转到货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertPurchaseOrderToTurnDelivery", value = "采购订单信息转到货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseDelivery.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseDeliveryController/insertPurchaseOrderToTurnDelivery")
    public void insertPurchaseOrderToTurnDelivery(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.insertPurchaseOrderToTurnDelivery(inputObject, outputObject);
    }

    /**
     * 采购单信息转采购退货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertPurchaseOrderToReturns", value = "采购单信息转采购退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseReturn.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseOrderController/insertPurchaseOrderToReturns")
    public void insertPurchaseOrderToReturns(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.insertPurchaseOrderToReturns(inputObject, outputObject);
    }

    /**
     * 采购单信息转采购换货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertPurchaseExchange", value = "采购单信息转采购换货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseExchange.class, value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseOrderController/insertPurchaseExchange")
    public void insertPurchaseExchange(InputObject inputObject, OutputObject outputObject) {
        purchaseOrderService.insertPurchaseOrderToExchanges(inputObject, outputObject);
    }
}

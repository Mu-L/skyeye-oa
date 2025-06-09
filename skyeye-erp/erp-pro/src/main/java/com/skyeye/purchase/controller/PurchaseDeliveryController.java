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
import com.skyeye.inspection.entity.QualityInspection;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.service.PurchaseDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PurchaseDeliveryController
 * @Description: 到货单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/21 22:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "到货单", tags = "到货单", modelName = "采购模块")
public class PurchaseDeliveryController {

    @Autowired
    private PurchaseDeliveryService purchaseDeliveryService;

    /**
     * 获取到货单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPurchaseDeliveryList", value = "获取到货单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PurchaseDeliveryController/queryPurchaseDeliveryList")
    public void queryPurchaseDeliveryList(InputObject inputObject, OutputObject outputObject) {
        purchaseDeliveryService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑到货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writePurchaseDelivery", value = "新增/编辑到货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseDelivery.class)
    @RequestMapping("/post/PurchaseDeliveryController/writePurchaseDelivery")
    public void writePurchaseDelivery(InputObject inputObject, OutputObject outputObject) {
        purchaseDeliveryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 转质检单时，根据id查询到货单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPurchaseDeliveryTransById", value = "转质检单时，根据id查询到货单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseDeliveryController/queryPurchaseDeliveryTransById")
    public void queryPurchaseDeliveryTransById(InputObject inputObject, OutputObject outputObject) {
        purchaseDeliveryService.queryPurchaseDeliveryTransById(inputObject, outputObject);
    }

    /**
     * 到货单转质检单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deliveryToQualityInspection", value = "到货单转质检单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = QualityInspection.class, value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseDeliveryController/deliveryToQualityInspection")
    public void deliveryToQualityInspection(InputObject inputObject, OutputObject outputObject) {
        purchaseDeliveryService.deliveryToQualityInspection(inputObject, outputObject);
    }

    /**
     * 转采购入库单时，根据id查询到货单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPurchaseDeliveryTransPurchasePutById", value = "转采购入库单时，根据id查询到货单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseDeliveryController/queryPurchaseDeliveryTransPurchasePutById")
    public void queryPurchaseDeliveryTransPurchasePutById(InputObject inputObject, OutputObject outputObject) {
        purchaseDeliveryService.queryPurchaseDeliveryTransPurchasePutById(inputObject, outputObject);
    }

    /**
     * 到货单转采购入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deliveryToPurchasePut", value = "到货单转采购入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchasePut.class, value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseDeliveryController/deliveryToPurchasePut")
    public void deliveryToPurchasePut(InputObject inputObject, OutputObject outputObject) {
        purchaseDeliveryService.deliveryToPurchasePut(inputObject, outputObject);
    }

}

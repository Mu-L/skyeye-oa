package com.skyeye.purchase.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.depot.entity.DepotOut;
import com.skyeye.purchase.entity.PurchaseDelivery;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.service.PurchaseExchangesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PurchaseExchangesController
 * @Description: 采购换货单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/8 21:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "采购换货单", tags = "采购换货单", modelName = "采购模块")
public class PurchaseExchangesController {

    @Autowired
    private PurchaseExchangesService purchaseExchangesService;

    /**
     * 获取采购换货列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPurchaseExchangesToList", value = "获取采购换货列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PurchaseExchangesController/queryPurchaseExchangesToList")
    public void queryPurchaseExchangesToList(InputObject inputObject, OutputObject outputObject) {
        purchaseExchangesService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑采购换货信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExchanges", value = "新增/编辑采购换货信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseExchange.class)
    @RequestMapping("/post/PurchaseExchangesController/writePurchaseExchanges")
    public void writePurchaseExchanges(InputObject inputObject, OutputObject outputObject) {
        purchaseExchangesService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 转采购到货单时，根据id查询采购换货信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPurchaseExchangesTransToDeliveryById", value = "转采购到货单时，根据id查询采购换货信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseExchangesController/queryPurchaseExchangesTransToDeliveryById")
    public void queryPurchaseExchangesTransToDeliveryById(InputObject inputObject, OutputObject outputObject) {
        purchaseExchangesService.queryPurchaseExchangesTransToDeliveryById(inputObject, outputObject);
    }

    /**
     * 采购换货单信息转采购到货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertPurchaseExchangesToDelivery", value = "采购换货单信息转采购到货单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = PurchaseDelivery.class, value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseExchangesController/insertPurchaseExchangesToDelivery")
    public void insertPurchaseExchangesToDelivery(InputObject inputObject, OutputObject outputObject) {
        purchaseExchangesService.insertPurchaseExchangesToDelivery(inputObject, outputObject);
    }
}

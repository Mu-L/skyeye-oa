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
import com.skyeye.depot.entity.DepotPut;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.service.PurchasePutService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PurchasePutController
 * @Description: 采购入库单控制类
 * @author: skyeye云系列--卫志强
 * @date: 2019/10/16 15:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "采购入库单", tags = "采购入库单", modelName = "采购模块")
public class PurchasePutController {

    @Autowired
    private PurchasePutService purchasePutService;

    @ApiOperation(id = "purchaseput001", value = "获取采购入库单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PurchasePutController/queryPurchasePutList")
    public void queryPurchasePutList(InputObject inputObject, OutputObject outputObject) {
        purchasePutService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePurchasePut", value = "新增/编辑采购入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchasePut.class)
    @RequestMapping("/post/PurchasePutController/writePurchasePut")
    public void writePurchasePut(InputObject inputObject, OutputObject outputObject) {
        purchasePutService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPurchasePutTransById", value = "转仓库入库单时，根据id查询采购入库信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchasePutController/queryPurchasePutTransById")
    public void queryPurchasePutTransById(InputObject inputObject, OutputObject outputObject) {
        purchasePutService.queryPurchasePutTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertPurchasePutToTurnDepot", value = "采购入库单信息转仓库入库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DepotPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchasePutController/insertPurchasePutToTurnDepot")
    public void insertPurchasePutToTurnDepot(InputObject inputObject, OutputObject outputObject) {
        purchasePutService.insertPurchasePutToTurnDepot(inputObject, outputObject);
    }

}

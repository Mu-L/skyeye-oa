/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.assets.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.assets.entity.AssetPurchase;
import com.skyeye.eve.assets.entity.AssetPurchasePut;
import com.skyeye.eve.assets.entity.AssetPurchaseReturn;
import com.skyeye.eve.assets.service.AssetPurchaseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AssetPurchaseController
 * @Description: 资产采购单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/18 23:28
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "资产采购", tags = "资产采购", modelName = "资产模块")
public class AssetPurchaseController {

    @Autowired
    private AssetPurchaseService assetPurchaseService;

    @ApiOperation(id = "asset018", value = "获取资产采购单信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AssetPurchaseController/queryAssetPurchaseList")
    public void queryAssetPurchaseList(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLastMonthAssetPurchaseCost", value = "获取上个月各项目的成本", method = "POST", allUse = "0")
    @RequestMapping("/post/AssetPurchaseController/queryLastMonthAssetPurchaseCost")
    public void queryLastMonthAssetPurchaseCost(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.queryLastMonthAssetPurchaseCost(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAssetPurchase", value = "新增/编辑资产采购单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AssetPurchase.class)
    @RequestMapping("/post/AssetPurchaseController/writeAssetPurchase")
    public void writeAssetPurchase(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "asset020", value = "资产采购申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/AssetPurchaseController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "asset022", value = "作废资产采购申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssetPurchaseController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "asset037", value = "撤销资产采购申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/AssetPurchaseController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAssetPurchaseOrderTransById", value = "转采购入库单/采购退货单时，根据id查询采购订单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssetPurchaseController/queryAssetPurchaseOrderTransById")
    public void queryAssetPurchaseOrderTransById(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.queryAssetPurchaseOrderTransById(inputObject, outputObject);
    }

    @ApiOperation(id = "insertAssetPurchaseOrderToTurnPut", value = "采购单信息转采购入库", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AssetPurchasePut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssetPurchaseController/insertAssetPurchaseOrderToTurnPut")
    public void insertAssetPurchaseOrderToTurnPut(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.insertAssetPurchaseOrderToTurnPut(inputObject, outputObject);
    }

    @ApiOperation(id = "insertAssetPurchaseOrderToReturns", value = "采购单信息转采购退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = AssetPurchaseReturn.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AssetPurchaseController/insertAssetPurchaseOrderToReturns")
    public void insertAssetPurchaseOrderToReturns(InputObject inputObject, OutputObject outputObject) {
        assetPurchaseService.insertAssetPurchaseOrderToReturns(inputObject, outputObject);
    }

}

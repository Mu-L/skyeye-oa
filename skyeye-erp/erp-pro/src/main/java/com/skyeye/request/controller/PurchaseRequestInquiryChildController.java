/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.request.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.request.entity.PurchaseRequestInquiryChild;
import com.skyeye.request.service.PurchaseRequestInquiryChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PurchaseRequestServiceImpl
 * @Description: 采购申请询价明细控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 11:05
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "采购申请询价明细", tags = "采购申请询价明细", modelName = "采购申请询价明细")
public class PurchaseRequestInquiryChildController {

    @Autowired
    private PurchaseRequestInquiryChildService purchaseRequestInquiryChildService;

    @ApiOperation(id = "queryPurchaseRequestInquiryChildList", value = "获取我的报价明细列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PurchaseRequestInquiryChildController/queryPurchaseRequestInquiryChildList")
    public void queryPurchaseRequestInquiryChildList(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestInquiryChildService.queryPurchaseRequestInquiryChildList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPurchaseRequestInquiryChildById", value = "根据id查询采购申请询价明细", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseRequestInquiryChildController/queryPurchaseRequestInquiryChildById")
    public void queryPurchaseRequestInquiryChildById(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestInquiryChildService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "writePurchaseRequestInquiryChild", value = "新增/编辑采购申请询价明细", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = PurchaseRequestInquiryChild.class)
    @RequestMapping("/post/PurchaseRequestInquiryChildController/writePurchaseRequestInquiryChild")
    public void writePurchaseRequestInquiryChild(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestInquiryChildService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePurchaseRequestInquiryChild", value = "删除采购申请询价明细", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PurchaseRequestInquiryChildController/deletePurchaseRequestInquiryChild")
    public void deletePurchaseRequestInquiryChild(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestInquiryChildService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryEnterpriseQuoteByItemAndNorms", value = "查询当前企业用户针对某个单据下的某个商品的某个规格的报价信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "采购申请单据id", required = "required"),
        @ApiImplicitParam(id = "materialId", name = "materialId", value = "商品id", required = "required"),
        @ApiImplicitParam(id = "normsId", name = "normsId", value = "规格id", required = "required")})
    @RequestMapping("/post/PurchaseRequestInquiryChildController/queryEnterpriseQuoteByItemAndNorms")
    public void queryEnterpriseQuoteByItemAndNorms(InputObject inputObject, OutputObject outputObject) {
        purchaseRequestInquiryChildService.queryEnterpriseQuoteByItemAndNorms(inputObject, outputObject);
    }
}

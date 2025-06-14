/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.production.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.production.entity.Production;
import com.skyeye.production.entity.ProductionPlan;
import com.skyeye.production.service.ProductionPlanService;
import com.skyeye.purchase.entity.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProductionPlanController
 * @Description: 出货计划单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/21 20:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "出货计划单", tags = "出货计划单", modelName = "出货计划单")
public class ProductionPlanController {

    @Autowired
    private ProductionPlanService productionPlanService;

    /**
     * 获取出货计划单信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductionPlanList", value = "获取出货计划单信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductionPlanController/queryProductionPlanList")
    public void queryProductionPlanList(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑出货计划单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProductionPlan", value = "新增/编辑出货计划单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ProductionPlan.class)
    @RequestMapping("/post/ProductionPlanController/writeProductionPlan")
    public void writeProductionPlan(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitProductionPlanToApproval", value = "提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductionPlanController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除出货计划单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteProductionPlan", value = "删除出货计划单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionPlanController/deleteProductionPlan")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销出货计划单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeProductionPlan", value = "撤销出货计划单", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductionPlanController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.revoke(inputObject, outputObject);
    }

    /**
     * 转生产计划单时，根据id查询出货计划单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductionPlanTransById", value = "转生产计划单时，根据id查询出货计划单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionPlanController/queryProductionPlanTransById")
    public void queryProductionPlanTransById(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.queryProductionPlanTransById(inputObject, outputObject);
    }

    /**
     * 出货计划单转生产计划单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertProductionPlanToProduction", value = "出货计划单转生产计划单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Production.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionPlanController/insertProductionPlanToProduction")
    public void insertProductionPlanToProduction(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.insertProductionPlanToProduction(inputObject, outputObject);
    }

    /**
     * 转采购订单时，根据id查询出货计划单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductionPlanTransPurchaseOrderById", value = "转采购订单时，根据id查询出货计划单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionPlanController/queryProductionPlanTransPurchaseOrderById")
    public void queryProductionPlanTransPurchaseOrderById(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.queryProductionPlanTransPurchaseOrderById(inputObject, outputObject);
    }

    /**
     * 出货计划单转采购订单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertProductionPlanToPurchaseOrder", value = "出货计划单转采购订单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseOrder.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionPlanController/insertProductionPlanToPurchaseOrder")
    public void insertProductionPlanToPurchaseOrder(InputObject inputObject, OutputObject outputObject) {
        productionPlanService.insertProductionPlanToPurchaseOrder(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inspection.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.inspection.entity.QualityInspection;
import com.skyeye.inspection.service.QualityInspectionService;
import com.skyeye.purchase.entity.PurchaseExchange;
import com.skyeye.purchase.entity.PurchasePut;
import com.skyeye.purchase.entity.PurchaseReturn;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: QualityInspectionController
 * @Description: 质检单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 8:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "质检单", tags = "质检单", modelName = "质检单")
public class QualityInspectionController {

    @Autowired
    private QualityInspectionService qualityInspectionService;

    /**
     * 获取质检单信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryQualityInspectionList", value = "获取质检单信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/QualityInspectionController/queryQualityInspectionList")
    public void queryQualityInspectionList(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑质检单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeQualityInspection", value = "新增/编辑质检单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = QualityInspection.class)
    @RequestMapping("/post/QualityInspectionController/writeQualityInspection")
    public void writeQualityInspection(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 质检申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitQualityInspectionToApproval", value = "质检申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/QualityInspectionController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除质检申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteQualityInspection", value = "删除质检申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/deleteQualityInspection")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销质检申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeQualityInspection", value = "撤销质检申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.revoke(inputObject, outputObject);
    }

    /**
     * 转采购入库单时，根据id查询质检单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryQualityInspectionTransById", value = "转采购入库单时，根据id查询质检单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/queryQualityInspectionTransById")
    public void queryQualityInspectionTransById(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.queryQualityInspectionTransById(inputObject, outputObject);
    }

    /**
     * 质检单转采购入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "qualityInspectionToPurchasePut", value = "质检单转采购入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchasePut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/qualityInspectionToPurchasePut")
    public void qualityInspectionToPurchasePut(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.qualityInspectionToPurchasePut(inputObject, outputObject);
    }

    /**
     * 转采购退货单时，根据id查询质检单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryQualityInspectionTransReturnById", value = "转采购退货单时，根据id查询质检单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/queryQualityInspectionTransReturnById")
    public void queryQualityInspectionTransReturnById(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.queryQualityInspectionTransReturnById(inputObject, outputObject);
    }

    /**
     * 质检单转采购退货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "qualityInspectionToPurchaseReturn", value = "质检单转采购退货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseReturn.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/qualityInspectionToPurchaseReturn")
    public void qualityInspectionToPurchaseReturn(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.qualityInspectionToPurchaseReturn(inputObject, outputObject);
    }

    /**
     * 转采购换货单时，根据id查询质检单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryQualityInspectionTransExchangesById", value = "转采购换货单时，根据id查询质检单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/queryQualityInspectionTransExchangesById")
    public void queryQualityInspectionTransExchangesById(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.queryQualityInspectionTransExchangesById(inputObject, outputObject);
    }

    /**
     * 质检单转采购换货单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "qualityInspectionToPurchaseExchanges", value = "质检单转采购换货单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseExchange.class, value = {
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QualityInspectionController/qualityInspectionToPurchaseExchanges")
    public void qualityInspectionToPurchaseExchanges(InputObject inputObject, OutputObject outputObject) {
        qualityInspectionService.qualityInspectionToPurchaseExchanges(inputObject, outputObject);
    }

}

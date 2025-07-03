/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pick.entity.RequisitionMaterial;
import com.skyeye.pick.entity.RequisitionOutLet;
import com.skyeye.pick.service.RequisitionMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RequisitionMaterialController
 * @Description: 领料申请单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/27 12:50
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "领料单", tags = "领料单", modelName = "物料单")
public class RequisitionMaterialController {

    @Autowired
    private RequisitionMaterialService requisitionMaterialService;

    /**
     * 获取领料申请单列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "erppick001", value = "获取领料申请单列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/RequisitionMaterialController/queryRequisitionMaterialOrderList")
    public void queryRequisitionMaterialOrderList(InputObject inputObject, OutputObject outputObject) {
        requisitionMaterialService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑领料申请单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeRequisitionMaterial", value = "新增/编辑领料申请单信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = RequisitionMaterial.class)
    @RequestMapping("/post/RequisitionMaterialController/writeRequisitionMaterial")
    public void writeRequisitionMaterial(InputObject inputObject, OutputObject outputObject) {
        requisitionMaterialService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除领料申请单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteRequisitionMaterialById", value = "删除领料申请单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RequisitionMaterialController/deleteRequisitionMaterialById")
    public void deleteRequisitionMaterialById(InputObject inputObject, OutputObject outputObject) {
        requisitionMaterialService.deleteById(inputObject, outputObject);
    }

    /**
     * 领料申请单提交审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitRequisitionMaterial", value = "领料申请单提交审核", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/RequisitionMaterialController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        requisitionMaterialService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销领料申请单申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeRequisitionMaterial", value = "撤销领料申请单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/RequisitionMaterialController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        requisitionMaterialService.revoke(inputObject, outputObject);
    }

    /**
     * 转领料出库单时，根据id查询领料单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryRequisitionMaterialTransById", value = "转领料出库单时，根据id查询领料单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RequisitionMaterialController/queryRequisitionMaterialTransById")
    public void queryRequisitionMaterialTransById(InputObject inputObject, OutputObject outputObject) {
        requisitionMaterialService.queryRequisitionMaterialTransById(inputObject, outputObject);
    }

    /**
     * 领料单信息转领料出库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertRequisitionMaterialToTurnOut", value = "领料单信息转领料出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = RequisitionOutLet.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/RequisitionMaterialController/insertRequisitionMaterialToTurnOut")
    public void insertRequisitionMaterialToTurnOut(InputObject inputObject, OutputObject outputObject) {
        requisitionMaterialService.insertRequisitionMaterialToTurnOut(inputObject, outputObject);
    }

}

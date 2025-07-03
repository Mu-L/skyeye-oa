/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.contract.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.contract.entity.SupplierContract;
import com.skyeye.contract.service.SupplierContractService;
import com.skyeye.purchase.entity.PurchaseOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SupplierContractController
 * @Description: 供应商合同管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/24 16:58
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "供应商合同管理", tags = "供应商合同管理", modelName = "供应商合同管理")
public class SupplierContractController {

    @Autowired
    private SupplierContractService supplierContractService;

    /**
     * 获取合同列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySupplierContractList", value = "获取合同列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SupplierContractController/querySupplierContractList")
    public void querySupplierContractList(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑合同信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeSupplierContract", value = "新增/编辑合同信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SupplierContract.class)
    @RequestMapping("/post/SupplierContractController/writeSupplierContract")
    public void writeSupplierContract(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除合同信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteSupplierContractById", value = "删除合同信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/deleteSupplierContractById")
    public void deleteSupplierContractById(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id批量获取供应商合同信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySupplierContractByIds", value = "根据id批量获取供应商合同信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/querySupplierContractByIds")
    public void querySupplierContractById(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.selectByIds(inputObject, outputObject);
    }

    /**
     * 根据供应商id获取合同管理列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract008", value = "根据供应商id获取合同管理列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "所属第三方业务数据id")})
    @RequestMapping("/post/SupplierContractController/querySupplierContractListByObjectId")
    public void querySupplierContractListByObjectId(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.querySupplierContractListByObjectId(inputObject, outputObject);
    }

    /**
     * 合同提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract009", value = "合同提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/SupplierContractController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 合同执行
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract010", value = "合同执行", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/performSupplierContract")
    public void performSupplierContract(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.performSupplierContract(inputObject, outputObject);
    }

    /**
     * 合同关闭
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract011", value = "合同关闭", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/closeSupplierContract")
    public void closeSupplierContract(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.closeSupplierContract(inputObject, outputObject);
    }

    /**
     * 合同搁置
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract012", value = "合同搁置", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/shelveSupplierContract")
    public void shelveSupplierContract(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.shelveSupplierContract(inputObject, outputObject);
    }

    /**
     * 合同恢复
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract013", value = "合同恢复", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/recoverySupplierContract")
    public void recoverySupplierContract(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.recoverySupplierContract(inputObject, outputObject);
    }

    /**
     * 作废合同信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract015", value = "作废合同信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销合同审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "mysuppliercontract016", value = "撤销合同审批", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/SupplierContractController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.revoke(inputObject, outputObject);
    }

    /**
     * 转采购订单时，根据合同id查询合同信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySupplierContractTransById", value = "转采购订单时，根据合同id查询合同信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/querySupplierContractTransById")
    public void querySupplierContractTransById(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.querySupplierContractTransById(inputObject, outputObject);
    }

    /**
     * 转采购订单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "supplierContractToOrder", value = "转采购订单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PurchaseOrder.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SupplierContractController/supplierContractToOrder")
    public void supplierContractToOrder(InputObject inputObject, OutputObject outputObject) {
        supplierContractService.supplierContractToOrder(inputObject, outputObject);
    }

}

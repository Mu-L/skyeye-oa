package com.skyeye.product.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.product.entity.ProductLead;
import com.skyeye.product.service.ProductLeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "借出申请", tags = "借出申请", modelName = "借出申请")
public class ProductLeadController {

    @Autowired
    private ProductLeadService productLeadService;

    /**
     * 获取借出出库订单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductLeadList", value = "获取借出出库单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductLeadController/queryProductLeadList")
    public void queryProductLeadList(InputObject inputObject, OutputObject outputObject) {
        productLeadService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑借出出库申请单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProductLead", value = "新增/编辑借出出库单", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ProductLead.class)
    @RequestMapping("/post/ProductLeadController/writeProductLead")
    public void writeProductLead(InputObject inputObject, OutputObject outputObject) {
        productLeadService.saveOrUpdateEntity(inputObject, outputObject);
    }

//    /**
//     * 借出出库申请提交审批
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "applyLead", value = "借出出库申请提交审批", method = "POST", allUse = "2")
//    @ApiImplicitParams({
//        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
//        @ApiImplicitParam(id = "approvalId", name = "approvalId", value = "审批人", required = "required")})
//    @RequestMapping("/post/ProductLeadController/applyLead")
//    public void applyLead(InputObject inputObject, OutputObject outputObject) {
//        productLeadService.submitToApproval(inputObject, outputObject);
//    }
//
//    /**
//     * 作废借出出库申请
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "cancelLead", value = "作废借出出库申请", method = "POST", allUse = "2")
//    @ApiImplicitParams({
//        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
//    @RequestMapping("/post/ProductLeadController/cancelLead")
//    public void cancelLead(InputObject inputObject, OutputObject outputObject) {
//        productLeadService.invalid(inputObject, outputObject);
//    }
//
//    /**
//     * 撤销资产采购申请
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "revokeLead", value = "撤销资产采购申请", method = "PUT", allUse = "2")
//    @ApiImplicitParams({
//        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
//    @RequestMapping("/post/ProductLeadController/revokeLead")
//    public void revokeLead(InputObject inputObject, OutputObject outputObject) {
//        productLeadService.revoke(inputObject, outputObject);
//    }
//
  //    /**
//     * 转归还入库单时，根据id查询借出出库单信息
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "queryProductLeadToReturnGoodsById", value = "转归还入库单时，根据id查询借出出库单信息", method = "GET", allUse = "2")
//    @ApiImplicitParams({
//        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
//    @RequestMapping("/post/ProductLeadController/queryProductLeadToReturnGoodsById")
//    public void queryProductLeadToReturnGoodsById(InputObject inputObject, OutputObject outputObject) {
//        productLeadService.queryProductLeadToReturnGoodsById(inputObject, outputObject);
//    }
//
//    /**
//     * 借出出库单信息转归还入库单信息
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "insertLeadToRestitution", value = "采购单信息转采购入库", method = "POST", allUse = "2")
//    @ApiImplicitParams({
//        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
//        @ApiImplicitParam(id = "restitutionMation", name = "restitutionMation", value = "归还入库信息",required = "required,json")})
//    @RequestMapping("/post/ProductLeadController/insertLeadToRestitution")
//    public void insertLeadToRestitution(InputObject inputObject, OutputObject outputObject) {
//        productLeadService.insertLeadToRestitution(inputObject, outputObject);
//    }


}

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
import com.skyeye.machin.entity.Machin;
import com.skyeye.production.entity.Production;
import com.skyeye.production.service.ProductionService;
import com.skyeye.whole.entity.WholeOrderOut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ProductionController
 * @Description: 生产计划单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/9 10:15
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "生产计划单", tags = "生产计划单", modelName = "生产计划单")
public class ProductionController {

    @Autowired
    private ProductionService productionService;

    /**
     * 查询生产计划单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductionList", value = "查询生产计划单列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ProductionController/queryProductionList")
    public void queryProductionList(InputObject inputObject, OutputObject outputObject) {
        productionService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑生产计划单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeProduction", value = "新增/编辑生产计划单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Production.class)
    @RequestMapping("/post/ProductionController/writeProduction")
    public void writeProduction(InputObject inputObject, OutputObject outputObject) {
        productionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询生产计划单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductionById", value = "根据id查询生产计划单", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "生产计划单id", required = "required")})
    @RequestMapping("/post/ProductionController/queryProductionById")
    public void queryProductionById(InputObject inputObject, OutputObject outputObject) {
        productionService.selectById(inputObject, outputObject);
    }

    /**
     * 删除生产计划单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteProductionById", value = "删除生产计划单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionController/deleteProductionById")
    public void deleteProductionById(InputObject inputObject, OutputObject outputObject) {
        productionService.deleteById(inputObject, outputObject);
    }

    /**
     * 提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "erpproduction007", value = "提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ProductionController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        productionService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销生产计划单申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeProduction", value = "撤销生产计划单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ProductionController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        productionService.revoke(inputObject, outputObject);
    }

    /**
     * 转加工单时，根据id查询生产计划单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductionTransById", value = "转加工单时，根据id查询生产计划单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionController/queryProductionTransById")
    public void queryProductionTransById(InputObject inputObject, OutputObject outputObject) {
        productionService.queryProductionTransById(inputObject, outputObject);
    }

    /**
     * 生产计划单转加工单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertProductionToMachin", value = "生产计划单转加工单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Machin.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionController/insertProductionToMachin")
    public void insertProductionToMachin(InputObject inputObject, OutputObject outputObject) {
        productionService.insertProductionToMachin(inputObject, outputObject);
    }

    /**
     * 转整单委外单时，根据id查询生产计划单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryProductionTransWholeById", value = "转整单委外单时，根据id查询生产计划单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionController/queryProductionTransWholeById")
    public void queryProductionTransWholeById(InputObject inputObject, OutputObject outputObject) {
        productionService.queryProductionTransWholeById(inputObject, outputObject);
    }

    /**
     * 生产计划单转整单委外单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertProductionToWhole", value = "生产计划单转整单委外单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = WholeOrderOut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ProductionController/insertProductionToWhole")
    public void insertProductionToWhole(InputObject inputObject, OutputObject outputObject) {
        productionService.insertProductionToWhole(inputObject, outputObject);
    }

}

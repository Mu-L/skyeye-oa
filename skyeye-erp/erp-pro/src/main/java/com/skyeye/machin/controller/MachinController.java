/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machin.entity.Machin;
import com.skyeye.machin.service.MachinService;
import com.skyeye.pick.entity.PatchMaterial;
import com.skyeye.pick.entity.RequisitionMaterial;
import com.skyeye.pick.entity.ReturnMaterial;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MachinController
 * @Description: 加工单管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/12 18:34
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "加工单管理", tags = "加工单管理", modelName = "加工单管理")
public class MachinController {

    @Autowired
    private MachinService machinService;

    /**
     * 获取加工单列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "erpmachin001", value = "获取加工单列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MachinController/queryMachinOrderList")
    public void queryMachinOrderList(InputObject inputObject, OutputObject outputObject) {
        machinService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑加工单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeMachin", value = "新增/编辑加工单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Machin.class)
    @RequestMapping("/post/MachinController/writeMachin")
    public void writeMachin(InputObject inputObject, OutputObject outputObject) {
        machinService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询加工单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMachinById", value = "根据id查询加工单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "加工单id", required = "required")})
    @RequestMapping("/post/MachinController/queryMachinById")
    public void queryMachinById(InputObject inputObject, OutputObject outputObject) {
        machinService.selectById(inputObject, outputObject);
    }

    /**
     * 删除加工单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteMachinById", value = "删除加工单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinController/deleteMachinById")
    public void deleteMachinById(InputObject inputObject, OutputObject outputObject) {
        machinService.deleteById(inputObject, outputObject);
    }

    /**
     * 加工单提交审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "erpmachin007", value = "提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/MachinController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        machinService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销加工单申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeMachin", value = "撤销加工单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/MachinController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        machinService.revoke(inputObject, outputObject);
    }

    /**
     * 根据id查询加工单信息(甘特图)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMachinForGanttById", value = "根据id查询加工单信息(甘特图)", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "加工单id", required = "required")})
    @RequestMapping("/post/MachinController/queryMachinForGanttById")
    public void queryMachinForGanttById(InputObject inputObject, OutputObject outputObject) {
        machinService.queryMachinForGanttById(inputObject, outputObject);
    }

    /**
     * 转领料单/补料单时，根据id查询加工单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMachinTransRequestById", value = "转领料单/补料单时，根据id查询加工单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinController/queryMachinTransRequestById")
    public void queryMachinTransRequestById(InputObject inputObject, OutputObject outputObject) {
        machinService.queryMachinTransRequestById(inputObject, outputObject);
    }

    /**
     * 加工单信息转领料单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertMachinToPickRequest", value = "加工单信息转领料单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = RequisitionMaterial.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinController/insertMachinToPickRequest")
    public void insertMachinToPickRequest(InputObject inputObject, OutputObject outputObject) {
        machinService.insertMachinToPickRequest(inputObject, outputObject);
    }

    /**
     * 加工单信息转补料单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertMachinToPickPatch", value = "加工单信息转补料单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatchMaterial.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinController/insertMachinToPickPatch")
    public void insertMachinToPickPatch(InputObject inputObject, OutputObject outputObject) {
        machinService.insertMachinToPickPatch(inputObject, outputObject);
    }

    /**
     * 转退料单时，根据id查询加工单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMachinTransReturnById", value = "转退料单时，根据id查询加工单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinController/queryMachinTransReturnById")
    public void queryMachinTransReturnById(InputObject inputObject, OutputObject outputObject) {
        machinService.queryMachinTransReturnById(inputObject, outputObject);
    }

    /**
     * 加工单信息转退料单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertMachinToPickReturn", value = "加工单信息转退料单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ReturnMaterial.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinController/insertMachinToPickReturn")
    public void insertMachinToPickReturn(InputObject inputObject, OutputObject outputObject) {
        machinService.insertMachinToPickReturn(inputObject, outputObject);
    }

}

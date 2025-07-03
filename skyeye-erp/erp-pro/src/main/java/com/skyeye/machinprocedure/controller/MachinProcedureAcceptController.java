/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machinprocedure.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.machinprocedure.entity.MachinProcedureAccept;
import com.skyeye.machinprocedure.service.MachinProcedureAcceptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MachinProcedureAcceptController
 * @Description: 工序验收控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/24 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "工序验收", tags = "工序验收", modelName = "工序验收")
public class MachinProcedureAcceptController {

    @Autowired
    private MachinProcedureAcceptService machinProcedureAcceptService;

    /**
     * 查询工序验收列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMachinProcedureAcceptList", value = "查询工序验收列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/MachinProcedureAcceptController/queryMachinProcedureAcceptList")
    public void queryMachinProcedureAcceptList(InputObject inputObject, OutputObject outputObject) {
        machinProcedureAcceptService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑工序验收
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeMachinProcedureAccept", value = "新增/编辑加工单子单据工序验收", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MachinProcedureAccept.class)
    @RequestMapping("/post/MachinProcedureAcceptController/writeMachinProcedureAccept")
    public void writeMachinProcedureAccept(InputObject inputObject, OutputObject outputObject) {
        machinProcedureAcceptService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id删除工序验收
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteMachinProcedureAcceptById", value = "根据id删除工序验收", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinProcedureAcceptController/deleteMachinProcedureAcceptById")
    public void deleteMachinProcedureAcceptById(InputObject inputObject, OutputObject outputObject) {
        machinProcedureAcceptService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id查询工序验收
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMachinProcedureAcceptById", value = "根据id查询工序验收", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/MachinProcedureAcceptController/queryMachinProcedureAcceptById")
    public void queryMachinProcedureAcceptById(InputObject inputObject, OutputObject outputObject) {
        machinProcedureAcceptService.selectById(inputObject, outputObject);
    }

    /**
     * 提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitMachinProcedureAcceptToApproval", value = "提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/MachinProcedureAcceptController/submitMachinProcedureAcceptToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        machinProcedureAcceptService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeMachinProcedureAccept", value = "撤销申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/MachinProcedureAcceptController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        machinProcedureAcceptService.revoke(inputObject, outputObject);
    }

}

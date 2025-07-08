/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.reimbursement.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.reimbursement.entity.Reimbursement;
import com.skyeye.reimbursement.service.ReimbursementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReimbursementController
 * @Description: 报销订单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/4 16:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "报销订单", tags = "报销订单", modelName = "报销订单")
public class ReimbursementController {

    @Autowired
    private ReimbursementService reimbursementService;

    /**
     * 查询报销订单列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryReimbursementList", value = "查询报销订单列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReimbursementController/queryReimbursementList")
    public void queryReimbursementList(InputObject inputObject, OutputObject outputObject) {
        reimbursementService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑报销订单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeReimbursement", value = "新增/编辑报销订单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Reimbursement.class)
    @RequestMapping("/post/ReimbursementController/writeReimbursement")
    public void writeReimbursement(InputObject inputObject, OutputObject outputObject) {
        reimbursementService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除报销订单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteReimbursementById", value = "删除报销订单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReimbursementController/deleteReimbursementById")
    public void deleteReimbursementById(InputObject inputObject, OutputObject outputObject) {
        reimbursementService.deleteById(inputObject, outputObject);
    }

    /**
     * 提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitReimbursementToApproval", value = "报销订单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ReimbursementController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        reimbursementService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeReimbursement", value = "撤销报销订单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ReimbursementController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        reimbursementService.revoke(inputObject, outputObject);
    }

    /**
     * 报销订单分析
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryCostAnalysis", value = "报销订单分析", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id="year", name = "year", value = "年份", required = "required"),
            @ApiImplicitParam(id="month", name = "month", value = "月份")
    })
    @RequestMapping("/post/ReimbursementController/queryCostAnalysis")
    public void queryCostAnalysis(InputObject inputObject, OutputObject outputObject) {
        reimbursementService.queryCostAnalysis(inputObject, outputObject);
    }

}

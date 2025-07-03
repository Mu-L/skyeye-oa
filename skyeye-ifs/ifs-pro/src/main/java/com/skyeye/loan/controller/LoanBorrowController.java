/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.loan.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.loan.entity.LoanBorrow;
import com.skyeye.loan.service.LoanBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LoanBorrowController
 * @Description: 借款单控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/5 14:18
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "借款单", tags = "借款单", modelName = "借款单")
public class LoanBorrowController {

    @Autowired
    private LoanBorrowService loanBorrowService;

    /**
     * 查询借款单列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryLoanBorrowList", value = "查询借款单列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LoanBorrowController/queryLoanBorrowList")
    public void queryLoanBorrowList(InputObject inputObject, OutputObject outputObject) {
        loanBorrowService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑借款单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeLoanBorrow", value = "新增/编辑借款单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = LoanBorrow.class)
    @RequestMapping("/post/LoanBorrowController/writeLoanBorrow")
    public void writeLoanBorrow(InputObject inputObject, OutputObject outputObject) {
        loanBorrowService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除借款单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteLoanBorrowById", value = "删除借款单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LoanBorrowController/deleteLoanBorrowById")
    public void deleteLoanBorrowById(InputObject inputObject, OutputObject outputObject) {
        loanBorrowService.deleteById(inputObject, outputObject);
    }

    /**
     * 提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitLoanBorrowToApproval", value = "借款单提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/LoanBorrowController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        loanBorrowService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeLoanBorrow", value = "撤销借款单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/LoanBorrowController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        loanBorrowService.revoke(inputObject, outputObject);
    }

    /**
     * 借款单类型占比图
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryLoanBorrowTypePie", value = "借款单类型占比图", method = "POST", allUse = "1")
    @RequestMapping("/post/LoanBorrowController/queryLoanBorrowTypePie")
    public void queryLoanBorrowTypePie(InputObject inputObject, OutputObject outputObject) {
        loanBorrowService.queryLoanBorrowTypePie(inputObject, outputObject);
    }

    /**
     * 部门类型占比图
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryLoanBorrowDeptPie", value = "部门类型占比图", method = "POST", allUse = "1")
    @RequestMapping("/post/LoanBorrowController/queryLoanBorrowDeptPie")
    public void queryLoanBorrowDeptPie(InputObject inputObject, OutputObject outputObject) {
        loanBorrowService.queryLoanBorrowDeptPie(inputObject, outputObject);
    }

}

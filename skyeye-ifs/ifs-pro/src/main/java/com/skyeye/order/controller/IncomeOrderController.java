/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.order.entity.IncomeOrder;
import com.skyeye.order.service.IncomeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IncomeOrderController
 * @Description: 明细账管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 11:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "明细账管理", tags = "明细账管理", modelName = "明细账管理")
public class IncomeOrderController {

    @Autowired
    private IncomeOrderService incomeService;

    /**
     * 查询明细账列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "income001", value = "查询明细账列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/IncomeController/queryIncomeByList")
    public void queryIncomeByList(InputObject inputObject, OutputObject outputObject) {
        incomeService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑明细账
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeIncomeOrder", value = "新增/编辑明细账", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = IncomeOrder.class)
    @RequestMapping("/post/IncomeController/writeIncomeOrder")
    public void writeIncomeOrder(InputObject inputObject, OutputObject outputObject) {
        incomeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除明细账信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "income005", value = "删除明细账信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/IncomeController/deleteIncomeOrderById")
    public void deleteIncomeOrderById(InputObject inputObject, OutputObject outputObject) {
        incomeService.deleteById(inputObject, outputObject);
    }

    /**
     * 提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "income008", value = "明细账申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/IncomeController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        incomeService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "income009", value = "撤销明细账申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/IncomeController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        incomeService.revoke(inputObject, outputObject);
    }

}

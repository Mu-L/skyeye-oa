/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.payment.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.payment.service.WagesPaymentHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: WagesPaymentHistoryController
 * @Description: 薪资发放历史管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/22 18:11
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "薪资发放历史", tags = "薪资发放历史", modelName = "薪资发放历史")
public class WagesPaymentHistoryController {

    @Autowired
    private WagesPaymentHistoryService wagesPaymentHistoryService;

    /**
     * 获取所有已发放薪资发放历史列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wagespaymenthistory001", value = "获取所有已发放薪资发放历史列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WagesPaymentHistoryController/queryAllGrantWagesPaymentHistoryList")
    public void queryAllGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) {
        wagesPaymentHistoryService.queryAllGrantWagesPaymentHistoryList(inputObject, outputObject);
    }

    /**
     * 获取我的薪资发放历史列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wagespaymenthistory002", value = "获取我的薪资发放历史列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WagesPaymentHistoryController/queryMyWagesPaymentHistoryList")
    public void queryMyWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) {
        wagesPaymentHistoryService.queryMyWagesPaymentHistoryList(inputObject, outputObject);
    }

    /**
     * 获取所有待发放薪资列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "wagespaymenthistory003", value = "获取所有待发放薪资列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/WagesPaymentHistoryController/queryAllNotGrantWagesPaymentHistoryList")
    public void queryAllNotGrantWagesPaymentHistoryList(InputObject inputObject, OutputObject outputObject) {
        wagesPaymentHistoryService.queryAllNotGrantWagesPaymentHistoryList(inputObject, outputObject);
    }

    /**
     * 获取员工薪资条薪资
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryWagesStaffPaymentDetail", value = "获取员工薪资条薪资", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "staffId", name = "staffId", value = "员工id", required = "required"),
        @ApiImplicitParam(id = "payMonth", name = "payMonth", value = "薪资月份", required = "required")})
    @RequestMapping("/post/WagesPaymentHistoryController/queryWagesStaffPaymentDetail")
    public void queryWagesStaffPaymentDetail(InputObject inputObject, OutputObject outputObject) {
        wagesPaymentHistoryService.queryWagesStaffPaymentDetail(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStaffIdLastWages", value = "获取员工上个月薪资", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "tenantId", name = "tenantId", value = "租户id")
    })
    @RequestMapping("/post/WagesPaymentHistoryController/queryStaffIdLastWages")
    public void queryStaffIdLastWages(InputObject inputObject, OutputObject outputObject) {
        wagesPaymentHistoryService.queryStaffIdLastWages(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.invoice.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.invoice.entity.Invoice;
import com.skyeye.invoice.service.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: InvoiceController
 * @Description: 发票控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/3 19:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "发票管理", tags = "发票管理", modelName = "发票管理")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    /**
     * 获取发票列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryInvoiceList", value = "获取发票列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InvoiceController/queryInvoiceList")
    public void queryInvoiceList(InputObject inputObject, OutputObject outputObject) {
        invoiceService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑发票信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeInvoice", value = "新增/编辑发票信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Invoice.class)
    @RequestMapping("/post/InvoiceController/writeInvoice")
    public void writeInvoice(InputObject inputObject, OutputObject outputObject) {
        invoiceService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除发票信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteInvoiceById", value = "删除发票信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/InvoiceController/deleteInvoiceById")
    public void deleteInvoiceById(InputObject inputObject, OutputObject outputObject) {
        invoiceService.deleteById(inputObject, outputObject);
    }

    /**
     * 发票提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitInvoiceToApproval", value = "发票提交审批", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/InvoiceController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        invoiceService.submitToApproval(inputObject, outputObject);
    }


    /**
     * 撤销发票审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeInvoice", value = "撤销发票审批", method = "PUT", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/InvoiceController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        invoiceService.revoke(inputObject, outputObject);
    }

    /**
     * 发票开具
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllInvoiceList", value = "获取给客户所有的发票列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InvoiceController/queryAllInvoiceList")
    public void queryAllInvoiceList(InputObject inputObject, OutputObject outputObject) {
        invoiceService.queryAllInvoiceList(inputObject, outputObject);
    }

    /**
     * 发票统计
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryInvoiceStatistics", value = "发票统计", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id="year", name = "year", value = "年份", required = "required"),
            @ApiImplicitParam(id="month", name = "month", value = "月份")
    })
    @RequestMapping("/post/InvoiceController/queryInvoiceStatistics")
    public void queryInvoiceStatistics(InputObject inputObject, OutputObject outputObject) {
        invoiceService.queryInvoiceStatistics(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllInvoicesLists", value = "获取所有的发票列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InvoiceController/queryAllInvoicesLists")
    public void queryAllInvoicesLists(InputObject inputObject, OutputObject outputObject) {
        invoiceService.queryAllInvoicesLists(inputObject, outputObject);
    }
}

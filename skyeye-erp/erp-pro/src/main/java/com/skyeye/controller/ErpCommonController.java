/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.service.ErpCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ErpCommonController
 * @Description: ERP进销存公共接口控制类
 * @author: skyeye云系列--卫志强
 * @date: 2019/10/16 15:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "公共接口", tags = "公共接口", modelName = "公共接口")
public class ErpCommonController {

    @Autowired
    private ErpCommonService erpCommonService;

    @ApiOperation(id = "queryErpOrderById", value = "获取ERP单据详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "订单id", required = "required"),
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "单据类型，值为服务类的className", required = "required")})
    @RequestMapping("/post/ErpCommonController/queryDepotHeadDetailsMationById")
    public void queryDepotHeadDetailsMationById(InputObject inputObject, OutputObject outputObject) {
        erpCommonService.queryDepotHeadDetailsMationById(inputObject, outputObject);
    }

    @ApiOperation(id = "erpcommon005", value = "删除ERP单据信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "订单id", required = "required"),
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "单据类型，值为服务类的className", required = "required")})
    @RequestMapping("/post/ErpCommonController/deleteErpOrderById")
    public void deleteErpOrderById(InputObject inputObject, OutputObject outputObject) {
        erpCommonService.deleteErpOrderById(inputObject, outputObject);
    }

    @ApiOperation(id = "erpcommon003", value = "erp相关单据撤销审批", method = "PUT", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required"),
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "单据类型，值为服务类的className", required = "required")})
    @RequestMapping("/post/ErpCommonController/editDepotHeadToRevoke")
    public void editDepotHeadToRevoke(InputObject inputObject, OutputObject outputObject) {
        erpCommonService.editDepotHeadToRevoke(inputObject, outputObject);
    }

    @ApiOperation(id = "erpcommon006", value = "订单信息提交审核", method = "PUT", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class, value = {
        @ApiImplicitParam(id = "serviceClassName", name = "serviceClassName", value = "单据类型，值为服务类的className", required = "required")})
    @RequestMapping("/post/ErpCommonController/orderSubmitToApproval")
    public void orderSubmitToApproval(InputObject inputObject, OutputObject outputObject) {
        erpCommonService.orderSubmitToApproval(inputObject, outputObject);
    }

}

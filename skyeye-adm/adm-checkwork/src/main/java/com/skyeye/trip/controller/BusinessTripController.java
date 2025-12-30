/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.trip.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.trip.entity.BusinessTrip;
import com.skyeye.trip.service.BusinessTripService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: BusinessTripController
 * @Description: 出差申请
 * @author: skyeye云系列--卫志强
 * @date: 2021/4/6 22:02
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@RestController
@Api(value = "出差申请", tags = "出差申请", modelName = "出差申请")
public class BusinessTripController {

    @Autowired
    private BusinessTripService businessTripService;

    /**
     * 获取我的出差申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkbusinesstrip001", value = "获取我的出差申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/BusinessTripController/queryBusinessTripList")
    public void queryBusinessTripList(InputObject inputObject, OutputObject outputObject) {
        businessTripService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑出差申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeBusinessTrip", value = "新增/编辑出差申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = BusinessTrip.class)
    @RequestMapping("/post/BusinessTripController/writeBusinessTrip")
    public void writeBusinessTrip(InputObject inputObject, OutputObject outputObject) {
        businessTripService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 出差申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkbusinesstrip006", value = "出差申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/BusinessTripController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        businessTripService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废出差申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkbusinesstrip007", value = "作废出差申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/BusinessTripController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        businessTripService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销出差申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "checkworkbusinesstrip009", value = "撤销出差申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/BusinessTripController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        businessTripService.revoke(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.vehicle.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.vehicle.entity.VehicleUse;
import com.skyeye.eve.vehicle.service.VehicleApplyUseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: VehicleApplyUseController
 * @Description: 用车申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 17:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "用车申请", tags = "用车申请", modelName = "车辆模块")
public class VehicleApplyUseController {

    @Autowired
    private VehicleApplyUseService vehicleApplyUseService;

    /**
     * 获取发起的用车申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle014", value = "获取发起的用车申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/VehicleApplyUseController/queryVehicleUseList")
    public void queryVehicleUseList(InputObject inputObject, OutputObject outputObject) {
        vehicleApplyUseService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑用车申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeVehicleUse", value = "新增/编辑用车申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = VehicleUse.class)
    @RequestMapping("/post/VehicleApplyUseController/writeVehicleUse")
    public void writeVehicleUse(InputObject inputObject, OutputObject outputObject) {
        vehicleApplyUseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 用车申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle017", value = "用车申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/VehicleApplyUseController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        vehicleApplyUseService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废用车申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle018", value = "作废用车申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/VehicleApplyUseController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        vehicleApplyUseService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销用车申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "vehicle022", value = "撤销用车申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/VehicleApplyUseController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        vehicleApplyUseService.revoke(inputObject, outputObject);
    }

}

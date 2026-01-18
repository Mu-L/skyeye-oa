/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.store.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.store.entity.StaffTransfer;
import com.skyeye.store.service.StaffTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StaffTransferController
 * @Description: 员工调拨申请控制层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "员工调拨申请", tags = "员工调拨申请", modelName = "员工调拨申请")
public class StaffTransferController {

    @Autowired
    private StaffTransferService staffTransferService;

    @ApiOperation(id = "queryStaffTransferList", value = "获取我的员工调拨申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StaffTransferController/queryStaffTransferList")
    public void queryStaffTransferList(InputObject inputObject, OutputObject outputObject) {
        staffTransferService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeStaffTransfer", value = "新增/编辑员工调拨申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = StaffTransfer.class)
    @RequestMapping("/post/StaffTransferController/writeStaffTransfer")
    public void writeStaffTransfer(InputObject inputObject, OutputObject outputObject) {
        staffTransferService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStaffTransferById", value = "根据id查询员工调拨申请信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/StaffTransferController/queryStaffTransferById")
    public void queryStaffTransferById(InputObject inputObject, OutputObject outputObject) {
        staffTransferService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteStaffTransferById", value = "删除员工调拨申请", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/StaffTransferController/deleteStaffTransferById")
    public void deleteStaffTransferById(InputObject inputObject, OutputObject outputObject) {
        staffTransferService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitStaffTransferToApproval", value = "员工调拨申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/StaffTransferController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        staffTransferService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeStaffTransfer", value = "撤销员工调拨申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/StaffTransferController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        staffTransferService.revoke(inputObject, outputObject);
    }

}


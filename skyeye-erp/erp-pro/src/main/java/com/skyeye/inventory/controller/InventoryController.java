/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.inventory.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.inventory.entity.Inventory;
import com.skyeye.inventory.service.InventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: InventoryController
 * @Description: 盘点任务单据控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/18 15:43
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "盘点任务单", tags = "盘点任务单", modelName = "盘点任务单")
public class InventoryController {

    @Autowired
    private InventoryService inventoryService;

    /**
     * 获取盘点任务单信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryInventoryList", value = "获取盘点任务单信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InventoryController/queryInventoryList")
    public void queryInventoryList(InputObject inputObject, OutputObject outputObject) {
        inventoryService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑盘点任务单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeInventory", value = "新增/编辑盘点任务单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Inventory.class)
    @RequestMapping("/post/InventoryController/writeInventory")
    public void writeInventory(InputObject inputObject, OutputObject outputObject) {
        inventoryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 质检申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitInventoryToApproval", value = "质检申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/InventoryController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        inventoryService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 删除质检申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteInventory", value = "删除质检申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/InventoryController/deleteInventory")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        inventoryService.deleteById(inputObject, outputObject);
    }

    /**
     * 撤销质检申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeInventory", value = "撤销质检申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/InventoryController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        inventoryService.revoke(inputObject, outputObject);
    }

}

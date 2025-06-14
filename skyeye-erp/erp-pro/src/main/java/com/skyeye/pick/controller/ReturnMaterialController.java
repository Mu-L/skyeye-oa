/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pick.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pick.entity.ReturnMaterial;
import com.skyeye.pick.entity.ReturnPut;
import com.skyeye.pick.service.ReturnMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReturnMaterialController
 * @Description: 退料申请单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/12/20 10:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "退料单", tags = "退料单", modelName = "物料单")
public class ReturnMaterialController {

    @Autowired
    private ReturnMaterialService returnMaterialService;

    /**
     * 获取退料申请单列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "erppick002", value = "获取退料申请单列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ReturnMaterialController/queryReturnMaterialOrderList")
    public void queryReturnMaterialOrderList(InputObject inputObject, OutputObject outputObject) {
        returnMaterialService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑退料申请单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeReturnMaterial", value = "新增/编辑退料申请单信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ReturnMaterial.class)
    @RequestMapping("/post/ReturnMaterialController/writeReturnMaterial")
    public void writeReturnMaterial(InputObject inputObject, OutputObject outputObject) {
        returnMaterialService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除退料申请单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteReturnMaterialById", value = "删除退料申请单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReturnMaterialController/deleteReturnMaterialById")
    public void deleteReturnMaterialById(InputObject inputObject, OutputObject outputObject) {
        returnMaterialService.deleteById(inputObject, outputObject);
    }

    /**
     * 退料申请单提交审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitReturnMaterial", value = "退料申请单提交审核", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ReturnMaterialController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        returnMaterialService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销退料申请单申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeReturnMaterial", value = "撤销退料申请单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ReturnMaterialController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        returnMaterialService.revoke(inputObject, outputObject);
    }

    /**
     * 转退料入库单时，根据id查询退料单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryReturnMaterialTransById", value = "转退料入库单时，根据id查询退料单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReturnMaterialController/queryReturnMaterialTransById")
    public void queryReturnMaterialTransById(InputObject inputObject, OutputObject outputObject) {
        returnMaterialService.queryReturnMaterialTransById(inputObject, outputObject);
    }

    /**
     * 退料单信息转退料入库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertReturnMaterialToTurnOut", value = "退料单信息转退料入库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ReturnPut.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ReturnMaterialController/insertReturnMaterialToTurnOut")
    public void insertReturnMaterialToTurnOut(InputObject inputObject, OutputObject outputObject) {
        returnMaterialService.insertReturnMaterialToTurnOut(inputObject, outputObject);
    }

}

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
import com.skyeye.pick.entity.PatchMaterial;
import com.skyeye.pick.entity.PatchOutLet;
import com.skyeye.pick.service.PatchMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatchMaterialController
 * @Description: 补料申请单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/27 12:50
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "补料单", tags = "补料单", modelName = "物料单")
public class PatchMaterialController {

    @Autowired
    private PatchMaterialService patchMaterialService;

    /**
     * 获取补料申请单列表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "erppick003", value = "获取补料申请单列表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatchMaterialController/queryPatchMaterialOrderList")
    public void queryPatchMaterialOrderList(InputObject inputObject, OutputObject outputObject) {
        patchMaterialService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑补料申请单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writePatchMaterial", value = "新增/编辑补料申请单信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatchMaterial.class)
    @RequestMapping("/post/PatchMaterialController/writePatchMaterial")
    public void writePatchMaterial(InputObject inputObject, OutputObject outputObject) {
        patchMaterialService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除补料申请单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deletePatchMaterialById", value = "删除补料申请单信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatchMaterialController/deletePatchMaterialById")
    public void deletePatchMaterialById(InputObject inputObject, OutputObject outputObject) {
        patchMaterialService.deleteById(inputObject, outputObject);
    }

    /**
     * 补料申请单提交审核
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitPatchMaterial", value = "补料申请单提交审核", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/PatchMaterialController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        patchMaterialService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销补料申请单申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokePatchMaterial", value = "撤销补料申请单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/PatchMaterialController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        patchMaterialService.revoke(inputObject, outputObject);
    }

    /**
     * 转补料出库单时，根据id查询补料单信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPatchMaterialTransById", value = "转补料出库单时，根据id查询补料单信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatchMaterialController/queryPatchMaterialTransById")
    public void queryPatchMaterialTransById(InputObject inputObject, OutputObject outputObject) {
        patchMaterialService.queryPatchMaterialTransById(inputObject, outputObject);
    }

    /**
     * 补料单信息转补料出库单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "insertPatchMaterialToTurnOut", value = "补料单信息转补料出库单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatchOutLet.class, value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatchMaterialController/insertPatchMaterialToTurnOut")
    public void insertPatchMaterialToTurnOut(InputObject inputObject, OutputObject outputObject) {
        patchMaterialService.insertPatchMaterialToTurnOut(inputObject, outputObject);
    }

}

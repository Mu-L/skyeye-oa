/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.accessory.controller;

import com.skyeye.accessory.entity.SealApply;
import com.skyeye.accessory.entity.SealApplyChangeStock;
import com.skyeye.accessory.service.SealApplyService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealApplyController
 * @Description: 配件申领单管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2022/1/11 22:41
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "配件申领单管理", tags = "配件申领单管理", modelName = "配件申领单管理")
public class SealApplyController {

    @Autowired
    private SealApplyService sealApplyService;

    /**
     * 查询我的配件申领单列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealseservice023", value = "查询我的配件申领单列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealApplyController/querySealApplyList")
    public void querySealApplyList(InputObject inputObject, OutputObject outputObject) {
        sealApplyService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑配件申领单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeSealApply", value = "新增/编辑配件申领单", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SealApply.class)
    @RequestMapping("/post/SealApplyController/writeSealApply")
    public void writeSealApply(InputObject inputObject, OutputObject outputObject) {
        sealApplyService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除配件申领单
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteSealApplyById", value = "删除配件申领单", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealApplyController/deleteSealApplyById")
    public void deleteSealApplyById(InputObject inputObject, OutputObject outputObject) {
        sealApplyService.deleteById(inputObject, outputObject);
    }

    /**
     * 配件申领单申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitSealApplyToApproval", value = "配件申领单申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/SealApplyController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        sealApplyService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 撤销配件申领单申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeSealApply", value = "撤销配件申领单申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/SealApplyController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        sealApplyService.revoke(inputObject, outputObject);
    }

    /**
     * 修改配件申领单出库状态
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "editSealApplyOtherState", value = "修改配件申领单出库状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required"),
        @ApiImplicitParam(id = "otherState", name = "otherState", value = "出库状态", required = "required,num")})
    @RequestMapping("/post/SealApplyController/editSealApplyOtherState")
    public void editSealApplyOtherState(InputObject inputObject, OutputObject outputObject) {
        sealApplyService.editSealApplyOtherState(inputObject, outputObject);
    }

    /**
     * 修改配件申领单已出库的数量
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "editSealApplyOutNum", value = "修改配件申领单已出库的数量", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SealApplyChangeStock.class)
    @RequestMapping("/post/SealApplyController/editSealApplyOutNum")
    public void editSealApplyOutNum(InputObject inputObject, OutputObject outputObject) {
        sealApplyService.editSealApplyOutNum(inputObject, outputObject);
    }

}

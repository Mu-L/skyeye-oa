/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.seal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.seal.entity.SealRevert;
import com.skyeye.eve.seal.service.SealApplyRevertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SealApplyRevertController
 * @Description: 印章归还申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 17:39
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "印章归还", tags = "印章归还", modelName = "印章模块")
public class SealApplyRevertController {

    @Autowired
    private SealApplyRevertService sealApplyRevertService;

    /**
     * 获取我发起的印章归还列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealrevert001", value = "获取我发起的印章归还列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SealApplyRevertController/querySealRevertList")
    public void querySealRevertList(InputObject inputObject, OutputObject outputObject) {
        sealApplyRevertService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑印章归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeSealRevert", value = "新增/编辑印章归还申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SealRevert.class)
    @RequestMapping("/post/SealApplyRevertController/writeSealRevert")
    public void writeSealRevert(InputObject inputObject, OutputObject outputObject) {
        sealApplyRevertService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 印章归还申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealrevert006", value = "印章归还申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/SealApplyRevertController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        sealApplyRevertService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废印章归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealrevert007", value = "作废印章归还申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SealApplyRevertController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        sealApplyRevertService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销印章归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "sealrevert010", value = "撤销印章归还申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/SealApplyRevertController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        sealApplyRevertService.revoke(inputObject, outputObject);
    }

}

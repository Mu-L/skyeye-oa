/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.licence.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.licence.entity.LicenceRevert;
import com.skyeye.eve.licence.service.LicenceApplyRevertService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LicenceApplyRevertController
 * @Description: 证照归还申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 10:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "证照归还", tags = "证照归还", modelName = "证照模块")
public class LicenceApplyRevertController {

    @Autowired
    private LicenceApplyRevertService licenceApplyRevertService;

    /**
     * 获取我发起的的证照归还列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licencerevert001", value = "获取我发起的的证照归还列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LicenceApplyRevertController/queryLicenceRevertList")
    public void queryLicenceRevertList(InputObject inputObject, OutputObject outputObject) {
        licenceApplyRevertService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑证照归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeLicenceRevert", value = "新增/编辑证照归还申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = LicenceRevert.class)
    @RequestMapping("/post/LicenceApplyRevertController/writeLicenceRevert")
    public void writeLicenceRevert(InputObject inputObject, OutputObject outputObject) {
        licenceApplyRevertService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 证照归还申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licencerevert006", value = "证照归还申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/LicenceApplyRevertController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        licenceApplyRevertService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废证照归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licencerevert007", value = "作废证照归还申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LicenceApplyRevertController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        licenceApplyRevertService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销证照归还申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licencerevert010", value = "撤销证照归还申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/LicenceApplyRevertController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        licenceApplyRevertService.revoke(inputObject, outputObject);
    }

}

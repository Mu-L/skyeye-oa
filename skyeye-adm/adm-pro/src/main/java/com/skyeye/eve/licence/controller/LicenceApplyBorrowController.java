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
import com.skyeye.eve.licence.entity.LicenceUse;
import com.skyeye.eve.licence.service.LicenceApplyBorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LicenceApplyBorrowController
 * @Description: 证照借用控制层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 22:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "证照借用", tags = "证照借用", modelName = "证照模块")
public class LicenceApplyBorrowController {

    @Autowired
    private LicenceApplyBorrowService licenceApplyBorrowService;

    /**
     * 获取我发起的证照借用列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licenceborrow001", value = "获取我发起的证照借用列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LicenceApplyBorrowController/queryLicenceBorrowList")
    public void queryLicenceBorrowList(InputObject inputObject, OutputObject outputObject) {
        licenceApplyBorrowService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑证照借用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeLicenceUse", value = "新增/编辑证照借用申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = LicenceUse.class)
    @RequestMapping("/post/LicenceApplyBorrowController/writeLicenceUse")
    public void writeLicenceUse(InputObject inputObject, OutputObject outputObject) {
        licenceApplyBorrowService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 证照借用申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licenceborrow006", value = "证照借用申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/LicenceApplyBorrowController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        licenceApplyBorrowService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废证照借用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licenceborrow007", value = "作废证照借用申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LicenceApplyBorrowController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        licenceApplyBorrowService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销证照借用申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "licenceborrow010", value = "撤销证照借用申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/LicenceApplyBorrowController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        licenceApplyBorrowService.revoke(inputObject, outputObject);
    }

}

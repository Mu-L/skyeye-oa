/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personrequire.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personrequire.entity.PersonRequire;
import com.skyeye.personrequire.service.PersonRequireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PersonRequireController
 * @Description: 人员需求控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/8 16:01
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "人员需求申请", tags = "人员需求申请", modelName = "人员需求申请")
public class PersonRequireController {

    @Autowired
    private PersonRequireService personRequireService;

    @ApiOperation(id = "queryBossPersonRequireList", value = "获取我发起的人员需求申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PersonRequireController/queryPersonRequireList")
    public void queryPersonRequireList(InputObject inputObject, OutputObject outputObject) {
        personRequireService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePersonRequire", value = "新增/编辑人员需求申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PersonRequire.class)
    @RequestMapping("/post/PersonRequireController/writePersonRequire")
    public void writePersonRequire(InputObject inputObject, OutputObject outputObject) {
        personRequireService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "submitPersonRequire", value = "人员需求申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/PersonRequireController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        personRequireService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidPersonRequire", value = "作废人员需求申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "人员需求主键id", required = "required")})
    @RequestMapping("/post/PersonRequireController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        personRequireService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "revokePersonRequire", value = "撤销人员需求申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程id", required = "required")})
    @RequestMapping("/post/PersonRequireController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        personRequireService.revoke(inputObject, outputObject);
    }

    @ApiOperation(id = "setPersonLiable", value = "人员需求申请责任人设置", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "人员需求主键id", required = "required"),
        @ApiImplicitParam(id = "personLiable", name = "personLiable", value = "人员需求的责任人", required = "required,json")})
    @RequestMapping("/post/PersonRequireController/setPersonLiable")
    public void setPersonLiable(InputObject inputObject, OutputObject outputObject) {
        personRequireService.setPersonLiable(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyChargePersonRequireList", value = "获取我负责的人员需求申请", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PersonRequireController/queryMyChargePersonRequireList")
    public void queryMyChargePersonRequireList(InputObject inputObject, OutputObject outputObject) {
        personRequireService.queryMyChargePersonRequireList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllBossPersonRequireList", value = "获取所有审批通过状态之后的人员需求申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PersonRequireController/queryAllPersonRequireList")
    public void queryAllPersonRequireList(InputObject inputObject, OutputObject outputObject) {
        personRequireService.queryAllPersonRequireList(inputObject, outputObject);
    }

}

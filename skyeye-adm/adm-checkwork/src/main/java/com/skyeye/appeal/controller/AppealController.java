/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.appeal.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.appeal.entity.Appeal;
import com.skyeye.appeal.service.AppealService;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AppealController
 * @Description: 考勤申诉管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/7/18 11:40
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "考勤申诉", tags = "考勤申诉", modelName = "考勤申诉")
public class AppealController {

    @Autowired
    private AppealService appealService;

    @ApiOperation(id = "queryAppealList", value = "获取我的考勤申诉列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AppealController/queryAppealList")
    public void queryAppealList(InputObject inputObject, OutputObject outputObject) {
        appealService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAppeal", value = "新增/编辑考勤申诉", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Appeal.class)
    @RequestMapping("/post/AppealController/writeAppeal")
    public void writeAppeal(InputObject inputObject, OutputObject outputObject) {
        appealService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "submitAppeal", value = "考勤申诉提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/AppealController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        appealService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidAppeal", value = "作废考勤申诉", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AppealController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        appealService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeAppeal", value = "撤销考勤申诉", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/AppealController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        appealService.revoke(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.regularworker.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.regularworker.entity.RegularWorker;
import com.skyeye.regularworker.service.RegularWorkerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: RegularWorkerController
 * @Description: 转正申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-24 15:16:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "转正申请", tags = "转正申请", modelName = "转正申请")
public class RegularWorkerController {

    @Autowired
    private RegularWorkerService regularWorkerService;

    /**
     * 获取我发起的转正申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryBossInterviewRegularWorkerList", value = "获取我发起的转正申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/RegularWorkerController/queryRegularWorkerList")
    public void queryRegularWorkerList(InputObject inputObject, OutputObject outputObject) {
        regularWorkerService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑转正申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeRegularWorker", value = "新增/编辑转正申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = RegularWorker.class)
    @RequestMapping("/post/RegularWorkerController/writeRegularWorker")
    public void writeRegularWorker(InputObject inputObject, OutputObject outputObject) {
        regularWorkerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 转正申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "submitRegularWorker", value = "转正申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/RegularWorkerController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        regularWorkerService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废转正申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "invalidRegularWorker", value = "作废转正申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "转正申请主键id", required = "required")})
    @RequestMapping("/post/RegularWorkerController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        regularWorkerService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销转正申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "revokeRegularWorker", value = "撤销转正申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程id", required = "required")})
    @RequestMapping("/post/RegularWorkerController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        regularWorkerService.revoke(inputObject, outputObject);
    }

}

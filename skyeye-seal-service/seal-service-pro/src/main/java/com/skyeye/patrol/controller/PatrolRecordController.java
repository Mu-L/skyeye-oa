/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.patrol.entity.PatrolRecord;
import com.skyeye.patrol.service.PatrolRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PatrolRecordController
 * @Description: 巡检记录控制层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "巡检记录", tags = "巡检记录", modelName = "巡检记录")
public class PatrolRecordController {

    @Autowired
    private PatrolRecordService patrolRecordService;

    @ApiOperation(id = "queryPatrolRecordList", value = "获取巡检记录列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PatrolRecordController/queryPatrolRecordList")
    public void queryPatrolRecordList(InputObject inputObject, OutputObject outputObject) {
        patrolRecordService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writePatrolRecord", value = "新增/编辑巡检记录", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PatrolRecord.class)
    @RequestMapping("/post/PatrolRecordController/writePatrolRecord")
    public void writePatrolRecord(InputObject inputObject, OutputObject outputObject) {
        patrolRecordService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPatrolRecordById", value = "根据ID查询巡检记录详情", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolRecordController/queryPatrolRecordById")
    public void queryPatrolRecordById(InputObject inputObject, OutputObject outputObject) {
        patrolRecordService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePatrolRecordById", value = "根据ID删除巡检记录", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PatrolRecordController/deletePatrolRecordById")
    public void deletePatrolRecordById(InputObject inputObject, OutputObject outputObject) {
        patrolRecordService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "submitToApprovalPatrolRecord", value = "巡检记录提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/PatrolRecordController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        patrolRecordService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "revokePatrolRecord", value = "撤销巡检记录审批申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/PatrolRecordController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        patrolRecordService.revoke(inputObject, outputObject);
    }

}


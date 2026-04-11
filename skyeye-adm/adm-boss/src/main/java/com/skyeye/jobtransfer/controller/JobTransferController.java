/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.jobtransfer.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.jobtransfer.entity.JobTransfer;
import com.skyeye.jobtransfer.service.JobTransferService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: JobTransferController
 * @Description: 岗位调动申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-27 15:57:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "岗位调动申请", tags = "岗位调动申请", modelName = "岗位调动申请")
public class JobTransferController {

    @Autowired
    private JobTransferService jobTransferService;

    @ApiOperation(id = "queryBossInterviewJobTransferList", value = "获取我发起的岗位调动申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/JobTransferController/queryJobTransferList")
    public void queryJobTransferList(InputObject inputObject, OutputObject outputObject) {
        jobTransferService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeJobTransfer", value = "新增/编辑岗位调动申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = JobTransfer.class)
    @RequestMapping("/post/JobTransferController/writeJobTransfer")
    public void writeJobTransfer(InputObject inputObject, OutputObject outputObject) {
        jobTransferService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "submitJobTransfer", value = "岗位调动申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/JobTransferController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        jobTransferService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidJobTransfer", value = "作废岗位调动申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "岗位调动申请主键id", required = "required")})
    @RequestMapping("/post/JobTransferController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        jobTransferService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeJobTransfer", value = "撤销岗位调动申请", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程id", required = "required")})
    @RequestMapping("/post/JobTransferController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        jobTransferService.revoke(inputObject, outputObject);
    }

}

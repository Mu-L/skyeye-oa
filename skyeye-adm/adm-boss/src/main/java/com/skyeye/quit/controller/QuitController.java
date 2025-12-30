/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.quit.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.quit.entity.Quit;
import com.skyeye.quit.service.QuitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: QuitController
 * @Description: 离职申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-25 18:08:53
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "离职申请", tags = "离职申请", modelName = "离职申请")
public class QuitController {

    @Autowired
    private QuitService quitService;

    @ApiOperation(id = "queryBossInterviewQuitList", value = "获取我发起的离职申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/QuitController/queryInterviewQuitList")
    public void queryInterviewQuitList(InputObject inputObject, OutputObject outputObject) {
        quitService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeInterviewQuit", value = "新增/编辑离职申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Quit.class)
    @RequestMapping("/post/QuitController/writeInterviewQuit")
    public void writeInterviewQuit(InputObject inputObject, OutputObject outputObject) {
        quitService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "submitInterviewQuit", value = "离职申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/QuitController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        quitService.submitToApproval(inputObject, outputObject);
    }

    @ApiOperation(id = "invalidInterviewQuit", value = "作废离职申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "离职申请主键id", required = "required")})
    @RequestMapping("/post/QuitController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        quitService.invalid(inputObject, outputObject);
    }

    @ApiOperation(id = "revokeInterviewQuit", value = "撤销离职申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程id", required = "required")})
    @RequestMapping("/post/QuitController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        quitService.revoke(inputObject, outputObject);
    }

}

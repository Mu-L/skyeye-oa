/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.conference.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.features.SubmitSkyeyeFlowable;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.conference.entity.ConferenceRoomReserve;
import com.skyeye.eve.conference.service.ConferenceRoomReserveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ConferenceRoomReserveController
 * @Description: 会议室预定申请控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 14:17
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "会议室预定", tags = "会议室预定", modelName = "会议室模块")
public class ConferenceRoomReserveController {

    @Autowired
    private ConferenceRoomReserveService conferenceRoomReserveService;

    /**
     * 获取我发起的会议室预定申请列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "conferenceroomreserve001", value = "获取我发起的会议室预定申请列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ConferenceRoomReserveController/queryConferenceRoomReserveList")
    public void queryConferenceRoomReserveList(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomReserveService.queryPageList(inputObject, outputObject);
    }

    /**
     * 新增/编辑会议室预定申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeConferenceRoomReserve", value = "新增/编辑会议室预定申请", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ConferenceRoomReserve.class)
    @RequestMapping("/post/ConferenceRoomReserveController/writeConferenceRoomReserve")
    public void writeConferenceRoomReserve(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomReserveService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 会议室预定申请提交审批
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "conferenceroomreserve006", value = "会议室预定申请提交审批", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = SubmitSkyeyeFlowable.class)
    @RequestMapping("/post/ConferenceRoomReserveController/submitToApproval")
    public void submitToApproval(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomReserveService.submitToApproval(inputObject, outputObject);
    }

    /**
     * 作废会议室预定申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "conferenceroomreserve007", value = "作废会议室预定申请", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConferenceRoomReserveController/invalid")
    public void invalid(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomReserveService.invalid(inputObject, outputObject);
    }

    /**
     * 撤销会议室预定申请
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "conferenceroomreserve010", value = "撤销会议室预定申请", method = "PUT", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "processInstanceId", name = "processInstanceId", value = "流程实例id", required = "required")})
    @RequestMapping("/post/ConferenceRoomReserveController/revoke")
    public void revoke(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomReserveService.revoke(inputObject, outputObject);
    }

}

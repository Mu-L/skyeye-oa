/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.conference.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.conference.entity.ConferenceRoom;
import com.skyeye.eve.conference.service.ConferenceRoomService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ConferenceRoomController
 * @Description: 会议室管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/1 15:23
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "会议室管理", tags = "会议室管理", modelName = "会议室模块")
public class ConferenceRoomController {

    @Autowired
    private ConferenceRoomService conferenceRoomService;

    @ApiOperation(id = "conferenceroom001", value = "查询会议室列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ConferenceRoomController/queryConferenceRoomList")
    public void queryConferenceRoomList(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeConferenceRoom", value = "新增/修改会议室信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ConferenceRoom.class)
    @RequestMapping("/post/ConferenceRoomController/writeConferenceRoom")
    public void writeConferenceRoom(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "conferenceroom003", value = "根据id删除会议室信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConferenceRoomController/deleteConferenceRoomById")
    public void deleteConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "conferenceroom004", value = "会议室恢复正常", method = "POST", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConferenceRoomController/normalConferenceRoomById")
    public void normalConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.normalConferenceRoomById(inputObject, outputObject);
    }

    @ApiOperation(id = "conferenceroom005", value = "会议室维修", method = "POST", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConferenceRoomController/repairConferenceRoomById")
    public void repairConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.repairConferenceRoomById(inputObject, outputObject);
    }

    @ApiOperation(id = "conferenceroom006", value = "会议室报废", method = "POST", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConferenceRoomController/scrapConferenceRoomById")
    public void scrapConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.scrapConferenceRoomById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryConferenceRoomById", value = "根据id查询会议室信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ConferenceRoomController/queryConferenceRoomById")
    public void queryConferenceRoomById(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "conferenceroomreserve008", value = "获取所有正常的会议室列表", method = "GET", allUse = "2")
    @RequestMapping("/post/ConferenceRoomController/queryAllConferenceRoomList")
    public void queryAllConferenceRoomList(InputObject inputObject, OutputObject outputObject) {
        conferenceRoomService.queryAllConferenceRoomList(inputObject, outputObject);
    }

}

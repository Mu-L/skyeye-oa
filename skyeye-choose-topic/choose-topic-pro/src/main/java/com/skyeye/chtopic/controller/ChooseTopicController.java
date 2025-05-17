/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chtopic.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.chtopic.service.ChooseTopicService;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ChooseTopicController
 * @Description: 课题管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/8 10:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "课题管理", tags = "课题管理", modelName = "课题管理")
public class ChooseTopicController {

    @Autowired
    private ChooseTopicService chooseTopicService;

    @ApiOperation(id = "queryChooseTopicList", value = "分页获取课题信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class,
        value = {@ApiImplicitParam(id = "objectId", name = "objectId", value = "活动id", required = "required")})
    @RequestMapping("/post/ChooseTopicController/queryChooseTopicList")
    public void queryUserList(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "importChooseTopic", value = "上传课题信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "activityId", name = "activityId", value = "课题活动id")})
    @RequestMapping("/post/ChooseTopicController/importChooseTopic")
    public void importChooseTopic(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.importChooseTopic(inputObject, outputObject);
    }

    @ApiOperation(id = "exportChooseTopic", value = "导出选题结果信息", method = "GET", allUse = "0")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "activityId", name = "activityId", value = "课题活动id")})
    @RequestMapping("/post/ChooseTopicController/exportChooseTopic")
    public void exportChooseTopic(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.exportChooseTopic(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteChooseTopicById", value = "删除课题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required")})
    @RequestMapping("/post/ChooseTopicController/deleteChooseTopicById")
    public void deleteChooseTopicById(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "chooseTopicById", value = "选题/指导老师", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required"),
        @ApiImplicitParam(id = "teacherId", name = "teacherId", value = "教师id")})
    @RequestMapping("/post/ChooseTopicController/chooseTopicById")
    public void chooseTopicById(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.chooseTopicById(inputObject, outputObject);
    }

    @ApiOperation(id = "cnacleChooseTopicById", value = "取消选题", method = "PUT", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required")})
    @RequestMapping("/post/ChooseTopicController/cnacleChooseTopicById")
    public void cnacleChooseTopicById(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.cnacleChooseTopicById(inputObject, outputObject);
    }

    /**
     * 取消指导老师
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "cancelTeacherResult", value = "取消指导老师", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required")})
    @RequestMapping("/post/ChooseTopicController/cancelTeacherResult")
    public void cancelTeacherResult(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.cancelTeacherResult(inputObject, outputObject);
    }

    @ApiOperation(id = "queryChooseMyTopicList", value = "获取选择我作为指导老师的课题信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "activityId", name = "activityId", value = "活动id")})
    @RequestMapping("/post/ChooseTopicController/queryChooseMyTopicList")
    public void queryChooseMyTopicList(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.queryChooseMeTopicList(inputObject, outputObject);
    }

    @ApiOperation(id = "changeResultForTeacher", value = "修改老师对学生选择的结果", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "课题id", required = "required"),
        @ApiImplicitParam(id = "teacherResult", name = "teacherResult", value = "结果，1同意 2拒绝", required = "required,num")})
    @RequestMapping("/post/ChooseTopicController/changeResultForTeacher")
    public void changeResultForTeacher(InputObject inputObject, OutputObject outputObject) {
        chooseTopicService.changeResultForTeacher(inputObject, outputObject);
    }

}

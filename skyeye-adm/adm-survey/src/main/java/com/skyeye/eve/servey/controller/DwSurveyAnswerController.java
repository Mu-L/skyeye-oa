package com.skyeye.eve.servey.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.servey.entity.DwSurveyAnswer;
import com.skyeye.eve.servey.service.DwSurveyAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "问卷回答信息表管理", tags = "问卷回答信息表管理", modelName = "问卷回答信息表管理")
public class DwSurveyAnswerController {

    @Autowired
    private DwSurveyAnswerService dwSurveyAnswerService;

    @ApiOperation(id = "writeDwSurveyAnswer", value = "新增问卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwSurveyAnswer.class)
    @RequestMapping("/post/DwSurveyAnswerController/writeDwSurveyAnswer")
    public void writeDwSurveyAnswer(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSurveyAnswerById", value = "根据ID删除问卷回答信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwSurveyAnswerController/deleteSurveyAnswerById")
    public void deleteSurveyAnswerById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySurveyAnswerById", value = "根据ID获取问卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwSurveyAnswerController/querySurveyAnswerById")
    public void querySurveyAnswerById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.selectById(inputObject, outputObject);//仅用了方法
    }

    @ApiOperation(id = "queryMySurveyAnswerList", value = "获取我的问卷回答信息", method = "POST", allUse = "2")
    @RequestMapping("/post/DwSurveyAnswerController/queryMySurveyAnswerList")
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.queryMySurveyAnswerList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNoOrYesSurveyAnswerList", value = "获取已/待批阅问卷信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "state", name = "state", value = "批阅状态", required = "required")})
    @RequestMapping("/post/DwSurveyAnswerController/queryNoOrYesSurveyAnswerList")
    public void queryNoOrYesSurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.queryNoOrYesSurveyAnswerList(inputObject, outputObject);
    }

    @ApiOperation(id = "querySurveyAnswerBySurveyId", value = "根据问卷surveyId(holderId)获取问卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyAnswerController/querySurveyAnswerBySurveyId")
    public void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.querySurveyAnswerBySurveyId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFilterApprovedSurveys", value = "筛选已批阅问卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyAnswerController/queryFilterApprovedSurveys")
    public void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.queryFilterApprovedSurveys(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFilterToBeReviewedSurveys", value = "筛选待批阅问卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyAnswerController/queryFilterToBeReviewedSurveys")
    public void queryFilterToBeReviewedSurveys(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.queryFilterToBeReviewedSurveys(inputObject, outputObject);
    }

    @ApiOperation(id = "querySurveyAnswerByDirectoryIdAndUserId", value = "根据试卷ID和用户Id获取问卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwSurveyAnswerController/querySurveyAnswerByDirectoryIdAndUserId")
    public void querySurveyAnswerByDirectoryIdAndUserId(InputObject inputObject, OutputObject outputObject) {
        dwSurveyAnswerService.querySurveyAnswerByDirectoryIdAndUserId(inputObject, outputObject);//仅用了方法
    }
}

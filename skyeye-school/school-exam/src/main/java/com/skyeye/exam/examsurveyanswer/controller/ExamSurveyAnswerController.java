package com.skyeye.exam.examsurveyanswer.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamSurveyAnswerController
 * @Description: 试卷回答信息表管理控制层
 * @author: skyeye云系列--luyujia
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "试卷回答信息表管理", tags = "试卷回答信息表管理", modelName = "试卷回答信息表管理")
public class ExamSurveyAnswerController {

    @Autowired
    private ExamSurveyAnswerService examSurveyAnswerService;

    /**
     * 新增试卷回答信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamSurveyAnswer", value = "新增试卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamSurveyAnswer.class)
    @RequestMapping("/post/ExamSurveyAnswerController/writeExamSurveyAnswer")
    public void writeExamSurveyAnswer(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据ID删除试卷回答信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteSurveyAnswerById", value = "根据ID删除试卷回答信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamSurveyAnswerController/deleteSurveyAnswerById")
    public void deleteSurveyAnswerById(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据ID获取试卷回答信息
     * 学生角度,获取我的某个的回答信息
     * 教师角度,获取某个学生的回答信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySurveyAnswerById", value = "根据ID获取试卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamSurveyAnswerController/querySurveyAnswerById")
    public void querySurveyAnswerById(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.selectById(inputObject, outputObject);
    }

    /**
     * 根据试卷ID和用户Id获取回答信息和试卷信息
     * 学生角度根据试卷id去获取所有的回答与试卷信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySurveyBySurveyIdAndUserId", value = "根据试卷ID和用户Id获取回答信息和试卷信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "surveyId", name = "surveyId", value = "试卷Id", required = "required")})
    @RequestMapping("/post/ExamSurveyAnswerController/querySurveyBySurveyIdAndUserId")
    public void querySurveyAnswerBquerySurveyBySurveyIdAndUserIdyId(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.querySurveyBySurveyIdAndUserId(inputObject, outputObject);
    }

    /**
     * 根据createId获取my试卷回答信息
     * 学生角度,获取我的所有试卷回答信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMySurveyAnswerList", value = "根据createId获取我的试卷回答信息", method = "POST", allUse = "2")
    @RequestMapping("/post/ExamSurveyAnswerController/queryMySurveyAnswerList")
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.queryMySurveyAnswerList(inputObject, outputObject);
    }

    /**
     * 获取已/待批阅试卷信息
     * 教师角度,获取所有需要批阅的试卷(仅仅是试卷，不是回答者的回答的试卷信息)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllSurveyList", value = "获取所有批阅试卷信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyAnswerController/queryAllSurveyList")
    public void queryAllSurveyList(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.queryAllSurveyList(inputObject, outputObject);
    }

    /**
     * 根据SurveyId获取试卷回答信息
     * 这张试卷下的所有回答者（学生）的信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySurveyAnswerBySurveyId", value = "根据试卷Id获取试卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyAnswerController/querySurveyAnswerBySurveyId")
    public void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.querySurveyAnswerBySurveyId(inputObject, outputObject);
    }

    /**
     * 筛选已批阅获取试卷回答信息
     * 教师角度,获取所有已经或未曾批阅的试卷(仅仅是回答者信息)
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFilterApprovedSurveys", value = "筛选未/已批阅 试卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyAnswerController/queryFilterApprovedSurveys")
    public void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.queryFilterApprovedSurveys(inputObject, outputObject);
    }

}

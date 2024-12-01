package com.skyeye.exam.examSurveyAnswer.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examSurveyAnswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examSurveyAnswer.service.ExamSurveyAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamSurveyAnswerController
 * @Description: 试卷回答信息表管理控制层
 * @author: skyeye云系列--lqy
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
        examSurveyAnswerService.createEntity(inputObject, outputObject);
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
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySurveyAnswerById", value = "根据ID获取试卷回答信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamSurveyAnswerController/querySurveyAnswerById")
    public void querySurveyAnswerById(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.selectById(inputObject, outputObject);
    }

    /**
     * 根据createId获取my试卷回答信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMySurveyAnswerList", value = "根据createId获取my试卷回答信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "createId", name = "createId", value = "回答人的id", required = "required")})
    @RequestMapping("/post/ExamSurveyAnswerController/queryMySurveyAnswerList")
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        examSurveyAnswerService.queryMySurveyAnswerList(inputObject, outputObject);
    }


}

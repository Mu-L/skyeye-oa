package com.skyeye.exam.examsurveyquanswer.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examananswer.entity.ExamAnAnswer;
import com.skyeye.exam.examsurveyquanswer.entity.ExamSurveyQuAnswer;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName: ExamSurveyQuAnswerController
 * @Description: 答卷 题目和所得分数的关联表管理控制层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 题目和所得分数的关联表管理", tags = "答卷 题目和所得分数的关联表管理", modelName = "答卷 题目和所得分数的关联表管理")
public class ExamSurveyQuAnswerController {

    @Autowired
    private ExamSurveyQuAnswerService examSurveyQuAnswerService;

    /**
     * 新增/编辑题目和所得分数的关联表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamSurveyQuAnswer", value = "新增/编辑题目和所得分数的关联表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ExamSurveyQuAnswer.class)
    @RequestMapping("/post/ExamSurveyQuAnswerController/writeExamSurveyQuAnswer")
    public void writeExamSurveyQuAnswer(InputObject inputObject, OutputObject outputObject) {
        examSurveyQuAnswerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取题目和所得分数的关联表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamSurveyQuAnswerList", value = "获取题目和所得分数的关联表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyQuAnswerController/queryExamSurveyQuAnswerList")
    public void queryExamSurveyQuAnswerList(InputObject inputObject, OutputObject outputObject) {
        examSurveyQuAnswerService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除题目和所得分数的关联表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamSurveyQuAnswerById", value = "删除题目和所得分数的关联表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamSurveyQuAnswerController/deleteExamSurveyQuAnswerById")
    public void deleteExamSurveyQuAnswerById(InputObject inputObject, OutputObject outputObject) {
        examSurveyQuAnswerService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取题目和所得分数的关联表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamSurveyQuAnswerListById", value = "根据id获取题目和所得分数的关联表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamSurveyQuAnswerController/queryExamSurveyQuAnswerListById")
    public void queryExamSurveyQuAnswerListById(InputObject inputObject, OutputObject outputObject) {
        examSurveyQuAnswerService.queryExamSurveyQuAnswerListById(inputObject, outputObject);
    }
}

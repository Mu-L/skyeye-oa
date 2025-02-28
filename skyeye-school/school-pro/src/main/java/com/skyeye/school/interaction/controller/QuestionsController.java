package com.skyeye.school.interaction.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.interaction.entity.QuestionCategories;
import com.skyeye.school.interaction.entity.Questions;
import com.skyeye.school.interaction.service.QuestionsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: questionsController
 * @Description: 互动答题题目管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/17 12:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "互动答题题目管理", tags = "互动答题题目管理", modelName = "互动答题题目管理")
public class QuestionsController {

    @Autowired
    private QuestionsService questionsService;

    /**
     * 新增/编辑题目类别信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeQuestion1", value = "新增/编辑题目信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Questions.class)
    @RequestMapping("/post/QuestionsController/writeQuestion")
    public void writeQuestion(InputObject inputObject, OutputObject outputObject) {
        questionsService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据subjectClassesId查询题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryQuestionListBySubjectClassesId", value = "根据subjectClassesId查询题目信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectClassesId", name = "subjectClassesId", value = "根据subjectClassesId查询题目信息",required = "require")})
    @RequestMapping("/post/QuestionsController/queryQuestionListBySubjectClassesId")
    public void queryQuestionListBySubjectClassesId(InputObject inputObject, OutputObject outputObject) {
        questionsService.queryQuestionListBySubjectClassesId(inputObject, outputObject);
    }

    /**
     * 根据题目id查询题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryQuestionById", value = "根据题目id查询题目信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "根据题目id查询题目信息",required = "require")})
    @RequestMapping("/post/QuestionsController/queryQuestionById")
    public void queryQuestionById(InputObject inputObject, OutputObject outputObject) {
        questionsService.selectById(inputObject, outputObject);
    }

    /**
     * 根据题目id删除题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteQuestionById", value = "根据题目id删除题目信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "根据题目id删除题目信息",required = "require")})
    @RequestMapping("/post/QuestionsController/deleteQuestionById")
    public void deleteQuestionById(InputObject inputObject, OutputObject outputObject) {
        questionsService.deleteById(inputObject, outputObject);
    }
}

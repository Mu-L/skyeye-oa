/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.examquestion.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.eve.examquestion.service.QuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: QuestionController
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/15 15:21
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "题目管理", tags = "题目管理", modelName = "题库管理")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    /**
     * 添加/编辑问题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeQuestion", value = "添加/编辑问题", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Question.class)
    @RequestMapping("/post/QuestionController/writeQuestion")
    public void writeQuestion(InputObject inputObject, OutputObject outputObject) {
        questionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 分页获取题目信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryQuestionList", value = "分页获取题目信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/QuestionController/queryQuestionList")
    public void queryQuestionList(InputObject inputObject, OutputObject outputObject) {
        questionService.queryQuestionLists(inputObject, outputObject);
    }

    /**
     * 分页获取我的题目信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyQuestionList", value = "分页获取我的题目信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/QuestionController/queryMyQuestionList")
    public void queryMyQuestionList(InputObject inputObject, OutputObject outputObject) {
        questionService.queryPageList(inputObject, outputObject);
    }

    /**
     * 分页获取所有题库列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPageQuestionList", value = "分页获取所有题库列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/QuestionController/queryPageQuestionList")
    public void queryPageQuestionList(InputObject inputObject, OutputObject outputObject) {
        questionService.queryPageQuestionList(inputObject, outputObject);
    }

    /**
     * 根据Id查询问题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectQuestionById", value = "根据Ids批量查询问题", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "ids", name = "ids", value = "主键ids", required = "required")})
    @RequestMapping("/post/QuestionController/selectQuestionById")
    public void selectQuestionById(InputObject inputObject, OutputObject outputObject) {
        questionService.selectByIds(inputObject, outputObject);
    }

    /**
     * 根据ID删除题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteQuestionById", value = "根据ID删除题目信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/QuestionController/deleteQuestionById")
    public void deleteQuestionById(InputObject inputObject, OutputObject outputObject) {
        questionService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据科目ID查询题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectQuestionBySubjectId", value = "根据科目ID查询题目信息传holderId=subjectId", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/QuestionController/selectQuestionBySubjectId")
    public void selectQuestionBySubjectId(InputObject inputObject, OutputObject outputObject) {
        questionService.selectQuestionBySubjectId(inputObject, outputObject);
    }

    /**
     * 筛选题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFilterQuestionList", value = "筛选题目信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/QuestionController/queryFilterQuestionList")
    public void queryFilterQuestionList(InputObject inputObject, OutputObject outputObject) {
        questionService.queryFilterQuestionList(inputObject, outputObject);
    }


}

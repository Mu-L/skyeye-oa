package com.skyeye.exam.examSurveyDirectory.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examSurveyDirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examSurveyDirectory.service.ExamSurveyDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamSurveyDirectoryController
 * @Description: 试卷管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "试卷管理", tags = "试卷管理", modelName = "试卷管理")
public class ExamSurveyDirectoryController {

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

    /**
     * 获取所有试卷列表
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "exam001", value = "获取所有试卷列表", method = "POST", allUse = "1")
    @RequestMapping("/post/ExamSurveyDirectoryController/queryAllExamList")
    public void queryAllExamList(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryAllOrMyExamList(inputObject, outputObject);
    }


    /**
     * 获取我的试卷列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "exam001-my", value = "分页获取我的试卷列表", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "createId", name = "createId", value = "创建人Id",required = "required")
    })
    @RequestMapping("/post/ExamSurveyDirectoryController/queryPageMyExamList")
    public void queryPageMyExamList(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryAllOrMyExamList(inputObject, outputObject);
    }

    /**
     * 新增试卷
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "exam002", value = "新增试卷", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ExamSurveyDirectory.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/writeExamDirectory")
    public void writeExamDirectory(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.createEntity(inputObject, outputObject);
    }

    /**
     * 根据id获取试卷信息
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "exam003", value = "根据id获取试卷信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id",required = "required")
    })
    @RequestMapping("/post/ExamSurveyDirectoryController/queryDirectoryById")
    public void queryDirectoryById(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.selectById(inputObject, outputObject);
    }

    /**
     * 根据id删除试卷信息
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "exam025", value = "根据id获取试卷信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id",required = "required")
    })
    @RequestMapping("/post/ExamSurveyDirectoryController/deleteDirectoryById")
    public void deleteDirectoryById(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.deleteDirectoryById(inputObject, outputObject);

    }
}

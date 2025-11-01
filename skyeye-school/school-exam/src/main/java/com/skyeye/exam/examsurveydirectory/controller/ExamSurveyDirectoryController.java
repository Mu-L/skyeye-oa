package com.skyeye.exam.examsurveydirectory.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamSurveyDirectoryController
 * @Description: 试卷管理控制层
 * @author: skyeye云系列--luyujia
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "试卷管理", tags = "试卷管理", modelName = "试卷管理")
public class ExamSurveyDirectoryController {

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

    @ApiOperation(id = "writeExamDirectory", value = "新增/编辑试卷", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamSurveyDirectory.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/writeExamDirectory")
    public void writeExamDirectory(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "setUpExamDirectory", value = "发布试卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id", required = "required")})
    @RequestMapping("/post/ExamSurveyDirectoryController/setUpExamDirectory")
    public void setUpExamDirectory(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.setUpExamDirectory(inputObject, outputObject);
    }

    @ApiOperation(id = "copyExamDirectory", value = "复制试卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id", required = "required"),
        @ApiImplicitParam(id = "surveyName", name = "surveyName", value = "试卷名称")})
    @RequestMapping("/post/ExamSurveyDirectoryController/copyExamDirectory")
    public void copyExamDirectory(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.copyExamDirectory(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllExamList", value = "获取所有试卷列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/queryAllExamList")
    public void queryAllExamList(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryAllExamList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyExamList", value = "获取我的试卷列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/queryMyExamList")
    public void queryMyExamList(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryMyExamList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDirectoryById", value = "根据id获取试卷信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id", required = "required")})
    @RequestMapping("/post/ExamSurveyDirectoryController/queryDirectoryById")
    public void queryDirectoryById(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "changeWhetherDeleteById", value = "删除试卷信息,及改变whether_delete字段为2", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id", required = "required")})
    @RequestMapping("/post/ExamSurveyDirectoryController/changeWhetherDeleteById")
    public void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.changeWhetherDeleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSurvey", value = "删除试卷信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id", required = "required")})
    @RequestMapping("/post/ExamSurveyDirectoryController/deleteSurvey")
    public void deleteSurvey(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "takeExam", value = "是否可以参加考试", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id", required = "required")})
    @RequestMapping("/post/ExamSurveyDirectoryController/takeExam")
    public void takeExam(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.takeExam(inputObject, outputObject);
    }

    @ApiOperation(id = "updateExamMationEndById", value = "手动结束试卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "试卷id", required = "required")})
    @RequestMapping("/post/ExamSurveyDirectoryController/updateExamMationEndById")
    public void updateExamMationEndById(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.updateExamMationEndById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFilterExamLists", value = "筛选试卷", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/queryFilterExamLists")
    public void queryFilterExamLists(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryFilterExamLists(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMySurvey", value = "筛选我的试卷列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/queryMySurvey")
    public void queryMySurvey(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryMySurvey(inputObject, outputObject);
    }


    @ApiOperation(id = "querySurveyListBySubjectLinkId", value = "筛选科目和班级Id以及发布的试卷列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/querySurveyListBySubjectLinkId")
    public void querySurveyListBySubjectLinkId(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.querySurveyListBySubjectLinkId(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyDoSurvey", value = "获取我需做试卷，并过滤已做试卷。学生角度过滤已做试卷。教师角度获取我可以做的试卷，并显示哪些已做过", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/queryMyDoSurvey")
    public void queryMyDoSurvey(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryMyDoSurvey(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFilterNoSurveys", value = "筛选未/已批阅试卷信息。教师角度,筛选未/已批阅试卷，看状态", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamSurveyDirectoryController/queryFilterNoSurveys")
    public void queryFilterNoSurveys(InputObject inputObject, OutputObject outputObject) {
        examSurveyDirectoryService.queryFilterNoSurveys(inputObject, outputObject);
    }

}

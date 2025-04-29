package com.skyeye.eve.question.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwSurveyDirectory;
import com.skyeye.eve.question.service.DwSurveyDirectoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "问卷管理", tags = "问卷管理", modelName = "问卷管理")
public class DwSurveyDirectoryController {

    @Autowired
    private DwSurveyDirectoryService dwSurveyDirectoryService;

    @ApiOperation(id = "writeDwDirectory", value = "新增/编辑问卷", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwSurveyDirectory.class)
    @RequestMapping("/post/DwSurveyDirectoryController/writeDwDirectory")
    public void writeDwDirectory(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "setUpDwDirectory", value = "发布问卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/setUpDwDirectory")
    public void setUpDwDirectory(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.setUpDwDirectory(inputObject, outputObject);
    }

    @ApiOperation(id = "copyDwDirectory", value = "复制问卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required"),
        @ApiImplicitParam(id = "surveyName", name = "surveyName", value = "问卷名称")})
    @RequestMapping("/post/DwSurveyDirectoryController/copyDwDirectory")
    public void copyDwDirectory(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.copyDwDirectory(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDirectoryById", value = "根据id获取问卷信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/queryDirectoryById")
    public void queryDirectoryById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "changeWhetherDeleteById", value = "删除问卷信息,及改变whether_delete字段为2", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/changeWhetherDeleteById")
    public void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.changeWhetherDeleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSurvey", value = "物理删除问卷信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/deleteSurvey")
    public void deleteSurvey(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "takeExam", value = "是否可以参加考试", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/takeExam")
    public void takeExam(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.takeExam(inputObject, outputObject);
    }

    @ApiOperation(id = "updateDwMationEndById", value = "手动结束问卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/updateDwMationEndById")
    public void updateDwMationEndById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.updateDwMationEndById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFilterDwLists", value = "分页筛选问卷", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyDirectoryController/queryFilterDwLists")
    public void queryFilterDwLists(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.queryFilterDwLists(inputObject, outputObject);
    }

    @ApiOperation(id = "queryMyDwurvey", value = "筛选我的问卷列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyDirectoryController/queryMyDwurvey")
    public void queryMyDwurvey(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.queryMyDwurvey(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDwurveyMationById", value = "分析报告试卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/queryDwurveyMationById")
    public void queryDwurveyMationById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.queryDwurveyMationById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryDwSurveyDirectoryMationByIdToHTML", value = "获取调查问卷题目信息用来生成html页面", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/queryDwSurveyDirectoryMationByIdToHTML")
    public void queryDwSurveyDirectoryMationByIdToHTML(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.queryDwSurveyDirectoryMationByIdToHTML(inputObject, outputObject);
    }

}

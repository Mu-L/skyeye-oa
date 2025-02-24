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

    /**
     * 新增/编辑问卷
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwDirectory", value = "新增/编辑问卷", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwSurveyDirectory.class)
    @RequestMapping("/post/DwSurveyDirectoryController/writeDwDirectory")
    public void writeDwDirectory(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 发布问卷
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "setUpDwDirectory", value = "发布问卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/setUpDwDirectory")
    public void setUpDwDirectory(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.setUpDwDirectory(inputObject, outputObject);
    }

    /**
     * 复制问卷
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "copyDwDirectory", value = "复制问卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required"),
            @ApiImplicitParam(id = "surveyName", name = "surveyName", value = "问卷名称")})
    @RequestMapping("/post/DwSurveyDirectoryController/copyDwDirectory")
    public void copyDwDirectory(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.copyDwDirectory(inputObject, outputObject);
    }

    /**
     * 获取所有问卷列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllDwList", value = "获取所有问卷列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyDirectoryController/queryAllDwList")
    public void queryAllDwList(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.queryAllDwList(inputObject, outputObject);
    }

    /**
     * 获取我的问卷列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyDwList", value = "获取我的问卷列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyDirectoryController/queryMyDwList")
    public void queryMyDwList(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.queryMyDwList(inputObject, outputObject);
    }

    /**
     * 根据id获取问卷信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDirectoryById", value = "根据id获取问卷信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/queryDirectoryById")
    public void queryDirectoryById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.selectById(inputObject, outputObject);
    }

    /**
     * 逻辑删除问卷信息,及改变whether_delete字段为2
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeWhetherDeleteById", value = "删除问卷信息,及改变whether_delete字段为2", method = "POST", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/changeWhetherDeleteById")
    public void changeWhetherDeleteById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.changeWhetherDeleteById(inputObject, outputObject);
    }

    /**
     * 物理删除问卷信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteSurvey", value = "删除问卷信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/deleteSurvey")
    public void deleteSurvey(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.deleteById(inputObject, outputObject);
    }

    /**
     * 是否可以参加考试
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "takeExam", value = "是否可以参加考试", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/takeExam")
    public void takeExam(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.takeExam(inputObject, outputObject);
    }

    /**
     * 手动结束问卷
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "updateDwMationEndById", value = "手动结束问卷", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "问卷id", required = "required")})
    @RequestMapping("/post/DwSurveyDirectoryController/updateDwMationEndById")
    public void updateDwMationEndById(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.updateDwMationEndById(inputObject, outputObject);
    }

    /**
     * 分页筛选问卷
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryFilterDwLists", value = "筛选问卷", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwSurveyDirectoryController/queryFilterDwLists")
    public void queryFilterDwLists(InputObject inputObject, OutputObject outputObject) {
        dwSurveyDirectoryService.queryFilterDwLists(inputObject, outputObject);
    }

}

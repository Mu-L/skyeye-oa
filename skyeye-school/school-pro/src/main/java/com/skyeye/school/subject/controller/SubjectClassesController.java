/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SubjectClassesController
 * @Description: 科目表与班级表的关系控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/10 14:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "科目表与班级表的关系", tags = "科目表与班级表的关系", modelName = "科目管理")
public class SubjectClassesController {

    @Autowired
    private SubjectClassesService subjectClassesService;

    @ApiOperation(id = "querySubjectClassesList", value = "分页获取科目下的班级列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SubjectClassesController/querySubjectClassesList")
    public void querySubjectClassesList(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryNoPageSubjectClassesList", value = "不分页获取科目下的班级列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "objectId", name = "objectId", value = "科目Id")})
    @RequestMapping("/post/SubjectClassesController/queryNoPageSubjectClassesList")
    public void queryNoPageSubjectClassesList(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.queryNoPageSubjectClassesList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeSubjectClasses", value = "添加或修改科目与班级的关系", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubjectClasses.class)
    @RequestMapping("/post/SubjectClassesController/writeSubjectClasses")
    public void writeSubjectClasses(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSubjectClassesById", value = "根据ID删除科目与班级的关系", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SubjectClassesController/deleteSubjectClassesById")
    public void deleteSubjectClassesById(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "querySubjectClassesBySourceCode", value = "根据编码查询科目与班级的关系信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "sourceCode", name = "sourceCode", value = "编码", required = "required")})
    @RequestMapping("/post/SubjectClassesController/querySubjectClassesBySourceCode")
    public void querySubjectClassesBySourceCode(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.querySubjectClassesBySourceCode(inputObject, outputObject);
    }

    @ApiOperation(id = "querySubjectClassesById", value = "根据id查询科目与班级的关系信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "科目与班级的关系id", required = "required")})
    @RequestMapping("/post/SubjectClassesController/querySubjectClassesById")
    public void querySubjectClassesById(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "changeEnabled", value = "改变enabled状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "编码", required = "required"),
        @ApiImplicitParam(id = "enabled", name = "enabled", value = "是否允许加入班级", required = "required,num")})
    @RequestMapping("/post/SubjectClassesController/changeEnabled")
    public void changeEnabled(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.changeEnabled(inputObject, outputObject);
    }

    @ApiOperation(id = "changeQuit", value = "改变quit的状态", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "编码", required = "required"),
        @ApiImplicitParam(id = "quit", name = "quit", value = "是否允许退出课程", required = "required,num")})
    @RequestMapping("/post/SubjectClassesController/changeQuit")
    public void changeQuit(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.changeQuit(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTeacherMessage", value = "获取创建老师信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "require")})
    @RequestMapping("/post/SubjectClassesController/queryTeacherMessage")
    public void queryTeacherMessage(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.queryTeacherMessage(inputObject, outputObject);
    }

    @ApiOperation(id = "querySubjectClassesInfo", value = "获取课程申报数据", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "科目表与班级表关系id", required = "require")})
    @RequestMapping("/post/SubjectClassesController/querySubjectClassesInfo")
    public void querySubjectClassesInfo(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.querySubjectClassesInfo(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStudentAnalysis", value = "获取学生分析", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "科目表与班级表关系id", required = "require")})
    @RequestMapping("/post/SubjectClassesController/queryStudentAnalysis")
    public void queryStudentAnalysis(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.queryStudentAnalysis(inputObject, outputObject);
    }

    @ApiOperation(id = "queryOverallAnalysis", value = "获取整体分析", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "科目表与班级表关系id", required = "require")})
    @RequestMapping("/post/SubjectClassesController/queryOverallAnalysis")
    public void queryOverallAnalysis(InputObject inputObject, OutputObject outputObject) {
        subjectClassesService.queryOverallAnalysis(inputObject, outputObject);
    }

}
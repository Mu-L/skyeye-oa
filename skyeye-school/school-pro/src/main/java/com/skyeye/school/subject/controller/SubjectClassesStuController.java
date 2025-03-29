/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SubjectClassesStuController
 * @Description: 科目表与班级表关系下的学生信息控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "科目表与班级表关系下的学生信息", tags = "科目表与班级表关系下的学生信息", modelName = "科目管理")
public class SubjectClassesStuController {

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    /**
     * 加入课程班级
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "joinSubjectClasses", value = "加入课程班级", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubjectClassesStu.class)
    @RequestMapping("/post/SubjectClassesStuController/joinSubjectClasses")
    public void joinSubjectClasses(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.joinSubjectClasses(inputObject, outputObject);
    }

    /**
     * 退出课程班级
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "exitSubjectClasses", value = "退出课程班级", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SubjectClassesStuController/exitSubjectClasses")
    public void exitSubjectClasses(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllStudentSubjectClassesById", value = "查询所有学生信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id"),
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id")})
    @RequestMapping("/post/SubjectClassesStuController/queryAllStudentSubjectClassesById")
    public void queryAllStudentSubjectClassesById(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.queryAllStudentById(inputObject, outputObject);
    }

    /**
     * 根据科目表与班级表关系id和学号查询单人学生信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryStudentSubjectClassesBySubClassLinkIdAndStuNo", value = "根据科目表与班级表关系id和学号查询单人学生信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required"),
        @ApiImplicitParam(id = "stuNo", name = "stuNo", value = "学号", required = "required")})
    @RequestMapping("/post/SubjectClassesStuController/queryStudentSubjectClassesBySubClassLinkIdAndStuNo")
    public void queryStudentSubjectClassesBySubClassLinkIdAndStuNo(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.queryStudentSubjectClassesBySubClassLinkIdAndStuNo(inputObject, outputObject);
    }

    /**
     * 根据学号和科目表与班级表关系Id查询科目表与班级表关系下的学生信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryIdBysubClassLinkIdAndstuNo", value = "根据学号和科目表与班级表关系Id查询科目表与班级表关系下的学生信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required"),
        @ApiImplicitParam(id = "stuNo", name = "stuNo", value = "学号", required = "required")})
    @RequestMapping("/post/SubjectClassesStuController/queryIdBysubClassLinkIdAndstuNo")
    public void queryIdBysubClassLinkIdAndstuNo(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.queryIdBysubClassLinkIdAndstuNo(inputObject, outputObject);
    }

    /**
     * 根据id查询科目与班级的关系信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteUserById", value = "根据id删除学生信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required"),
        @ApiImplicitParam(id = "stuNo", name = "stuNo", value = "学号", required = "required")})
    @RequestMapping("/post/SubjectClassesStuController/deleteUserById")
    public void deleteUserById(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.deleteUserById(inputObject, outputObject);
    }

    /**
     * 学生奖励学生星星
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "updateRewardNumberById", value = "学生奖励学生星星", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "stuNo", name = "stuNo", value = "学号", required = "required"),
        @ApiImplicitParam(id = "reward", name = "reward", value = "奖励星星", required = "required"),
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "班级与科目关系id", required = "required")})
    @RequestMapping("/post/SubjectClassesStuController/updateRewardNumberById")
    public void updateRewardNumberById(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.updateRewardNumberById(inputObject, outputObject);
    }

    /**
     * 根据科目表与班级表关系id查询学生奖励星星列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectRewardList", value = "根据班级id查询学生奖励星星列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required")})
    @RequestMapping("/post/SubjectClassesStuController/selectRewardList")
    public void selectRewardList(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.selectRewardList(inputObject, outputObject);
    }

    /**
     * 根据科目表与班级表关系id查询学生分组
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectStudentList", value = "根据科目表与班级表关系id新增学生分组", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required"),
        @ApiImplicitParam(id = "groupCount", name = "groupCount", value = "分组数量", required = "required")})
    @RequestMapping("/post/SubjectClassesStuController/selectStudentList")
    public void selectStudentList(InputObject inputObject, OutputObject outputObject) {
        subjectClassesStuService.selectStudentList(inputObject, outputObject);
    }


}

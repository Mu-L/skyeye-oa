/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.student.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.student.entity.Student;
import com.skyeye.school.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: StudentController
 * @Description: 学生管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/9 9:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "学生管理", tags = "学生管理", modelName = "学生管理")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @ApiOperation(id = "queryStudentList", value = "获取学生信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StudentController/queryStudentList")
    public void queryStudentList(InputObject inputObject, OutputObject outputObject) {
        studentService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStudentListByNameOrNo", value = "根据学生姓名和学号获取在校学生信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StudentController/queryStudentListByNameOrNo")
    public void queryStudentListByNameOrNo(InputObject inputObject, OutputObject outputObject) {
        studentService.queryStudentListByNameOrNo(inputObject, outputObject);
    }

    @ApiOperation(id = "queryTeacherListByNameOrJobNumber", value = "根据姓名或工号获取老师信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/StudentController/queryTeacherListByNameOrJobNumber")
    public void queryTeacherListByNameOrJobNumber(InputObject inputObject, OutputObject outputObject) {
        studentService.queryTeacherListByNameOrJobNumber(inputObject, outputObject);
    }

    @ApiOperation(id = "writeStudent", value = "新增/修改学生信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Student.class)
    @RequestMapping("/post/StudentController/writeStudent")
    public void writeStudent(InputObject inputObject, OutputObject outputObject) {
        studentService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStudentDetailById", value = "获取学生信息详情", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/StudentController/queryStudentDetailById")
    public void queryStudentDetailById(InputObject inputObject, OutputObject outputObject) {
        studentService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "exportStudentModel", value = "导出学生模板", method = "Get", allUse = "1")
    @RequestMapping("/post/StudentController/exportStudentModel")
    public void exportStudentModel(InputObject inputObject, OutputObject outputObject) {
        studentService.exportStudentModel(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCurrentUserSubject", value = "获取当前登录人的所有的年制信息，年制下【所学科目】信息", method = "POST", allUse = "2")
    @RequestMapping("/post/StudentController/queryCurrentUserSubject")
    public void queryCurrentUserSubject(InputObject inputObject, OutputObject outputObject) {
        studentService.queryCurrentUserSubject(inputObject, outputObject);
    }

    @ApiOperation(id = "queryStudentByStudentNumbers", value = "根据学号列表查询出所有的学生信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "studentNumbers", name = "studentNumbers", value = "学号列表，逗号隔开", required = "required")})
    @RequestMapping("/post/StudentController/queryStudentByStudentNumbers")
    public void queryStudentByStudentNumbers(InputObject inputObject, OutputObject outputObject) {
        studentService.queryStudentByStudentNumbers(inputObject, outputObject);
    }

}

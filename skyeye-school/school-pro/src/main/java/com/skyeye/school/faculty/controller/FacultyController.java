/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.faculty.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.faculty.service.FacultyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: FacultyController
 * @Description: 院系管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "院系管理", tags = "院系管理", modelName = "院系管理")
public class FacultyController {

    @Autowired
    private FacultyService facultyService;

    @ApiOperation(id = "queryFacultyList", value = "获取院系信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/FacultyController/queryFacultyList")
    public void queryFacultyList(InputObject inputObject, OutputObject outputObject) {
        facultyService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeFaculty", value = "新增/编辑院系信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Faculty.class)
    @RequestMapping("/post/FacultyController/writeFaculty")
    public void writeFaculty(InputObject inputObject, OutputObject outputObject) {
        facultyService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteFacultyById", value = "根据ID删除院系信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/FacultyController/deleteFacultyById")
    public void deleteFacultyById(InputObject inputObject, OutputObject outputObject) {
        facultyService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFacultyByIds", value = "根据id批量查询院系信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id，多个用逗号隔开", required = "required")})
    @RequestMapping("/post/FacultyController/queryFacultyByIds")
    public void queryFacultyByIds(InputObject inputObject, OutputObject outputObject) {
        facultyService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "queryFacultyListBySchoolId", value = "根据学校id获取院系列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "schoolId", name = "schoolId", value = "学校id")})
    @RequestMapping("/post/FacultyController/queryFacultyListBySchoolId")
    public void queryFacultyListBySchoolId(InputObject inputObject, OutputObject outputObject) {
        facultyService.queryFacultyListBySchoolId(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.service.SchoolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SchoolController
 * @Description: 学校管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/6 21:13
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "学校管理", tags = "学校管理", modelName = "学校管理")
public class SchoolController {

    @Autowired
    private SchoolService schoolService;

    /**
     * 获取学校信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySchoolList", value = "获取学校信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SchoolController/querySchoolList")
    public void querySchoolList(InputObject inputObject, OutputObject outputObject) {
        schoolService.queryPageList(inputObject, outputObject);
    }

    /**
     * 添加或修改学校信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeSchool", value = "新增/编辑学校信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = School.class)
    @RequestMapping("/post/SchoolController/writeSchool")
    public void writeSchool(InputObject inputObject, OutputObject outputObject) {
        schoolService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除学校信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteSchoolById", value = "根据ID删除学校信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SchoolController/deleteSchoolById")
    public void deleteSchoolById(InputObject inputObject, OutputObject outputObject) {
        schoolService.deleteById(inputObject, outputObject);
    }

    /**
     * 获取所有学校列表展示为下拉选择框
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryAllSchoolList", value = "获取所有学校列表展示为下拉选择框", method = "GET", allUse = "2")
    @RequestMapping("/post/SchoolController/queryAllSchoolList")
    public void queryAllSchoolList(InputObject inputObject, OutputObject outputObject) {
        schoolService.queryAllSchoolList(inputObject, outputObject);
    }

    /**
     * 位置覆盖
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "coverBackground", value = "学校背景图位置覆盖", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = School.class)
    @RequestMapping("/post/SchoolController/coverBackground")
    public void coverBackground(InputObject inputObject, OutputObject outputObject) {
        schoolService.updateEntity(inputObject, outputObject);
    }

}

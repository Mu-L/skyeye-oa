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
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.service.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SubjectController
 * @Description: 科目管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "科目管理", tags = "科目管理", modelName = "科目管理")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    /**
     * 获取科目列表--管理端使用
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySubjectList", value = "获取科目列表--管理端使用", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SubjectController/querySubjectList")
    public void querySubjectList(InputObject inputObject, OutputObject outputObject) {
        subjectService.queryPageList(inputObject, outputObject);
    }

    /**
     * 添加或修改科目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeSubject", value = "新增/编辑科目信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Subject.class)
    @RequestMapping("/post/SubjectController/writeSubject")
    public void writeSubject(InputObject inputObject, OutputObject outputObject) {
        subjectService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除科目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteSubjectById", value = "根据ID删除科目信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/SubjectController/deleteSubjectById")
    public void deleteSubjectById(InputObject inputObject, OutputObject outputObject) {
        subjectService.deleteById(inputObject, outputObject);
    }

    /**
     * 获取我所加入/创建的课程信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySubjectListByUserId", value = "获取我所加入/创建的课程信息", method = "GET", allUse = "2")
    @RequestMapping("/post/SubjectController/querySubjectListByUserId")
    public void querySubjectListByUserId(InputObject inputObject, OutputObject outputObject) {
        subjectService.querySubjectListByUserId(inputObject, outputObject);
    }

}
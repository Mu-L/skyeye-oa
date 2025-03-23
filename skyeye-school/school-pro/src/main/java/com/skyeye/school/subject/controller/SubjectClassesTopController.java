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
import com.skyeye.school.subject.entity.SubjectClassesTop;
import com.skyeye.school.subject.service.SubjectClassesTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SubjectClassesTopController
 * @Description: 学生科目置顶表控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/23 20:22
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "学生科目置顶", tags = "学生科目置顶", modelName = "科目管理")
public class SubjectClassesTopController {

    @Autowired
    private SubjectClassesTopService subjectClassesTopService;

    @ApiOperation(id = "querySubjectClassesTopList", value = "获取我的置顶科目列表", method = "POST", allUse = "2")
    @RequestMapping("/post/SubjectClassesTopController/querySubjectClassesTopList")
    public void querySubjectClassesTopList(InputObject inputObject, OutputObject outputObject) {
        subjectClassesTopService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "createSubjectClassesTop", value = "置顶我的科目", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = SubjectClassesTop.class)
    @RequestMapping("/post/SubjectClassesTopController/createSubjectClassesTop")
    public void createSubjectClassesTop(InputObject inputObject, OutputObject outputObject) {
        subjectClassesTopService.createEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteSubjectClassesTop", value = "取消置顶我的科目", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目表id"),
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id")})
    @RequestMapping("/post/SubjectClassesController/deleteSubjectClassesTop")
    public void deleteSubjectClassesTop(InputObject inputObject, OutputObject outputObject) {
        subjectClassesTopService.deleteSubjectClassesTop(inputObject, outputObject);
    }

}

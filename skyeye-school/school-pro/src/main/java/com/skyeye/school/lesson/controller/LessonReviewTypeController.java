package com.skyeye.school.lesson.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lesson.entity.LessonReviewModel;
import com.skyeye.school.lesson.entity.LessonReviewType;
import com.skyeye.school.lesson.service.LessonReviewModelService;
import com.skyeye.school.lesson.service.LessonReviewTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "质评角色听课量管理", tags = "质评角色听课量管理", modelName = "质评角色听课量管理")
public class LessonReviewTypeController {

    @Autowired
    private LessonReviewTypeService lessonReviewTypeService;

    @ApiOperation(id = "writeLessonReviewType", value = "新增/编辑质评角色听课量", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LessonReviewType.class)
    @RequestMapping("/post/LessonReviewTypeController/writeLessonReviewType")
    public void writeLessonReviewType(InputObject inputObject, OutputObject outputObject) {
        lessonReviewTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLessonReviewTypeList", value = "获取质评角色听课量管理信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LessonReviewTypeController/queryLessonReviewTypeList")
    public void queryLessonReviewTypeList(InputObject inputObject, OutputObject outputObject) {
        lessonReviewTypeService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLessonReviewTypeById", value = "根据id查询质评角色听课量管理", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LessonReviewTypeController/queryLessonReviewTypeById")
    public void queryLessonReviewTypeById(InputObject inputObject, OutputObject outputObject) {
        lessonReviewTypeService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLessonReviewTypeById", value = "根据ID删除质评角色听课量管理", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LessonReviewTypeController/deleteLessonReviewTypeById")
    public void deleteLessonReviewTypeById(InputObject inputObject, OutputObject outputObject) {
        lessonReviewTypeService.deleteById(inputObject, outputObject);
    }
}

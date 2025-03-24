/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.courseware.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.courseware.entity.Courseware;
import com.skyeye.school.courseware.service.CoursewareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CoursewareController
 * @Description: 互动课件控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 9:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "互动课件", tags = "互动课件", modelName = "互动课件")
public class CoursewareController {

    @Autowired
    private CoursewareService coursewareService;

    @ApiOperation(id = "writeCourseware", value = "新增/编辑互动课件信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = Courseware.class)
    @RequestMapping("/post/CoursewareController/writeCourseware")
    public void writeCourseware(InputObject inputObject, OutputObject outputObject) {
        coursewareService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCoursewareById", value = "根据id查询互动课件信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CoursewareController/queryCoursewareById")
    public void queryCoursewareById(InputObject inputObject, OutputObject outputObject) {
        coursewareService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCoursewareById", value = "根据ID删除互动课件信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CoursewareController/deleteCoursewareById")
    public void deleteCoursewareById(InputObject inputObject, OutputObject outputObject) {
        coursewareService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCoursewareListBySubjectId", value = "根据科目表id获取互动课件列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目表id", required = "required")})
    @RequestMapping("/post/CoursewareController/queryCoursewareListBySubjectId")
    public void queryCoursewareListBySubjectId(InputObject inputObject, OutputObject outputObject) {
        coursewareService.queryCoursewareListBySubjectId(inputObject, outputObject);
    }

}

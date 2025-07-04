package com.skyeye.school.lectures.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: LessonReviewModelController
 * @Description: 听评课模型管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */


@RestController
@Api(value = "质评-听课记录表", tags = "质评-听课记录表", modelName = "质评-听课记录表")
public class LecturesAttenanceRecoredController {
    @Autowired
    private LecturesAttenanceRecoredService lecturesAttenanceRecoredService;

    @ApiOperation(id = "writeLecturesAttenanceRecored", value = "新增/编辑质评-听课记录表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LecturesAttenanceRecored.class)
    @RequestMapping("/post/LecturesAttenanceRecoredController/writeLecturesAttenanceRecored")
    public void writeLecturesAttenanceRecored(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLecturesAttenanceRecoredList", value = "获取质评-听课记录表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LecturesAttenanceRecoredController/queryLecturesAttenanceRecoredList")
    public void queryLecturesAttenanceRecoredList(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredService.queryPageList(inputObject, outputObject);

    }

    @ApiOperation(id = "queryLecturesAttenanceRecoredById", value = "根据id查询质评-听课记录表信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LecturesAttenanceRecoredController/queryLessonReviewModelById")
    public void queryLessonReviewModelById(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLecturesAttenanceRecoredById", value = "根据ID删除质评-听课记录表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LecturesAttenanceRecoredController/deleteLecturesAttenanceRecoredById")
    public void deleteLecturesAttenanceRecoredById(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredService.deleteById(inputObject, outputObject);
    }


}

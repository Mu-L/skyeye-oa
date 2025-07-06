package com.skyeye.school.lectures.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;
import com.skyeye.school.lectures.service.LecturesAttenanceRecoredChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * @ClassName: LessonReviewModelController
 * @Description: 授课成绩表控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 14:55
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的F
 */


@RestController
@Api(value = "授课成绩表", tags = "授课成绩表", modelName = "授课成绩表")
public class LecturesAttenanceRecoredChildController {
    @Autowired
    private LecturesAttenanceRecoredChildService lecturesAttenanceRecoredChildService;

    @ApiOperation(id = "writeLecturesAttenanceRecoredChild", value = "新增/编辑授课成绩表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LecturesAttenanceRecoredChild.class)
    @RequestMapping("/post/LecturesAttenanceRecoredChildCotroller/writeLecturesAttenanceRecoredChild")
    public void writeLecturesAttenanceRecoredChild(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredChildService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLecturesAttenanceRecoredChildList", value = "获取授课成绩表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/LecturesAttenanceRecoredChildCotroller/queryLecturesAttenanceRecoredChildList")
    public void queryLecturesAttenanceRecoredChildList(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredChildService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLecturesAttenanceRecoredChildById", value = "根据id查询授课成绩表信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LecturesAttenanceRecoredChildCotroller/queryLecturesAttenanceRecoredChildById")
    public void queryLecturesAttenanceRecoredChildById(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredChildService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLecturesAttenanceRecoredChildById", value = "根据ID删除授课成绩表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LecturesAttenanceRecoredChildCotroller/deleteLecturesAttenanceRecoredChildById")
    public void deleteLecturesAttenanceRecoredChildById(InputObject inputObject, OutputObject outputObject) {
        lecturesAttenanceRecoredChildService.deleteById(inputObject, outputObject);
    }

}
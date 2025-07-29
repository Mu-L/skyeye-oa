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
@Api(value = "听评课模型管理", tags = "听评课模型管理", modelName = "听评课模型管理")
public class LessonReviewModelController {

    @Autowired
    private LessonReviewModelService lessonReviewModelService;

    @ApiOperation(id = "writeLessonReviewModel", value = "新增/编辑听评课模型", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = LessonReviewModel.class)
    @RequestMapping("/post/LessonReviewModelController/writeLessonReviewModel")
    public void writeLessonReviewModel(InputObject inputObject, OutputObject outputObject) {
        lessonReviewModelService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLessonReviewModel", value = "获取听评课模型信息", method = "POST", allUse = "2")
    @RequestMapping("/post/LessonReviewModelController/queryLessonReviewModel")
    public void queryLessonReviewModel(InputObject inputObject, OutputObject outputObject) {
        lessonReviewModelService.queryLessonReviewModel(inputObject, outputObject);
    }

    @ApiOperation(id = "queryLessonReviewModelById", value = "根据id查询听评课模型信息", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LessonReviewModelController/queryLessonReviewModelById")
    public void queryLessonReviewModelById(InputObject inputObject, OutputObject outputObject) {
        lessonReviewModelService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteLessonReviewModelById", value = "根据id删除听评课模型信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/LessonReviewModelController/deleteLessonReviewModelById")
    public void deleteLessonReviewModelById(InputObject inputObject, OutputObject outputObject) {
        lessonReviewModelService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "publishReviewModelVersionById", value = "根据id发布听评课模型版本号", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "方案id", required = "required")})
    @RequestMapping("/post/LessonReviewModelController/publishReviewModelVersionById")
    public void publishBomVersionById(InputObject inputObject, OutputObject outputObject) {
        lessonReviewModelService.publishVersionById(inputObject, outputObject);
    }

}

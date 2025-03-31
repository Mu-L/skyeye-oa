/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.contorller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.score.service.ScoreTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ScoreTypeController
 * @Description: 成绩类型管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/26 12:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "成绩类型管理", tags = "成绩类型管理", modelName = "成绩类型管理")
public class ScoreTypeController {

    @Autowired
    private ScoreTypeService scoreTypeService;

    /**
     * 新增成绩类型信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeScoreTypeList", value = "新增成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ScoreType.class)
    @RequestMapping("/post/ScoreTypeController/writeScoreTypeList")
    public void writeScoreTypeList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取同表成绩类型信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "querySameTableDateList", value = "获取同表成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "classId", name = "classId", value = "班级id", required = "required"),
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required")})
    @RequestMapping("/post/ScoreTypeController/querySameTableDateList")
    public void querySameTableDateList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.querySameTableDateList(inputObject, outputObject);
    }

    /**
     * 获取不同表成绩类型信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDifferentTableDateList", value = "获取不同表成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "classId", name = "classId", value = "班级id", required = "required"),
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required")})
    @RequestMapping("/post/ScoreTypeController/queryDifferentTableDateList")
    public void queryDifferentTableDateList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.queryDifferentTableDateList(inputObject, outputObject);
    }

    /**
     * 根据id删除成绩类型信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteScoreTypeById", value = "根据id删除成绩类型信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/deleteScoreTypeById")
    public void deleteScoreTypeById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeService.deleteById(inputObject, outputObject);
    }
}

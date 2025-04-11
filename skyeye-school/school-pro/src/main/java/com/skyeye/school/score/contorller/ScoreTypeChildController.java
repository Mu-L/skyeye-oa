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
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScoreTypeChildService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ScoreTypeChildController
 * @Description: 成绩类型子表管理
 * @author: skyeye云系列--卫志强
 * @date: 2022/5/26 12:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "成绩类型子表管理", tags = "成绩类型子表管理", modelName = "成绩类型子表管理")
public class ScoreTypeChildController {

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @ApiOperation(id = "writeScoreTypeChild", value = "新增/编辑成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ScoreTypeChild.class)
    @RequestMapping("/post/ScoreTypeChildController/writeScoreTypeChild")
    public void writeScoreTypeChild(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "selectScoreTypeById", value = "根据id查询成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/selectScoreTypeById")
    public void selectScoreTypeById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteScoreTypeChildById", value = "根据id删除成绩类型子表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/deleteScoreTypeChildById")
    public void deleteScoreTypeChildById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryScoreTypeChildFirstList", value = "获取一级成绩类型子表列表", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required"),
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/queryScoreTypeChildFirstList")
    public void queryScoreTypeChildFirstList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.queryScoreTypeChildFirstList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryScoreTypeChildSecondList", value = "获取二级成绩类型子表列表和成绩", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "subjectId", name = "subjectId", value = "科目id", required = "required"),
        @ApiImplicitParam(id = "subClassLinkId", name = "subClassLinkId", value = "科目表与班级表关系id", required = "required"),
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父节点id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/queryScoreTypeChildSecondList")
    public void queryScoreTypeChildSecondList(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.queryScoreTypeChildSecondList(inputObject, outputObject);
    }
}

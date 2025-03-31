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

    /**
     * 新增/编辑成绩类型子表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeScoreTypeChild", value = "新增/编辑成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ScoreTypeChild.class)
    @RequestMapping("/post/ScoreTypeChildController/writeScoreTypeChild")
    public void writeScoreTypeChild(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 根据id查询成绩类型子表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectScoreTypeById", value = "根据id查询成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/selectScoreTypeById")
    public void selectScoreTypeById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.selectById(inputObject, outputObject);
    }

    /**
     * 根据id删除成绩类型子表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteScoreTypeChildById", value = "根据id删除成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/deleteScoreTypeChildById")
    public void deleteScoreTypeChildById(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.deleteById(inputObject, outputObject);
    }

    /**
     * 关联或取消关联成绩类型子表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "boundDataOrNot", value = "关联或取消关联成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "parentId", name = "parentId", value = "父级id(平时成绩的主键id)"),
        @ApiImplicitParam(id = "id", name = "id", value = "子数据id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/boundDataOrNot")
    public void boundDataOrNOt(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.boundDataOrNot(inputObject, outputObject);
    }

    /**
     * 修改成绩类型子表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeProportion", value = "修改成绩类型子表信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "proportion", name = "proportion", value = "占比(80即占比80%)", required = "required"),
        @ApiImplicitParam(id = "id", name = "id", value = "子数据id", required = "required")})
    @RequestMapping("/post/ScoreTypeChildController/changeProportion")
    public void changeProportion(InputObject inputObject, OutputObject outputObject) {
        scoreTypeChildService.changeProportion(inputObject, outputObject);
    }
}

package com.skyeye.exam.examanscore.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanscore.entity.ExamAnScore;
import com.skyeye.exam.examanscore.service.ExamAnScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "评分题保存表管理", tags = "评分题保存表管理", modelName = "评分题保存表管理")
public class ExamAnScoreController {

    @Autowired
    private ExamAnScoreService examAnScoreService;

    /**
     * 添加或修改评分题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnScore", value = "新增/编辑评分题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnScore.class)
    @RequestMapping("/post/ExamAnScoreController/writeExamAnScore")
    public void writeExamAnScore(InputObject inputObject, OutputObject outputObject) {
        examAnScoreService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取评分题保存表信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnScoreList", value = "获取评分题保存表信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnScoreController/queryExamAnScoreList")
    public void queryExamAnScoreList(InputObject inputObject, OutputObject outputObject) {
        examAnScoreService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除评分题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnScoreById", value = "根据ID删除评分题保存表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnScoreController/deleteExamAnScoreById")
    public void deleteExamAnScoreById(InputObject inputObject, OutputObject outputObject) {
        examAnScoreService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取评分题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnScoreListById", value = "根据id获取评分题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnScoreController/queryExamAnScoreListById")
    public void queryExamAnScoreListById(InputObject inputObject, OutputObject outputObject) {
        examAnScoreService.queryExamAnScoreListById(inputObject, outputObject);
    }
}

package com.skyeye.exam.examquscore.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examquscore.service.ExamQuScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamQuScoreController
 * @Description: 评分题行选项管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "评分题行选项管理", tags = "评分题行选项管理", modelName = "评分题行选项管理")
public class ExamQuScoreController {

    @Autowired
    private ExamQuScoreService examQuScoreService;

    /**
     * 分页获取评分题行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamQuScoreList", value = "分页获取评分题行选项表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamQuScoreController/queryExamQuScoreList")
    public void queryExamQuScoreList(InputObject inputObject, OutputObject outputObject) {
        examQuScoreService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除评分题行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamQuScoreById", value = "根据ID物理删除评分题行选项表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamQuScoreController/deleteExamQuScoreById")
    public void deleteExamQuScoreById(InputObject inputObject, OutputObject outputObject) {
        examQuScoreService.deleteById(inputObject, outputObject);
    }

    /**
     * 逻辑删除评分题行选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeVisibility", value = "逻辑删除评分题行选项表信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamQuScoreController/changeVisibility")
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        examQuScoreService.changeVisibility(inputObject, outputObject);
    }


}

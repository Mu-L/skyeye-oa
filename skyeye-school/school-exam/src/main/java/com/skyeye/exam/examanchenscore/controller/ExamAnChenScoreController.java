package com.skyeye.exam.examanchenscore.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
import com.skyeye.exam.examanchenscore.entity.ExamAnChenScore;
import com.skyeye.exam.examanchenscore.service.ExamAnChenScoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnChenScoreController
 * @Description: 答卷 矩阵多选题控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 矩阵多选题", tags = "答卷 矩阵多选题", modelName = "答卷 矩阵多选题")
public class ExamAnChenScoreController {
    @Autowired
    private ExamAnChenScoreService examAnChenScoreService;

    /**
     * 新增/编辑矩阵多选题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnChenScore", value = "新增/编辑矩阵多选题保存表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnChenScore.class)
    @RequestMapping("/post/ExamAnChenScoreController/writeExamAnChenScore")
    public void writeExamAnChenScore(InputObject inputObject, OutputObject outputObject) {
        examAnChenScoreService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenScoreList", value = "获取矩阵多选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnChenScoreController/queryExamAnChenScoreList")
    public void queryExamAnChenScoreList(InputObject inputObject, OutputObject outputObject) {
        examAnChenScoreService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnChenScoreById", value = "删除矩阵多选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenScoreController/deleteExamAnChenScoreById")
    public void deleteExamAnChenScoreById(InputObject inputObject, OutputObject outputObject) {
        examAnChenScoreService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵多选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenScoreListById", value = "根据id获取矩阵多选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenScoreController/queryExamAnChenScoreListById")
    public void queryExamAnChenScoreListById(InputObject inputObject, OutputObject outputObject) {
        examAnChenScoreService.queryExamAnChenScoreListById(inputObject, outputObject);
    }
}

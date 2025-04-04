package com.skyeye.eve.myquestionbank.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.myquestionbank.entity.MatrixMultipleChoiceQuestion;
import com.skyeye.eve.myquestionbank.service.MatrixMultipleChoiceQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MatrixMultipleChoiceController
 * @Description:矩阵多选题管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/5 15:17
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "矩阵多选题管理", tags = "矩阵多选题信息", modelName = "矩阵多选题信息")
public class MatrixMultipleChoiceQuestionController{

    @Autowired
    private MatrixMultipleChoiceQuestionService matrixMultipleChoiceQuestionService;

    /**
     * 新增矩阵多选题
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeMatrixMultipleChoiceQuestion", value = "新增/编辑矩阵单选题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MatrixMultipleChoiceQuestion.class)
    @RequestMapping("/post/MatrixMultipleChoiceQuestionController/addMatrixMultipleChoiceQuestionMation")
    public void addMatrixMultipleChoiceQuestionMation(InputObject inputObject, OutputObject outputObject) {
        matrixMultipleChoiceQuestionService.addMatrixMultiplechoiceQuestionMation(inputObject, outputObject);
    }

    /**
     * 获取矩阵多选题信息
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMatrixMultiplechoiceQuestion", value = "获取矩阵多选题信息", method = "POST", allUse = "0")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id")})
    @RequestMapping("/post/MatrixMultipleChoiceQuestionController/queryMatrixMultipleChoiceQuestion")
    public void queryMatrixMultipleChoiceQuestion(InputObject inputObject, OutputObject outputObject) {
        matrixMultipleChoiceQuestionService.queryMatrixMultiplechoiceQuestionMationToEditById(inputObject, outputObject);
    }

}



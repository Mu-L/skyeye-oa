package com.skyeye.eve.myquestionbank.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.myquestionbank.entity.MatrixMultipleChoice;
import com.skyeye.eve.myquestionbank.service.MatrixMultipleChoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: MatrixMultipleChoiceController
 * @Description:矩阵单选题管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/5 15:17
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "矩阵单选题管理", tags = "矩阵单选题信息", modelName = "矩阵单选题信息")
public class MatrixMultipleChoiceController {

    @Autowired
    private MatrixMultipleChoiceService matrixMultipleChoiceService;

    /**
     * 新增矩阵单选题
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeMatrixMultipleChoice", value = "新增/编辑矩阵单选题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = MatrixMultipleChoice.class)
    @RequestMapping("/post/MatrixMultipleChoiceController/addMatrixMultipleChoiceMation")
    public void addMatrixMultipleChoiceMation(InputObject inputObject, OutputObject outputObject) {
        matrixMultipleChoiceService.addMatrixMultipleChoiceMation(inputObject, outputObject);
    }

    /**
     * 编辑矩阵单选题回显
     *
     * @param inputObject 入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMatrixMultipleChoice", value = "新增/编辑矩阵单选题信息时回显", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id")})
    @RequestMapping("/post/MatrixMultipleChoiceController/queryMatrixMultipleChoice")
    public void queryMatrixMultipleChoice(InputObject inputObject, OutputObject outputObject) {
        matrixMultipleChoiceService.queryMatrixMultipleChoiceMationToEditById(inputObject, outputObject);
    }



}
//继承
//控制层方法改写
//主键id
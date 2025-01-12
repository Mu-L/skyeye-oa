package com.skyeye.exam.examquestionlogic.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examquestionlogic.entity.ExamQuestionLogic;
import com.skyeye.exam.examquestionlogic.service.ExamQuestionLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamQuestionLogicController
 * @Description: 题目逻辑设置管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "题目逻辑设置管理", tags = "题目逻辑设置管理", modelName = "题目逻辑设置管理")
public class ExamQuestionLogicController {

    @Autowired
    private ExamQuestionLogicService examQuestionLogicService;

//    /**
//     * 新增/编辑题目逻辑设置管理
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @ApiOperation(id = "writeExamQuestionLogic", value = "新增/编辑题目逻辑设置管理", method = "POST", allUse = "1")
//    @ApiImplicitParams(classBean = ExamQuestionLogic.class)
//    @RequestMapping("/post/ExamAnChenCheckboxController/writeExamQuestionLogic")
//    public void writeExamQuestionLogic(InputObject inputObject, OutputObject outputObject) {
//        examQuestionLogicService.saveOrUpdateEntity(inputObject, outputObject);
//    }
}

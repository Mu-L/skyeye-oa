/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.SchoolQuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: SchoolQuestionBankController
 * @Description: 题库管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/7 14:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "题库管理", tags = "题库管理", modelName = "题库管理")
public class SchoolQuestionBankController {

    @Autowired
    private SchoolQuestionBankService schoolQuestionBankService;

    /**
     * 获取我的题库列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationList")
    public void querySchoolQuestionBankMationList(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.querySchoolQuestionBankMationList(inputObject, outputObject);
    }

    /**
     * 新增单选题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/addQuRadioMation")
    public void addQuRadioMation(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.addQuRadioMation(inputObject, outputObject);
    }

    /**
     * 删除我的题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/deleteSchoolQuestionBankMationById")
    public void deleteSchoolQuestionBankMationById(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.deleteSchoolQuestionBankMationById(inputObject, outputObject);
    }

    /**
     * 编辑单选题时回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/queryQuRadioMationToEditById")
    public void queryQuRadioMationToEditById(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.queryQuRadioMationToEditById(inputObject, outputObject);
    }

    /**
     * 新增多选题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/addQuCheckBoxMation")
    public void addQuCheckBoxMation(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.addQuCheckBoxMation(inputObject, outputObject);
    }

    /**
     * 编辑多选题时回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/queryQuCheckBoxMationToEditById")
    public void queryQuCheckBoxMationToEditById(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.queryQuCheckBoxMationToEditById(inputObject, outputObject);
    }

    /**
     * 新增填空题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/addQuFillblankMation")
    public void addQuFillblankMation(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.addQuFillblankMation(inputObject, outputObject);
    }

    /**
     * 编辑填空题时回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/queryQuFillblankMationToEditById")
    public void queryQuFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.queryQuFillblankMationToEditById(inputObject, outputObject);
    }

    /**
     * 新增评分题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/addQuScoreMation")
    public void addQuScoreMation(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.addQuScoreMation(inputObject, outputObject);
    }

    /**
     * 编辑评分题时回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/queryQuScoreMationToEditById")
    public void queryQuScoreMationToEditById(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.queryQuScoreMationToEditById(inputObject, outputObject);
    }

    /**
     * 新增排序题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/addQuOrderbyMation")
    public void addQuOrderbyMation(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.addQuOrderbyMation(inputObject, outputObject);
    }

    /**
     * 编辑排序题时回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/queryQuOrderbyMationToEditById")
    public void queryQuOrderbyMationToEditById(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.queryQuOrderbyMationToEditById(inputObject, outputObject);
    }

    /**
     * 新增多项填空题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/addQuMultiFillblankAddMation")
    public void addQuMultiFillblankAddMation(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.addQuMultiFillblankAddMation(inputObject, outputObject);
    }

    /**
     * 编辑多项填空题时回显
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/queryQuMultiFillblankMationToEditById")
    public void queryQuMultiFillblankMationToEditById(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.queryQuMultiFillblankMationToEditById(inputObject, outputObject);
    }

//    /**
//     * 新增矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题
//     *
//     * @param inputObject 入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @RequestMapping("/post/SchoolQuestionBankController/addQuChenMation")
//    public void addQuChenMation(InputObject inputObject, OutputObject outputObject) {
//        schoolQuestionBankService.addQuChenMation(inputObject, outputObject);
//    }
// 
//    /**
//     * 编辑矩阵单选题,矩阵多选题,矩阵评分题,矩阵填空题时回显
//     *
//     * @param inputObject 入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @RequestMapping("/post/SchoolQuestionBankController/queryQuChenMationToEditById")
//    public void queryQuChenMationToEditById(InputObject inputObject, OutputObject outputObject) {
//        schoolQuestionBankService.queryQuChenMationToEditById(inputObject, outputObject);
//    }

    /**
     * 获取题库列表(包含我的私有题库以及所有公开题库)供试卷选择
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationListToChoose")
    public void querySchoolQuestionBankMationListToChoose(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.querySchoolQuestionBankMationListToChoose(inputObject, outputObject);
    }

    /**
     * 根据试题id串获取试题详细信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationListByIds")
    public void querySchoolQuestionBankMationListByIds(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.querySchoolQuestionBankMationListByIds(inputObject, outputObject);
    }

    /**
     * 获取所有公共题库以及个人私有题库列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/SchoolQuestionBankController/querySchoolQuestionBankMationAllList")
    public void querySchoolQuestionBankMationAllList(InputObject inputObject, OutputObject outputObject) {
        schoolQuestionBankService.querySchoolQuestionBankMationAllList(inputObject, outputObject);
    }

}

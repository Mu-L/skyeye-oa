package com.skyeye.exam.examanchencheckbox.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examanchencheckbox.entity.ExamAnChenCheckbox;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnChenCheckboxController
 * @Description: 答卷 矩阵多选题控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 矩阵多选题", tags = "答卷 矩阵多选题", modelName = "答卷 矩阵多选题")
public class ExamAnChenCheckboxController {

    @Autowired
    private ExamAnChenCheckboxService examAnChenCheckboxService;

    /**
     * 新增/编辑矩阵多选题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnChenCheckbox", value = "新增/编辑矩阵多选题", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnChenCheckbox.class)
    @RequestMapping("/post/ExamAnChenCheckboxController/writeExamAnChenCheckbox")
    public void writeExamAnChenCheckbox(InputObject inputObject, OutputObject outputObject) {
        examAnChenCheckboxService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵多选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenCheckboxList", value = "获取矩阵多选题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnChenCheckboxController/queryExamAnChenCheckboxList")
    public void queryExamAnChenCheckboxList(InputObject inputObject, OutputObject outputObject) {
        examAnChenCheckboxService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵多选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnChenCheckboxById", value = "删除矩阵多选题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenCheckboxController/deleteExamAnChenCheckboxById")
    public void deleteExamAnChenCheckboxById(InputObject inputObject, OutputObject outputObject) {
        examAnChenCheckboxService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵多选题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenCheckboxListById", value = "根据id获取矩阵多选题列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenCheckboxController/queryExamAnChenCheckboxListById")
    public void queryExamAnChenCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        examAnChenCheckboxService.queryExamAnChenCheckboxListById(inputObject, outputObject);
    }
}

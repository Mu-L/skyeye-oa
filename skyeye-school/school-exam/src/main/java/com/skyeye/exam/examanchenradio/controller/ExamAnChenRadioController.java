package com.skyeye.exam.examanchenradio.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examanchenradio.entity.ExamAnChenRadio;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
/**
 * @ClassName: ExamAnChenRadioController
 * @Description: "答卷 矩阵单选题控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 矩阵单选题", tags = "答卷 矩阵单选题", modelName = "答卷 矩阵单选题")
public class ExamAnChenRadioController {
    @Autowired
    private ExamAnChenRadioService examAnChenRadioService;

    /**
     * 新增/编辑矩阵单选题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnChenRadio", value = "新增/编辑矩阵单选题", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ExamAnChenRadio.class)
    @RequestMapping("/post/ExamAnChenRadioController/writeExamAnChenRadio")
    public void writeExamAnChenRadio(InputObject inputObject, OutputObject outputObject) {
        examAnChenRadioService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵单选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenRadioList", value = "获取矩阵单选题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnChenRadioController/queryExamAnChenRadioList")
    public void queryExamAnChenRadioList(InputObject inputObject, OutputObject outputObject) {
        examAnChenRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵单选题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnChenRadioById", value = "删除矩阵单选题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenRadioController/deleteExamAnChenRadioById")
    public void deleteExamAnChenRadioById(InputObject inputObject, OutputObject outputObject) {
        examAnChenRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵单选题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenRadioListById", value = "根据id获取矩阵单选题列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenRadioController/queryExamAnChenRadioListById")
    public void queryExamAnChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        examAnChenRadioService.queryExamAnChenRadioListById(inputObject, outputObject);
    }
}

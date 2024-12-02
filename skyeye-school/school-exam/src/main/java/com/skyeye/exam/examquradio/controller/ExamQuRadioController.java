package com.skyeye.exam.examquradio.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examquradio.service.ExamQuRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamQuRadioController
 * @Description: 单选题选项表管理控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "单选题选项表管理", tags = "单选题选项表管理", modelName = "单选题选项表管理")
public class ExamQuRadioController {

    @Autowired
    private ExamQuRadioService examQuRadioService;

    /**
     * 分页获取单选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamQuRadioList", value = "分页获取单选题选项表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamQuRadioController/queryExamQuRadioList")
    public void queryExamQuRadioList(InputObject inputObject, OutputObject outputObject) {
        examQuRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除单选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamQuRadioById", value = "根据ID物理删除单选题选项表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamQuRadioController/deleteExamQuRadioById")
    public void deleteExamQuRadioById(InputObject inputObject, OutputObject outputObject) {
        examQuRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 逻辑删除单选题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "changeVisibility", value = "逻辑删除单选题选项表信息", method = "POST", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamQuRadioController/changeVisibility")
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        examQuRadioService.changeVisibility(inputObject, outputObject);
    }

}

package com.skyeye.exam.examanradio.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.ExamService;
import com.skyeye.exam.examanradio.entity.ExamAnRadio;
import com.skyeye.exam.examanradio.service.ExamAnRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "单选题保存表管理", tags = "单选题保存表管理", modelName = "单选题保存表管理")
public class ExamAnRadioController {

    @Autowired
    private ExamAnRadioService examAnRadioService;

    /**
     * 添加单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnRadio", value = "新增单选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnRadio.class)
    @RequestMapping("/post/ExamAnRadioController/writeExamAnRadio")
    public void writeExamAnRadio(InputObject inputObject, OutputObject outputObject) {
        examAnRadioService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取单选题保存表信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnRadioList", value = "获取单选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnRadioController/queryExamAnRadioList")
    public void queryExamAnRadioList(InputObject inputObject, OutputObject outputObject) {
        examAnRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnRadioById", value = "根据ID删除单选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnRadioController/deleteExamAnRadioById")
    public void deleteExamAnRadioById(InputObject inputObject, OutputObject outputObject) {
        examAnRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取单选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnRadioListById", value = "根据id获取单选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnRadioController/queryExamAnRadioListById")
    public void queryExamAnRadioListById(InputObject inputObject, OutputObject outputObject) {
        examAnRadioService.queryExamAnRadioListById(inputObject, outputObject);
    }
}

package com.skyeye.exam.examanyesno.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanyesno.entity.ExamAnYesno;
import com.skyeye.exam.examanyesno.service.ExamAnYesnoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "判断题保存表管理", tags = "判断题保存表管理", modelName = "判断题保存表管理")
public class ExamAnYesnoController {

    @Autowired
    private ExamAnYesnoService examAnYesnoService;

    /**
     * 添加或修改判断题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnYesno", value = "新增/编辑判断题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnYesno.class)
    @RequestMapping("/post/ExamAnYesnoController/writeExamAnYesno")
    public void writeExamAnYesno(InputObject inputObject, OutputObject outputObject) {
        examAnYesnoService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取判断题保存表信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnYesnoList", value = "获取判断题保存表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnYesnoController/queryExamAnYesnoList")
    public void queryExamAnYesnoList(InputObject inputObject, OutputObject outputObject) {
        examAnYesnoService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除判断题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnYesnoById", value = "根据ID删除单选题保存表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnYesnoController/deleteExamAnYesnoById")
    public void deleteExamAnYesnoById(InputObject inputObject, OutputObject outputObject) {
        examAnYesnoService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取判断题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnYesnoListById", value = "根据id获取单选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnYesnoController/queryExamAnYesnoListById")
    public void queryExamAnYesnoListById(InputObject inputObject, OutputObject outputObject) {
        examAnYesnoService.queryExamAnYesnoListById(inputObject, outputObject);
    }
}

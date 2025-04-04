package com.skyeye.exam.examancheckbox.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examananswer.entity.ExamAnAnswer;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examancheckbox.service.ExamAnCheckboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnCheckboxController
 * @Description: 答卷 多选题保存表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 多选题保存表", tags = "答卷 多选题保存表", modelName = "答卷 多选题保存表")
public class ExamAnCheckboxController {
    @Autowired
    private ExamAnCheckboxService examAnCheckboxService;

    /**
     * 新增/编辑多选题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnCheckbox", value = "新增/编辑多选题保存表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnCheckbox.class)
    @RequestMapping("/post/ExamAnCheckboxController/writeExamAnCheckbox")
    public void writeExamAnCheckbox(InputObject inputObject, OutputObject outputObject) {
        examAnCheckboxService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnCheckboxList", value = "获取多选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnCheckboxController/queryExamAnCheckboxList")
    public void queryExamAnCheckboxList(InputObject inputObject, OutputObject outputObject) {
        examAnCheckboxService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除多选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnCheckboxById", value = "删除多选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnCheckboxController/deleteExamAnCheckboxById")
    public void deleteExamAnCheckboxById(InputObject inputObject, OutputObject outputObject) {
        examAnCheckboxService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取多选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnCheckboxListById", value = "根据id获取多选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnCheckboxController/queryExamAnCheckboxListById")
    public void queryExamAnCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        examAnCheckboxService.queryExamAnCheckboxListById(inputObject, outputObject);
    }
}

package com.skyeye.exam.examananswer.controller;


import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examananswer.entity.ExamAnAnswer;
import com.skyeye.exam.examananswer.service.ExamAnAnswerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnAnswerController
 * @Description: 答卷/问答题保存表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷/问答题保存表", tags = "答卷/问答题保存表", modelName = "答卷/问答题保存表")
public class ExamAnAnswerController {

    @Autowired
    private ExamAnAnswerService examAnAnswerService;

    /**
     * 新增/编辑问答题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnAnswer", value = "新增/编辑问答题保存表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnAnswer.class)
    @RequestMapping("/post/ExamAnAnswerController/writeExamAnAnswer")
    public void writeExamAnAnswer(InputObject inputObject, OutputObject outputObject) {
        examAnAnswerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取问答题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnAnswerList", value = "获取问答题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnAnswerController/queryExamAnAnswerList")
    public void queryExamAnAnswerList(InputObject inputObject, OutputObject outputObject) {
        examAnAnswerService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除问答题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnAnswerById", value = "删除问答题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnAnswerController/deleteExamAnAnswerById")
    public void deleteExamAnAnswerById(InputObject inputObject, OutputObject outputObject) {
        examAnAnswerService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取问答题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnAnswerListById", value = "根据id获取问答题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnAnswerController/queryExamAnAnswerListById")
    public void queryExamAnAnswerListById(InputObject inputObject, OutputObject outputObject) {
        examAnAnswerService.queryExamAnAnswerListById(inputObject, outputObject);
    }

}

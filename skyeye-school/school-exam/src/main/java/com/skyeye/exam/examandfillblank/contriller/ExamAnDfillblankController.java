package com.skyeye.exam.examandfillblank.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examandfillblank.entity.ExamAnDfillblank;
import com.skyeye.exam.examandfillblank.service.ExamAnDfilllankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnDfillblankController
 * @Description: 答卷 多行填空题保存表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 多行填空题保存表", tags = "答卷 多行填空题保存表", modelName = "答卷 多行填空题保存表")
public class ExamAnDfillblankController {
    @Autowired
    private ExamAnDfilllankService examAnDfilllankService;

    /**
     * 新增/编辑多行填空题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnDfilllank", value = "新增/编辑多行填空题保存表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ExamAnDfillblank.class)
    @RequestMapping("/post/ExamAnDfillblankController/writeExamAnDfilllank")
    public void writeExamAnDfilllank(InputObject inputObject, OutputObject outputObject) {
        examAnDfilllankService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取多行填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnDfilllankList", value = "获取多行填空题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnDfillblankController/queryExamAnDfilllankList")
    public void queryExamAnDfilllankList(InputObject inputObject, OutputObject outputObject) {
        examAnDfilllankService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除多行填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnDfilllankById", value = "删除多行填空题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnDfillblankController/deleteExamAnDfilllankById")
    public void deleteExamAnDfilllankById(InputObject inputObject, OutputObject outputObject) {
        examAnDfilllankService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取多行填空题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnDfilllankById", value = "根据id获取多行填空题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnDfillblankController/queryExamAnDfilllankById")
    public void queryExamAnDfilllankById(InputObject inputObject, OutputObject outputObject) {
        examAnDfilllankService.queryExamAnDfilllankById(inputObject, outputObject);
    }
}

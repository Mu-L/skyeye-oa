package com.skyeye.exam.examanfillblank.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;
import com.skyeye.exam.examanfillblank.entity.ExamAnFillblank;
import com.skyeye.exam.examanfillblank.service.ExamAnFillblankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnFillblankController
 * @Description: 答卷 填空题保存表控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 填空题保存表", tags = "答卷 填空题保存表", modelName = "答卷 填空题保存表")
public class ExamAnFillblankController {
    @Autowired
    private ExamAnFillblankService examAnFillblankService;

    /**
     * 新增/编辑填空题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnFillblank", value = "新增/编辑填空题保存表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnFillblank.class)
    @RequestMapping("/post/ExamAnFillblankController/writeExamAnFillblank")
    public void writeExamAnFillblank(InputObject inputObject, OutputObject outputObject) {
        examAnFillblankService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnFillblankList", value = "获取填空题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnFillblankController/queryExamAnFillblankList")
    public void queryExamAnFillblankList(InputObject inputObject, OutputObject outputObject) {
        examAnFillblankService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除填空题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnFillblankById", value = "删除填空题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnFillblankController/deleteExamAnFillblankById")
    public void deleteExamAnFillblankById(InputObject inputObject, OutputObject outputObject) {
        examAnFillblankService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取填空题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnFillblankListById", value = "根据id获取填空题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnFillblankController/queryExamAnFillblankListById")
    public void queryExamAnFillblankListById(InputObject inputObject, OutputObject outputObject) {
        examAnFillblankService.queryExamAnFillblankListById(inputObject, outputObject);
    }
}

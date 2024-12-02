package com.skyeye.exam.examancompchenradio.contriller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanchenscore.entity.ExamAnChenScore;
import com.skyeye.exam.examancompchenradio.entity.ExamAnCompChenRadio;
import com.skyeye.exam.examancompchenradio.service.ExamAnCompChenRadioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnCompChenRadioController
 * @Description: 答卷 复合矩阵单选题服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 复合矩阵单选题", tags = "答卷 复合矩阵单选题", modelName = "答卷 复合矩阵单选题")
public class ExamAnCompChenRadioController {
    @Autowired
    private ExamAnCompChenRadioService examAnCompChenRadioService;

    /**
     * 新增/编辑复合矩阵单选题保存表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnCompChenRadio", value = "新增/编辑复合矩阵单选题保存表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ExamAnCompChenRadio.class)
    @RequestMapping("/post/ExamAnCompChenRadioController/writeExamAnCompChenRadio")
    public void writeExamAnCompChenRadio(InputObject inputObject, OutputObject outputObject) {
        examAnCompChenRadioService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取复合矩阵单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnCompChenRadioList", value = "获取复合矩阵单选题保存表信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnCompChenRadioController/queryExamAnCompChenRadioList")
    public void queryExamAnCompChenRadioList(InputObject inputObject, OutputObject outputObject) {
        examAnCompChenRadioService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除复合矩阵单选题保存表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnCompChenRadioById", value = "删除复合矩阵单选题保存表信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnCompChenRadioController/deleteExamAnCompChenRadioById")
    public void deleteExamAnCompChenRadioById(InputObject inputObject, OutputObject outputObject) {
        examAnCompChenRadioService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取复合矩阵单选题保存表列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnCompChenRadioListById", value = "根据id获取复合矩阵单选题保存表列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnCompChenRadioController/queryExamAnCompChenRadioListById")
    public void queryExamAnCompChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        examAnCompChenRadioService.queryExamAnCompChenRadioListById(inputObject, outputObject);
    }
}

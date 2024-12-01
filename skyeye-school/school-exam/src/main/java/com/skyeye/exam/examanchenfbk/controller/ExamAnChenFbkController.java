package com.skyeye.exam.examanchenfbk.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examancheckbox.entitiy.ExamAnCheckbox;
import com.skyeye.exam.examanchenfbk.entity.ExamAnChenFbk;
import com.skyeye.exam.examanchenfbk.service.ExamAnChenFbkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnChenFbkController
 * @Description: 答卷 矩阵填空题控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@RestController
@Api(value = "答卷 矩阵填空题", tags = "答卷 矩阵填空题", modelName = "答卷 矩阵填空题")
public class ExamAnChenFbkController {

    @Autowired
    private ExamAnChenFbkService examAnChenFbkService;

    /**
     * 新增/编辑矩阵填空题
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnChenFbk", value = "新增/编辑矩阵填空题", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = ExamAnChenFbk.class)
    @RequestMapping("/post/ExamAnChenFbkController/writeExamAnChenFbk")
    public void writeExamAnChenFbk(InputObject inputObject, OutputObject outputObject) {
        examAnChenFbkService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取矩阵填空题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenFbkList", value = "获取矩阵填空题信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnChenFbkController/queryExamAnChenFbkList")
    public void queryExamAnChenFbkList(InputObject inputObject, OutputObject outputObject) {
        examAnChenFbkService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除矩阵填空题信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnChenFbkById", value = "删除矩阵填空题信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenFbkController/deleteExamAnChenFbkById")
    public void deleteExamAnChenFbkById(InputObject inputObject, OutputObject outputObject) {
        examAnChenFbkService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取矩阵填空题列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnChenFbkListById", value = "根据id获取矩阵填空题列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnChenFbkController/queryExamAnChenFbkListById")
    public void queryExamAnChenFbkListById(InputObject inputObject, OutputObject outputObject) {
        examAnChenFbkService.queryExamAnChenFbkListById(inputObject, outputObject);
    }
}

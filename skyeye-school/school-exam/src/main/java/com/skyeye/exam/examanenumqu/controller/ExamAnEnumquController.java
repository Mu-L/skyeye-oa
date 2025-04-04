package com.skyeye.exam.examanenumqu.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examanenumqu.entity.ExamAnEnumqu;
import com.skyeye.exam.examanenumqu.service.ExamAnEnumquService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ExamAnEnumquController
 * @Description: 答卷 枚举题答案控制层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/16 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "答卷 枚举题答案", tags = "答卷 枚举题答案", modelName = "答卷 枚举题答案")
public class ExamAnEnumquController {

    @Autowired
    private ExamAnEnumquService examAnEnumquService;

    /**
     * 新增/编辑枚举题答案
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeExamAnEnumqu", value = "新增/编辑枚举题答案", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ExamAnEnumqu.class)
    @RequestMapping("/post/ExamAnEnumquController/writeExamAnEnumqu")
    public void writeExamAnEnumqu(InputObject inputObject, OutputObject outputObject) {
        examAnEnumquService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 获取枚举题答案信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnEnumquList", value = "获取枚举题答案信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamAnEnumquController/queryExamAnEnumquList")
    public void queryExamAnEnumquList(InputObject inputObject, OutputObject outputObject) {
        examAnEnumquService.queryPageList(inputObject, outputObject);
    }

    /**
     * 删除枚举题答案信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamAnEnumquById", value = "删除枚举题答案信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnEnumquController/deleteExamAnEnumquById")
    public void deleteExamAnEnumquById(InputObject inputObject, OutputObject outputObject) {
        examAnEnumquService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据id获取枚举题答案列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamAnEnumquListById", value = "根据id获取枚举题答案列表", method = "GET", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamAnEnumquController/queryExamAnEnumquListById")
    public void queryExamAnEnumquListById(InputObject inputObject, OutputObject outputObject) {
        examAnEnumquService.queryExamAnEnumquListById(inputObject, outputObject);
    }

}

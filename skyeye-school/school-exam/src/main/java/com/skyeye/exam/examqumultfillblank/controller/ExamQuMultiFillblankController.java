package com.skyeye.exam.examqumultfillblank.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examqumultfillblank.service.ExamQuMultiFillblankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "多行填空题管理", tags = "多行填空题管理", modelName = "多行填空题管理")
public class ExamQuMultiFillblankController {

    @Autowired
    private ExamQuMultiFillblankService examQuMultiFillblankService;

    /**
     * 分页获取多行填空题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryExamQuMultiFillblankList", value = "分页获取多行填空题选项表信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/ExamQuMultiFillblankController/queryExamQuMultiFillblankList")
    public void queryExamQuMultiFillblankList(InputObject inputObject, OutputObject outputObject) {
        examQuMultiFillblankService.queryPageList(inputObject, outputObject);
    }

    /**
     * 根据ID物理删除多行填空题选项表信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteExamQuMultiFillblankById", value = "根据ID物理删除多行填空题选项表信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/ExamQuMultiFillblankController/deleteExamQuMultiFillblankById")
    public void deleteExamQuMultiFillblankById(InputObject inputObject, OutputObject outputObject) {
        examQuMultiFillblankService.deleteById(inputObject, outputObject);
    }
}




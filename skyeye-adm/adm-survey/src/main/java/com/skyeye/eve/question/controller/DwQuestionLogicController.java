package com.skyeye.eve.question.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "题目逻辑设置管理", tags = "题目逻辑设置管理", modelName = "题目逻辑设置管理")
public class DwQuestionLogicController {

    @Autowired
    private DwQuestionLogicService dwQuestionLogicService;

    /**
     * 分页获取题目逻辑信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuestionLogicList", value = "分页获取题目逻辑信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuestionLogicController/queryDwQuestionLogicList")
    public void queryDwQuestionLogicList(InputObject inputObject, OutputObject outputObject) {
        dwQuestionLogicService.queryDwQuestionLogicList(inputObject, outputObject);
    }

    /**
     * 分页获取我的题目逻辑信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyDwQuestionLogicList", value = "分页获取我的题目逻辑信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuestionLogicController/queryMyDwQuestionLogicList")
    public void queryMyDwQuestionLogicList(InputObject inputObject, OutputObject outputObject) {
        dwQuestionLogicService.queryMyDwQuestionLogicList(inputObject, outputObject);
    }

    /**
     * 根据ID删除题目逻辑信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteMyDwQuestionLogicById", value = "根据ID删除题目逻辑信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuestionController/deleteMyDwQuestionLogicById")
    public void deleteMyDwQuestionLogicById(InputObject inputObject, OutputObject outputObject) {
        dwQuestionLogicService.deleteById(inputObject, outputObject);
    }

}


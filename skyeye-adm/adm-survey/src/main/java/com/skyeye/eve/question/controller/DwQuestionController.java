package com.skyeye.eve.question.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.service.DwQuestionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "题目管理", tags = "题目管理", modelName = "题库管理")
public class DwQuestionController {

    @Autowired
    private DwQuestionService dwquestionService;

    /**
     * 添加/编辑题目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwQuestion", value = "添加/编辑题目", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwQuestion.class)
    @RequestMapping("/post/DwQuestionController/writeDwQuestion")
    public void writeDwQuestion(InputObject inputObject, OutputObject outputObject) {
        dwquestionService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 分页获取未删除题目信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuestionList", value = "分页获取未删除题目信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuestionController/queryDwQuestionList")
    public void queryDwQuestionList(InputObject inputObject, OutputObject outputObject) {
        dwquestionService.queryDwQuestionList(inputObject, outputObject);
    }

    /**
     * 分页获取我的题目信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyDwQuestionList", value = "分页获取我的题目信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuestionController/queryMyDwQuestionList")
    public void queryMyDwQuestionList(InputObject inputObject, OutputObject outputObject) {
        dwquestionService.queryMyDwQuestionList(inputObject, outputObject);
    }

    /**
     * 分页获取所有题库列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryPageDwQuestionList", value = "分页获取所有题库列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuestionController/queryPageDwQuestionList")
    public void queryPageDwQuestionList(InputObject inputObject, OutputObject outputObject) {
        dwquestionService.queryPageDwQuestionList(inputObject, outputObject);
    }

    /**
     * 根据Id查询题目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "selectDwQuestionById", value = "根据Ids批量查询题目", method = "POST", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "ids", name = "ids", value = "主键ids", required = "required")})
    @RequestMapping("/post/DwQuestionController/selectDwQuestionById")
    public void selectDwQuestionById(InputObject inputObject, OutputObject outputObject) {
        dwquestionService.selectByIds(inputObject, outputObject);
    }

    /**
     * 根据ID删除题目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwQuestionById", value = "根据ID删除题目信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuestionController/deleteDwQuestionById")
    public void deleteDwQuestionById(InputObject inputObject, OutputObject outputObject) {
        dwquestionService.deleteById(inputObject, outputObject);
    }

}
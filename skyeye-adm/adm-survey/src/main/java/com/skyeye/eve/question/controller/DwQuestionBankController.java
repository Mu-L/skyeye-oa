package com.skyeye.eve.question.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwQuestionBank;
import com.skyeye.eve.question.service.DwQuestionBankService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "题库管理", tags = "题库管理", modelName = "题库管理")
public class DwQuestionBankController {

    @Autowired
    private DwQuestionBankService dwQuestionBankService;

    /**
     * 添加/编辑题库
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeDwQuestionBank", value = "添加/编辑题库", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = DwQuestionBank.class)
    @RequestMapping("/post/DwQuestionBankController/writeDwQuestionBank")
    public void writeDwQuestionBank(InputObject inputObject, OutputObject outputObject) {
        dwQuestionBankService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 分页获取所有题库信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryDwQuestionBankList", value = "分页获取所有题库信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuestionBankController/queryDwQuestionBankList")
    public void queryDwQuestionBankList(InputObject inputObject, OutputObject outputObject) {
        dwQuestionBankService.queryDwQuestionBankList(inputObject, outputObject);
    }

    /**
     * 分页获取我的题库信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "queryMyDwQuestionBankList", value = "分页获取我的题库信息列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/DwQuestionBankController/queryMyDwQuestionBankList")
    public void queryMyDwQuestionBankList(InputObject inputObject, OutputObject outputObject) {
        dwQuestionBankService.queryMyDwQuestionBankList(inputObject, outputObject);
    }

    /**
     * 根据ID删除题库信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "deleteDwQuestionBankById", value = "根据ID删除题库信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams({
            @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/DwQuestionController/deleteDwQuestionById")
    public void deleteDwQuestionById(InputObject inputObject, OutputObject outputObject) {
        dwQuestionBankService.deleteById(inputObject, outputObject);
    }


}

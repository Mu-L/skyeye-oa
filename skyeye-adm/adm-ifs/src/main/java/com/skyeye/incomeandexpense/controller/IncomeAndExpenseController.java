/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.incomeandexpense.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.incomeandexpense.entity.IncomeAndExpense;
import com.skyeye.incomeandexpense.service.IncomeAndExpenseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: IncomeAndExpenseController
 * @Description: 收支项目管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/21 17:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "收支项目管理", tags = "收支项目管理", modelName = "收支项目管理")
public class IncomeAndExpenseController {

    @Autowired
    private IncomeAndExpenseService incomeAndExpenseService;

    /**
     * 获取收支项目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "inoutitem001", value = "获取收支项目信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/InoutitemController/queryInoutitemList")
    public void queryInoutitemList(InputObject inputObject, OutputObject outputObject) {
        incomeAndExpenseService.queryPageList(inputObject, outputObject);
    }

    /**
     * 添加/编辑收支项目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "writeIncomeAndExpense", value = "添加/编辑收支项目", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = IncomeAndExpense.class)
    @RequestMapping("/post/InoutitemController/writeIncomeAndExpense")
    public void writeIncomeAndExpense(InputObject inputObject, OutputObject outputObject) {
        incomeAndExpenseService.saveOrUpdateEntity(inputObject, outputObject);
    }

    /**
     * 删除收支项目信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "inoutitem004", value = "删除收支项目信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/InoutitemController/deleteInoutitemById")
    public void deleteInoutitemById(InputObject inputObject, OutputObject outputObject) {
        incomeAndExpenseService.deleteById(inputObject, outputObject);
    }

    /**
     * 根据条件查询收支项目
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "inoutitem007", value = "根据条件查询收支项目", method = "GET", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "type", name = "type", value = "收支类型", required = "required,num")})
    @RequestMapping("/post/InoutitemController/queryInoutitemListByType")
    public void queryInoutitemListByType(InputObject inputObject, OutputObject outputObject) {
        incomeAndExpenseService.queryInoutitemListByType(inputObject, outputObject);
    }

}

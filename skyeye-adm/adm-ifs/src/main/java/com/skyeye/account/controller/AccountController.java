/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.account.controller;

import com.skyeye.account.entity.Account;
import com.skyeye.account.service.AccountService;
import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: AccountController
 * @Description: 结算账户管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2021/10/6 9:19
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "账户管理", tags = "账户管理", modelName = "账户管理")
public class AccountController {

    @Autowired
    private AccountService accountService;

    @ApiOperation(id = "account001", value = "查询账户信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/AccountController/queryAccountList")
    public void queryAccountList(InputObject inputObject, OutputObject outputObject) {
        accountService.queryPageList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeAccount", value = "添加/编辑账户信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = Account.class)
    @RequestMapping("/post/AccountController/writeAccount")
    public void writeAccount(InputObject inputObject, OutputObject outputObject) {
        accountService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAccountById", value = "根据id查询账户信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AccountController/queryAccountById")
    public void queryAccountById(InputObject inputObject, OutputObject outputObject) {
        accountService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAccountListById", value = "根据id批量查询账户信息", method = "POST", allUse = "0")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/AccountController/queryAccountListById")
    public void queryAccountListById(InputObject inputObject, OutputObject outputObject) {
        accountService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "account004", value = "根据id删除账户信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/AccountController/deleteAccountById")
    public void deleteAccountById(InputObject inputObject, OutputObject outputObject) {
        accountService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "account009", value = "获取所有的账户信息", method = "GET", allUse = "2")
    @RequestMapping("/post/AccountController/queryAllAccountList")
    public void queryAllAccountList(InputObject inputObject, OutputObject outputObject) {
        accountService.queryAllAccountList(inputObject, outputObject);
    }

}

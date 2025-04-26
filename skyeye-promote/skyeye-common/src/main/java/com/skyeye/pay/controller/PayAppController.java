/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.pay.entity.PayApp;
import com.skyeye.pay.service.PayAppService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: PayAppController
 * @Description: 支付应用信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31.
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "支付应用管理", tags = "支付应用管理", modelName = "支付应用管理")
public class PayAppController {

    @Autowired
    private PayAppService payAppService;

    @ApiOperation(id = "writePayApp", value = "新增/修改支付应用信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = PayApp.class)
    @RequestMapping("/post/PayAppController/writePayApp")
    public void writePayApp(InputObject inputObject, OutputObject outputObject) {
        payAppService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllEnabledPayAppList", value = "获取全部已启用的支付应用信息", method = "POST", allUse = "2")
    @RequestMapping("/post/PayAppController/queryAllEnabledPayAppList")
    public void queryAllEnabledPayAppList(InputObject inputObject, OutputObject outputObject) {
        payAppService.queryList(inputObject, outputObject);
    }

    @ApiOperation(id = "deletePayAppById", value = "根据id删除支付应用信息", method = "DELETE", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PayAppController/deletePayAppById")
    public void deletePayAppById(InputObject inputObject, OutputObject outputObject) {
        payAppService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayAppById", value = "根据id查询支付应用信息", method = "POST", allUse = "2")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/PayAppController/queryPayAppById")
    public void queryPayAppById(InputObject inputObject, OutputObject outputObject) {
        payAppService.selectById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryPayAppList", value = "分页查询支付应用信息列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/PayAppController/queryPayAppList")
    public void queryPayAppList(InputObject inputObject, OutputObject outputObject) {
        payAppService.queryPageList(inputObject, outputObject);
    }
}

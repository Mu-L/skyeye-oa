/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.customer.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.customer.entity.CustomerMation;
import com.skyeye.customer.entity.CustomerQueryDo;
import com.skyeye.customer.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: CustomerController
 * @Description: 客户信息管理控制类
 * @author: skyeye云系列--卫志强
 * @date: 2022/7/23 17:11
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "客户管理", tags = "客户管理", modelName = "客户管理")
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    @ApiOperation(id = "customer001", value = "获取客户管理列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/CustomerController/queryCustomerList")
    public void queryCustomerList(InputObject inputObject, OutputObject outputObject) {
        customerService.queryCustomerList(inputObject, outputObject);
    }

    @ApiOperation(id = "writeCustomerMation", value = "新增/编辑客户信息", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CustomerMation.class)
    @RequestMapping("/post/CustomerController/writeCustomerMation")
    public void writeCustomerMation(InputObject inputObject, OutputObject outputObject) {
        customerService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteCustomerMationById", value = "根据id删除客户信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "id", name = "id", value = "主键id", required = "required")})
    @RequestMapping("/post/CustomerController/deleteCustomerMationById")
    public void deleteCustomerMationById(InputObject inputObject, OutputObject outputObject) {
        customerService.deleteById(inputObject, outputObject);
    }

    @ApiOperation(id = "queryCustomerListByIds", value = "根据id批量获取客户信息", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "ids", name = "ids", value = "主键id", required = "required")})
    @RequestMapping("/post/CustomerController/queryCustomerListByIds")
    public void queryCustomerListByIds(InputObject inputObject, OutputObject outputObject) {
        customerService.selectByIds(inputObject, outputObject);
    }

    @ApiOperation(id = "customer012", value = "获取公海客户群列表", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = CustomerQueryDo.class)
    @RequestMapping("/post/CustomerController/queryInternationalCustomerList")
    public void queryInternationalCustomerList(InputObject inputObject, OutputObject outputObject) {
        customerService.queryInternationalCustomerList(inputObject, outputObject);
    }

    @ApiOperation(id = "queryAllCustomerList", value = "获取所有客户信息", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/CustomerController/queryAllCustomerList")
    public void queryAllCustomerList(InputObject inputObject, OutputObject outputObject) {
        customerService.queryList(inputObject, outputObject);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.server.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.annotation.operationlog.IgnoreOperationLog;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.server.entity.ServiceBean;
import com.skyeye.server.entity.ServiceBeanApi;
import com.skyeye.server.service.ServiceBeanService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ServiceBeanController
 * @Description: 所有实现了SkyeyeBusinessService的服务类的注册服务
 * @author: skyeye云系列--卫志强
 * @date: 2022/9/18 16:08
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "服务类注册", tags = "服务类注册", modelName = "系统公共模块")
public class ServiceBeanController {

    @Autowired
    private ServiceBeanService serviceBeanService;

    @IgnoreOperationLog
    @ApiOperation(id = "writeServiceBean", value = "新增/编辑服务类", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = ServiceBean.class)
    @RequestMapping("/post/ServiceBeanController/writeServiceBean")
    public void writeServiceBean(InputObject inputObject, OutputObject outputObject) {
        serviceBeanService.saveOrUpdateEntity(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "registerServiceBean", value = "服务类注册", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = ServiceBeanApi.class)
    @RequestMapping("/post/ServiceBeanController/registerServiceBean")
    public void registerServiceBean(InputObject inputObject, OutputObject outputObject) {
        serviceBeanService.registerServiceBean(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServiceClassForTree", value = "获取服务类信息(树结构)", method = "GET", allUse = "2")
    @RequestMapping("/post/ServiceBeanController/queryServiceClassForTree")
    public void queryServiceClassForTree(InputObject inputObject, OutputObject outputObject) {
        serviceBeanService.queryServiceClassForTree(inputObject, outputObject);
    }

    @ApiOperation(id = "queryServiceBeanByAppIdAndClassName", value = "根据appId和className查询服务类信息", method = "GET", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用得appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "服务类的className", required = "required"),
        @ApiImplicitParam(id = "needAttr", name = "needAttr", value = "是否需要查询属性信息", enumClass = WhetherEnum.class, defaultValue = "0")})
    @RequestMapping("/post/ServiceBeanController/queryServiceBeanByAppIdAndClassName")
    public void queryServiceBeanByAppIdAndClassName(InputObject inputObject, OutputObject outputObject) {
        serviceBeanService.queryServiceBeanByAppIdAndClassName(inputObject, outputObject);
    }

    @ApiOperation(id = "deleteServiceBeanByAppIdAndClassName", value = "根据appId和className删除服务类信息", method = "DELETE", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "appId", name = "appId", value = "应用得appId", required = "required"),
        @ApiImplicitParam(id = "className", name = "className", value = "服务类的className", required = "required")})
    @RequestMapping("/post/ServiceBeanController/deleteServiceBeanByAppIdAndClassName")
    public void deleteServiceBeanByAppIdAndClassName(InputObject inputObject, OutputObject outputObject) {
        serviceBeanService.deleteServiceBeanByAppIdAndClassName(inputObject, outputObject);
    }

}

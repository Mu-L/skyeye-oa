/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.common.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.service.ReportCommonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: ReportCommonController
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/17 21:31
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@RestController
@Api(value = "公共接口", tags = "公共接口", modelName = "公共接口")
public class ReportCommonController {

    @Autowired
    private ReportCommonService reportCommonService;

    @ApiOperation(id = "reportcommon001", value = "测试数据库连接", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "driverClass", name = "driverClass", value = "数据源驱动类", required = "required"),
        @ApiImplicitParam(id = "url", name = "url", value = "数据源连接字符串", required = "required"),
        @ApiImplicitParam(id = "user", name = "user", value = "用户名", required = "required"),
        @ApiImplicitParam(id = "pass", name = "pass", value = "密码")})
    @RequestMapping("/post/ReportCommonController/testConnection")
    public void testConnection(InputObject inputObject, OutputObject outputObject) {
        reportCommonService.testConnection(inputObject, outputObject);
    }

    @ApiOperation(id = "reportcommon002", value = "解析xml格式文本", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "xmlText", name = "xmlText", value = "xml格式文本", required = "required")})
    @RequestMapping("/post/ReportCommonController/parseXmlText")
    public void parseXmlText(InputObject inputObject, OutputObject outputObject) {
        reportCommonService.parseXmlText(inputObject, outputObject);
    }

    @ApiOperation(id = "reportcommon003", value = "解析Json格式文本", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "jsonText", name = "jsonText", value = "json格式文本", required = "required")})
    @RequestMapping("/post/ReportCommonController/parseJsonText")
    public void parseJsonText(InputObject inputObject, OutputObject outputObject) {
        reportCommonService.parseJsonText(inputObject, outputObject);
    }

    @ApiOperation(id = "reportcommon006", value = "获取数据库类型", method = "GET", allUse = "2")
    @RequestMapping("/post/ReportCommonController/queryDataBaseMationList")
    public void queryDataBaseMationList(InputObject inputObject, OutputObject outputObject) {
        reportCommonService.queryDataBaseMationList(inputObject, outputObject);
    }

    @ApiOperation(id = "reportcommon007", value = "获取连接池类型", method = "GET", allUse = "2")
    @RequestMapping("/post/ReportCommonController/queryPoolMationList")
    public void queryPoolMationList(InputObject inputObject, OutputObject outputObject) {
        reportCommonService.queryPoolMationList(inputObject, outputObject);
    }

    @ApiOperation(id = "reportcommon004", value = "解析SQL数据源", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "sqlText", name = "sqlText", value = "sql语句", required = "required"),
        @ApiImplicitParam(id = "dataBaseId", name = "dataBaseId", value = "数据源id", required = "required")})
    @RequestMapping("/post/ReportCommonController/parseSQLText")
    public void parseSQLText(InputObject inputObject, OutputObject outputObject) {
        reportCommonService.parseSQLText(inputObject, outputObject);
    }

    /**
     * 解析Rest接口
     * todo serviceStr字段目前还无法解析，导致API接口解析会有问题
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @ApiOperation(id = "reportcommon005", value = "解析Rest接口", method = "POST", allUse = "2")
    @ApiImplicitParams(value = {
        @ApiImplicitParam(id = "serviceStr", name = "serviceStr", value = "接口对应的服务", required = "required"),
        @ApiImplicitParam(id = "requestUrl", name = "requestUrl", value = "请求路径", required = "required"),
        @ApiImplicitParam(id = "requestMethod", name = "requestMethod", value = "请求方式", required = "required"),
        @ApiImplicitParam(id = "requestHeader", name = "requestHeader", value = "请求头", required = "required,json"),
        @ApiImplicitParam(id = "requestBody", name = "requestBody", value = "请求体", required = "json")})
    @RequestMapping("/post/ReportCommonController/parseRestText")
    public void parseRestText(InputObject inputObject, OutputObject outputObject) {
        reportCommonService.parseRestText(inputObject, outputObject);
    }

}

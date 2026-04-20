/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.annotation.operationlog.IgnoreOperationLog;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.personnel.entity.SysEveUserOperLog;
import com.skyeye.personnel.service.SysEveUserOperLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "系统操作日志", tags = "系统操作日志", modelName = "用户管理")
public class SysEveUserOperLogController {

    @Autowired
    private SysEveUserOperLogService sysEveUserOperLogService;

    @IgnoreOperationLog
    @ApiOperation(id = "createSysOperLog", value = "新增登录日志", method = "POST", allUse = "0")
    @ApiImplicitParams(classBean = SysEveUserOperLog.class)
    @RequestMapping("/post/SysEveUserOperLogController/createSysOperLog")
    public void createSysOperLog(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.createEntity(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogList", value = "查询系统操作日志列表", method = "POST", allUse = "1")
    @ApiImplicitParams(classBean = CommonPageInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogList")
    public void querySysOperLogList(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryPageList(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogOverviewStat", value = "查询系统操作日志概览统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogOverviewStat")
    public void querySysOperLogOverviewStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogOverviewStat(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogTrendStat", value = "查询系统操作日志趋势统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogTrendStat")
    public void querySysOperLogTrendStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogTrendStat(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogTopApiStat", value = "查询系统操作日志Top接口统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogTopApiStat")
    public void querySysOperLogTopApiStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogTopApiStat(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogTopPathStat", value = "查询系统操作日志Top请求路径统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogTopPathStat")
    public void querySysOperLogTopPathStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogTopPathStat(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogTopUserStat", value = "查询系统操作日志Top访问人统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogTopUserStat")
    public void querySysOperLogTopUserStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogTopUserStat(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogTopSourceServiceStat", value = "查询系统操作日志Top来源服务统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogTopSourceServiceStat")
    public void querySysOperLogTopSourceServiceStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogTopSourceServiceStat(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogTopFailApiStat", value = "查询系统操作日志失败最多接口统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogTopFailApiStat")
    public void querySysOperLogTopFailApiStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogTopFailApiStat(inputObject, outputObject);
    }

    @IgnoreOperationLog
    @ApiOperation(id = "querySysOperLogTopSlowApiStat", value = "查询系统操作日志慢接口统计", method = "POST", allUse = "2")
    @ApiImplicitParams(classBean = TableSelectInfo.class)
    @RequestMapping("/post/SysEveUserOperLogController/querySysOperLogTopSlowApiStat")
    public void querySysOperLogTopSlowApiStat(InputObject inputObject, OutputObject outputObject) {
        sysEveUserOperLogService.queryOperLogTopSlowApiStat(inputObject, outputObject);
    }
}

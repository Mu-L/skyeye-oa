/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.annotation.operationlog.IgnoreOperationLog;
import com.skyeye.common.entity.search.CommonPageInfo;
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
}

package com.skyeye.office.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.service.DocumentEditLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocumentEditLogController
 * @Description: 文档编辑日志控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@RestController
@Api(value = "文档编辑日志", tags = "文档编辑日志", modelName = "文档协同编辑模块")
public class DocumentEditLogController {

    @Autowired
    private DocumentEditLogService documentEditLogService;

    /**
     * 添加编辑日志
     */
    @ApiOperation(id = "addEditLog", value = "添加编辑日志", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "version", name = "version", value = "版本号", required = "required"),
        @ApiImplicitParam(id = "operationType", name = "operationType", value = "操作类型", required = "required"),
        @ApiImplicitParam(id = "operationContent", name = "operationContent", value = "操作内容")
    })
    @RequestMapping("/post/DocumentEditLogController/addEditLog")
    public void addEditLog(InputObject inputObject, OutputObject outputObject) {
        documentEditLogService.addEditLog(inputObject, outputObject);
    }

    /**
     * 获取编辑日志列表
     */
    @ApiOperation(id = "getEditLogs", value = "获取编辑日志列表", method = "GET", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required")
    })
    @RequestMapping("/post/DocumentEditLogController/getEditLogs")
    public void getEditLogs(InputObject inputObject, OutputObject outputObject) {
        documentEditLogService.getEditLogs(inputObject, outputObject);
    }
} 
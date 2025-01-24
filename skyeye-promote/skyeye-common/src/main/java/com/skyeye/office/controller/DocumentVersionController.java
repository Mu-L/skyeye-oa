package com.skyeye.office.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.service.DocumentVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocumentVersionController
 * @Description: 文档版本管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@RestController
@Api(value = "文档版本管理", tags = "文档版本管理", modelName = "文档协同编辑模块")
public class DocumentVersionController {

    @Autowired
    private DocumentVersionService documentVersionService;

    /**
     * 创建新版本
     */
    @ApiOperation(id = "createVersion", value = "创建新版本", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "fileUrl", name = "fileUrl", value = "文件URL", required = "required"),
        @ApiImplicitParam(id = "versionDesc", name = "versionDesc", value = "版本说明")
    })
    @RequestMapping("/post/DocumentVersionController/createVersion")
    public void createVersion(InputObject inputObject, OutputObject outputObject) {
        documentVersionService.createVersion(inputObject, outputObject);
    }

    /**
     * 获取版本历史
     */
    @ApiOperation(id = "getVersionHistory", value = "获取版本历史", method = "GET", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required")
    })
    @RequestMapping("/post/DocumentVersionController/getVersionHistory")
    public void getVersionHistory(InputObject inputObject, OutputObject outputObject) {
        documentVersionService.getVersionHistory(inputObject, outputObject);
    }

    /**
     * 回滚到指定版本
     */
    @ApiOperation(id = "rollbackVersion", value = "回滚到指定版本", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "version", name = "version", value = "版本号", required = "required")
    })
    @RequestMapping("/post/DocumentVersionController/rollbackVersion")
    public void rollbackVersion(InputObject inputObject, OutputObject outputObject) {
        documentVersionService.rollbackVersion(inputObject, outputObject);
    }
} 
package com.skyeye.office.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.service.DocumentAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocumentAuthController
 * @Description: 文档权限管理控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@RestController
@Api(value = "文档权限管理", tags = "文档权限管理", modelName = "文档协同编辑模块")
public class DocumentAuthController {

    @Autowired
    private DocumentAuthService documentAuthService;

    /**
     * 授予权限
     */
    @ApiOperation(id = "grantAuth", value = "授予权限", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户ID", required = "required"),
        @ApiImplicitParam(id = "authType", name = "authType", value = "权限类型(owner/edit/view)", required = "required")
    })
    @RequestMapping("/post/DocumentAuthController/grantAuth")
    public void grantAuth(InputObject inputObject, OutputObject outputObject) {
        documentAuthService.grantAuth(inputObject, outputObject);
    }

    /**
     * 撤销权限
     */
    @ApiOperation(id = "revokeAuth", value = "撤销权限", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户ID", required = "required")
    })
    @RequestMapping("/post/DocumentAuthController/revokeAuth")
    public void revokeAuth(InputObject inputObject, OutputObject outputObject) {
        documentAuthService.revokeAuth(inputObject, outputObject);
    }

    /**
     * 获取文档授权用户列表
     */
    @ApiOperation(id = "getAuthUsers", value = "获取文档授权用户列表", method = "GET", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required")
    })
    @RequestMapping("/post/DocumentAuthController/getAuthUsers")
    public void getAuthUsers(InputObject inputObject, OutputObject outputObject) {
        documentAuthService.getAuthUsers(inputObject, outputObject);
    }

    /**
     * 检查用户权限
     */
    @ApiOperation(id = "checkAuth", value = "检查用户权限", method = "GET", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户ID", required = "required")
    })
    @RequestMapping("/post/DocumentAuthController/checkAuth")
    public void checkAuth(InputObject inputObject, OutputObject outputObject) {
        documentAuthService.checkAuth(inputObject, outputObject);
    }
} 
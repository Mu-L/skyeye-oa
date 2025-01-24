package com.skyeye.office.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiImplicitParam;
import com.skyeye.annotation.api.ApiImplicitParams;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.service.DocumentOnlineUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName: DocumentOnlineUserController
 * @Description: 文档在线用户控制层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@RestController
@Api(value = "文档在线用户", tags = "文档在线用户", modelName = "文档协同编辑模块")
public class DocumentOnlineUserController {

    @Autowired
    private DocumentOnlineUserService documentOnlineUserService;

    /**
     * 用户加入文档
     */
    @ApiOperation(id = "userJoin", value = "用户加入文档", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户ID", required = "required")
    })
    @RequestMapping("/post/DocumentOnlineUserController/userJoin")
    public void userJoin(InputObject inputObject, OutputObject outputObject) {
        documentOnlineUserService.userJoin(inputObject, outputObject);
    }

    /**
     * 用户离开文档
     */
    @ApiOperation(id = "userLeave", value = "用户离开文档", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户ID", required = "required")
    })
    @RequestMapping("/post/DocumentOnlineUserController/userLeave")
    public void userLeave(InputObject inputObject, OutputObject outputObject) {
        documentOnlineUserService.userLeave(inputObject, outputObject);
    }

    /**
     * 更新用户活跃时间
     */
    @ApiOperation(id = "updateActiveTime", value = "更新用户活跃时间", method = "POST", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required"),
        @ApiImplicitParam(id = "userId", name = "userId", value = "用户ID", required = "required")
    })
    @RequestMapping("/post/DocumentOnlineUserController/updateActiveTime")
    public void updateActiveTime(InputObject inputObject, OutputObject outputObject) {
        documentOnlineUserService.updateActiveTime(inputObject, outputObject);
    }

    /**
     * 获取在线用户列表
     */
    @ApiOperation(id = "getOnlineUsers", value = "获取在线用户列表", method = "GET", allUse = "1")
    @ApiImplicitParams({
        @ApiImplicitParam(id = "documentId", name = "documentId", value = "文档ID", required = "required")
    })
    @RequestMapping("/post/DocumentOnlineUserController/getOnlineUsers")
    public void getOnlineUsers(InputObject inputObject, OutputObject outputObject) {
        documentOnlineUserService.getOnlineUsers(inputObject, outputObject);
    }
} 
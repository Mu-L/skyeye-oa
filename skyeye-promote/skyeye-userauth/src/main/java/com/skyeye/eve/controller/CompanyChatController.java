/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.annotation.api.ApiOperation;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.service.CompanyChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "聊天信息", tags = "聊天信息", modelName = "聊天信息")
public class CompanyChatController {

    @Autowired
    private CompanyChatService companyChatService;

    @ApiOperation(id = "companychat001", value = "获取好友列表，群聊信息，个人信息", method = "GET", allUse = "2")
    @RequestMapping("/post/CompanyChatController/getList")
    public void getList(InputObject inputObject, OutputObject outputObject) {
        companyChatService.getList(inputObject, outputObject);
    }

    /**
     * 编辑签名
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @RequestMapping("/post/CompanyChatController/editUserSignByUserId")
    public void editUserSignByUserId(InputObject inputObject, OutputObject outputObject) {
        companyChatService.editUserSignByUserId(inputObject, outputObject);
    }

}

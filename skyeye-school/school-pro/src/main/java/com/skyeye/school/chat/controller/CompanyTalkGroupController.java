package com.skyeye.school.chat.controller;

import com.skyeye.annotation.api.Api;
import com.skyeye.school.chat.service.CompanyChatGroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "群组管理", tags = "群组管理", modelName = "聊天模块")
public class CompanyTalkGroupController {

    @Autowired
    private CompanyChatGroupService companyChatGroupService;

}

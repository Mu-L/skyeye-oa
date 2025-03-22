package com.skyeye.school.chat.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.school.chat.dao.CompanyChatGroupDao;
import com.skyeye.school.chat.entity.CompanyChatGroup;
import com.skyeye.school.chat.service.CompanyChatGroupService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "群组管理", groupName = "聊天模块")
public class CompanyChatGroupServiceImpl extends SkyeyeBusinessServiceImpl<CompanyChatGroupDao, CompanyChatGroup> implements CompanyChatGroupService {
}

package com.skyeye.school.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.chat.dao.CompanyChatGroupDao;
import com.skyeye.school.chat.entity.ChatHistory;
import com.skyeye.school.chat.entity.CompanyChatGroup;
import com.skyeye.school.chat.service.ChatHistoryService;
import com.skyeye.school.chat.service.CompanyChatGroupService;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.personnel.service.SysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "群组管理", groupName = "聊天模块")
public class CompanyChatGroupServiceImpl extends SkyeyeBusinessServiceImpl<CompanyChatGroupDao, CompanyChatGroup> implements CompanyChatGroupService {
}
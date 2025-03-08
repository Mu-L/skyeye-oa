package com.skyeye.school.chat.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.school.chat.dao.ChatHistoryDao;
import com.skyeye.school.chat.entity.ChatHistory;
import com.skyeye.school.chat.service.ChatHistoryService;
import org.springframework.stereotype.Service;

@Service
@SkyeyeService(name = "聊天历史", groupName = "聊天历史")
public class ChatHistoryServiceImpl extends SkyeyeBusinessServiceImpl<ChatHistoryDao, ChatHistory> implements ChatHistoryService {
}

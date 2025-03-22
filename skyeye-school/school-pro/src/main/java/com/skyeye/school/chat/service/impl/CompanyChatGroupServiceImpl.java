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

    @Autowired
    private ChatHistoryService chatHistoryService;

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Override
    public List<Map<String, Object>> queryChatLogByPerToPer(Map<String, Object> map) {
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper
                .eq(MybatisPlusUtil.toColumns(ChatHistory::getChatType), CommonNumConstants.NUM_ONE)
                .and(wrapper -> wrapper
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getSendId), map.get("userId").toString())
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), map.get("receiveId").toString())
                        .or()
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getSendId), map.get("receiveId").toString())
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), map.get("userId").toString()));
        List<ChatHistory> chatHistoryList = chatHistoryService.list(queryWrapper);
        List<String> userIds = new ArrayList<>();
        for (ChatHistory chatHistory : chatHistoryList) {
            userIds.add(chatHistory.getSendId());
            userIds.add(chatHistory.getReceiveId());
        }
        List<SysEveUserStaff> userStaffList = sysEveUserStaffService.selectByUserIds(userIds);
        Map<String, String> userMap = new HashMap<>();
        for (SysEveUserStaff userStaff : userStaffList) {
            userMap.put(userStaff.getUserId(), userStaff.getUserName());
        }
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatHistory chatHistory : chatHistoryList) {
            Map<String, Object> map1 = new HashMap<>();
            map1.put("id", chatHistory.getId());
            map1.put("sendId", chatHistory.getSendId());
            map1.put("sendName", userMap.get(chatHistory.getSendId()));
            map1.put("receiveName", userMap.get(chatHistory.getReceiveId()));
            map1.put("content", chatHistory.getContent());
            map1.put("userId", map.get("userId").toString());
            map1.put("createTime", chatHistory.getCreateTime().toString());
            result.add(map);
        }
        return result;
    }
}
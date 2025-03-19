package com.skyeye.school.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.chat.dao.ChatHistoryDao;
import com.skyeye.school.chat.entity.ChatHistory;
import com.skyeye.school.chat.entity.FriendRelationship;
import com.skyeye.school.chat.service.ChatHistoryService;
import com.skyeye.school.chat.service.FriendRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "聊天历史", groupName = "聊天历史")
public class ChatHistoryServiceImpl extends SkyeyeBusinessServiceImpl<ChatHistoryDao, ChatHistory> implements ChatHistoryService {

    @Autowired
    private FriendRelationshipService friendRelationshipService;


    @Override
    protected void createPrepose(ChatHistory entity) {
        String uniqueId = entity.getUniqueId();
        FriendRelationship friendRelationship = friendRelationshipService.selectById(uniqueId);
        if (friendRelationship == null) {
            throw new CustomException("不是好友，不能聊天");
        }
        Integer chatType = entity.getChatType();
        if (chatType == null) {
            throw new CustomException("聊天类型不能为空");
        }
    }

    @Override
    public void queryChatHistoryByUniqueId(InputObject inputObject, OutputObject outputObject) {
        String uniqueId = inputObject.getParams().get("uniqueId").toString();
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getUniqueId), uniqueId);
        List<ChatHistory> list = list(queryWrapper);
        List<String> collect = list.stream().map(ChatHistory::getContent).collect(Collectors.toList());
        outputObject.setBeans(collect);
    }

    @Override
    public void deleteChatHistoryByUniqueId(InputObject inputObject, OutputObject outputObject) {
        String uniqueId = inputObject.getParams().get("uniqueId").toString();
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getUniqueId), uniqueId);
        remove(queryWrapper);
    }

    @Override
    public void deleteChatHistoryById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        remove(queryWrapper);
    }

    @Override
    public String createEntity(JSONObject jsonObject, Integer chatType, Integer readType) {
        ChatHistory chatHistory = new ChatHistory();
        chatHistory.setContent(jsonObject.getStr("message"));
        chatHistory.setSendId(jsonObject.getStr("userId"));
        String uniqueId = getSortString(jsonObject.getStr("userId"), jsonObject.getStr("to"));
        chatHistory.setUniqueId(uniqueId);
        chatHistory.setReceiveId(jsonObject.getStr("to"));
        chatHistory.setCreateTime(DateUtil.getTimeAndToString());
        chatHistory.setReadType(readType);
        chatHistory.setChatType(chatType);
        return createEntity(chatHistory, StrUtil.EMPTY);
    }

    @Override
    public String createEntity(JSONObject jsonObject, Integer chatType) {
        return createEntity(jsonObject, chatType, WhetherEnum.DISABLE_USING.getKey());
    }

    private String getSortString(String str1, String str2) {
        List<String> list = new ArrayList<>();
        list.add(str1);
        list.add(str2);
        list.sort(String::compareTo);
        return Joiner.on(CommonCharConstants.HORIZONTAL_LINE_MARK).join(list);
    }
}

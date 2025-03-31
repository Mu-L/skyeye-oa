/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.rest.promote.company.service.ISysEveUserStaffService;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.chat.dao.ChatHistoryDao;
import com.skyeye.school.chat.entity.ChatHistory;
import com.skyeye.school.chat.enums.ChatType;
import com.skyeye.school.chat.service.ChatHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "聊天历史", groupName = "聊天历史")
public class ChatHistoryServiceImpl extends SkyeyeBusinessServiceImpl<ChatHistoryDao, ChatHistory> implements ChatHistoryService {

    @Autowired
    private ISysEveUserStaffService iSysEveUserService;

    @Autowired
    private IUserService iUserService;

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

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public void queryMyChatUnReadMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        // 目前只是查询用户的未读消息，群聊消息暂时不做处理
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ChatHistory::getCreateTime));
        List<ChatHistory> chatHistoryList = list(queryWrapper);
        // 根据用户id查询员工数据
        List<String> userIds = chatHistoryList.stream().map(ChatHistory::getSendId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> staffList = iAuthUserService.queryDataMationByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds));
        Map<String, Map<String, Object>> userIdToStaff = staffList.stream().collect(Collectors.toMap(m -> m.get("userId").toString(), m -> m));
        // 给聊天记录添加员工信息
        chatHistoryList.forEach(talkChatHistory -> {
            Map<String, Object> staff = userIdToStaff.get(talkChatHistory.getSendId());
            talkChatHistory.setSendStaffMation(staff);
        });
        outputObject.setBeans(chatHistoryList);
        outputObject.settotal(chatHistoryList.size());
    }

    @Override
    public void editChatHistoryToRead(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String sendId = inputObject.getParams().get("sendId").toString();
        UpdateWrapper<ChatHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), userId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getSendId), sendId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(ChatHistory::getReadType), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void queryMyChatMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        // 分组查询我的最近的聊天消息列表(50条)
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), userId)
                .or().eq(MybatisPlusUtil.toColumns(ChatHistory::getSendId), userId));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ChatHistory::getCreateTime));
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(ChatHistory::getUniqueId));
        queryWrapper.last("LIMIT 50");
        List<ChatHistory> talkChatHistoryList = list(queryWrapper);
        if (CollectionUtil.isEmpty(talkChatHistoryList)) {
            return;
        }
        // 根据用户id查询员工数据
        List<String> userIds = talkChatHistoryList.stream()
            .filter(talkChatHistory -> talkChatHistory.getChatType() == ChatType.PERSONAL_TO_PERSONAL.getKey())
            .map(ChatHistory::getSendId).distinct().collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(userIds)) {
            List<String> receiveIds = talkChatHistoryList.stream()
                .filter(talkChatHistory -> talkChatHistory.getChatType() == ChatType.PERSONAL_TO_PERSONAL.getKey())
                .map(ChatHistory::getReceiveId).distinct().collect(Collectors.toList());
            userIds.addAll(receiveIds);
            userIds = userIds.stream().distinct().collect(Collectors.toList());
        }
        // 教师信息
        Map<String, Map<String, Object>> userMap = iAuthUserService.queryUserNameList(userIds);
        // 学生信息
        String userIdsStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds);
        List<Map<String, Object>> studentList = iUserService.queryEntityMationByIds(userIdsStr);
        Map<String, Map<String, Object>> studentMap = studentList.stream().collect(Collectors.toMap(m -> m.get("id").toString(), m -> m));
        List<Map<String, Object>> result = new ArrayList<>();
        for (ChatHistory talkChatHistory : talkChatHistoryList) {
            Map<String, Object> bean = new HashMap<>();
            if (talkChatHistory.getChatType() == ChatType.PERSONAL_TO_PERSONAL.getKey()) {
                Map<String, Object> user;
                // 1. 先判断是否是教师
                if (StrUtil.equals(userId, talkChatHistory.getSendId())) {
                    // 我发送的消息
                    user = userMap.get(talkChatHistory.getReceiveId());
                } else {
                    // 我接收的消息
                    user = userMap.get(talkChatHistory.getSendId());
                }
                if (CollectionUtil.isNotEmpty(user)) {
                    bean.put("type", LoginIdentity.TEACHER.getKey());
                }

                // 2. 判断是否是学生
                if (user == null) {
                    if (StrUtil.equals(userId, talkChatHistory.getSendId())) {
                        // 我发送的消息
                        user = studentMap.get(talkChatHistory.getReceiveId());
                    } else {
                        // 我接收的消息
                        user = studentMap.get(talkChatHistory.getSendId());
                    }
                    bean.put("type", LoginIdentity.STUDENT.getKey());
                }
                if (user == null) {
                    continue;
                }
                // 发送者信息
                if (StrUtil.equals(bean.get("type").toString(), LoginIdentity.TEACHER.getKey())) {
                    bean.put("name", user.get("userName").toString());
                    bean.put("avatar", user.get("userPhoto").toString());
                    bean.put("staffId", user.get("staffId").toString());
                } else {
                    bean.put("name", user.get("name").toString());
                    bean.put("avatar", user.getOrDefault("img", StrUtil.EMPTY).toString());
                }
                bean.put("talkId", user.get("id").toString());
            }
            bean.put("sendId", talkChatHistory.getSendId());
            bean.put("content", talkChatHistory.getContent());
            bean.put("createTime", talkChatHistory.getCreateTime());
            bean.put("chatType", talkChatHistory.getChatType());
            result.add(bean);
        }
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void deleteMyChatMessageList(InputObject inputObject, OutputObject outputObject) {
        // 获取当前用户ID
        String userId = inputObject.getLogParams().get("id").toString();
        // 获取要删除的会话唯一标识（
        Map<String, Object> map = inputObject.getParams();
        String uniqueId = map.get("uniqueId").toString();
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getUniqueId), uniqueId)
            .and(wrapper -> wrapper.eq(MybatisPlusUtil.toColumns(ChatHistory::getSendId), userId)
                .or()
                .eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), userId));
        boolean success = remove(queryWrapper);
        if (success) {
            outputObject.setreturnMessage("会话删除成功");
        } else {
            outputObject.setreturnMessage("会话删除失败");
        }
    }

    @Override
    public void queryChatLogByType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id").toString());
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = queryChatLogByPerToPer(map);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    public List<Map<String, Object>> queryChatLogByPerToPer(Map<String, Object> map) {
        QueryWrapper<ChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper
            .eq(MybatisPlusUtil.toColumns(ChatHistory::getChatType), CommonNumConstants.NUM_ONE)
            .and(wrapper ->
                wrapper.or(wrapperOr -> wrapperOr
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getSendId), map.get("userId").toString())
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), map.get("receiveId").toString()))
                    .or(wrapperOr -> wrapperOr
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getSendId), map.get("receiveId").toString())
                        .eq(MybatisPlusUtil.toColumns(ChatHistory::getReceiveId), map.get("userId").toString())))
            .orderByDesc(MybatisPlusUtil.toColumns(ChatHistory::getCreateTime));
        List<ChatHistory> chatHistoryList = list(queryWrapper);
        List<String> userIds = new ArrayList<>();
        for (ChatHistory chatHistory : chatHistoryList) {
            userIds.add(chatHistory.getSendId());
            userIds.add(chatHistory.getReceiveId());
        }

        String userIdsStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds);
        List<Map<String, Object>> userStaffList = iAuthUserService.queryDataMationByIds(userIdsStr);

        Map<String, String> userMap = userStaffList.stream().collect(Collectors.toMap(
            m -> m.get("userId").toString(),
            n -> n.get("userName").toString(),
            (existing, replacement) -> existing
        ));
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
            result.add(map1);
        }
        return result;
    }
}

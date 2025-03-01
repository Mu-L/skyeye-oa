/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chat.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.chat.dao.TalkChatHistoryDao;
import com.skyeye.chat.entity.TalkChatHistory;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.chat.service.TalkChatHistoryService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.enumclass.CompanyTalkGroupState;
import com.skyeye.eve.service.CompanyTalkGroupService;
import com.skyeye.personnel.service.SysEveUserStaffService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TalkChatHistoryServiceImpl
 * @Description: 聊天记录服务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/12 14:25
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "聊天历史记录", groupName = "聊天历史记录")
public class TalkChatHistoryServiceImpl extends SkyeyeBusinessServiceImpl<TalkChatHistoryDao, TalkChatHistory> implements TalkChatHistoryService {

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private CompanyTalkGroupService companyTalkGroupService;

    @Override
    public String createEntity(JSONObject jsonObject, Integer chatType) {
        return createEntity(jsonObject, chatType, WhetherEnum.DISABLE_USING.getKey());
    }

    @Override
    public String createEntity(JSONObject jsonObject, Integer chatType, Integer readType) {
        TalkChatHistory talkChatHistory = new TalkChatHistory();
        talkChatHistory.setContent(jsonObject.getStr("message"));
        talkChatHistory.setSendId(jsonObject.getStr("userId"));
        talkChatHistory.setReceiveId(jsonObject.getStr("to"));
        talkChatHistory.setCreateTime(DateUtil.getTimeAndToString());
        talkChatHistory.setReadType(readType);
        talkChatHistory.setChatType(chatType);
        return createEntity(talkChatHistory, StrUtil.EMPTY);
    }

    @Override
    public void queryMyUnReadMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        // 目前只是查询用户的未读消息，群聊消息暂时不做处理
        QueryWrapper<TalkChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime));
        List<TalkChatHistory> talkChatHistoryList = list(queryWrapper);
        // 根据用户id查询员工数据
        List<String> userIds = talkChatHistoryList.stream().map(TalkChatHistory::getSendId).distinct().collect(Collectors.toList());
        List<Map<String, Object>> staffList = sysEveUserStaffService.queryUserMationList(Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds), null);
        Map<String, Map<String, Object>> userIdToStaff = staffList.stream().collect(Collectors.toMap(m -> m.get("userId").toString(), m -> m));
        // 给聊天记录添加员工信息
        talkChatHistoryList.forEach(talkChatHistory -> {
            Map<String, Object> staff = userIdToStaff.get(talkChatHistory.getSendId());
            talkChatHistory.setSendStaffMation(staff);
        });

        outputObject.setBeans(talkChatHistoryList);
        outputObject.settotal(talkChatHistoryList.size());
    }

    @Override
    public void editTalkChatHistoryToRead(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String sendId = inputObject.getParams().get("sendId").toString();
        UpdateWrapper<TalkChatHistory> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), sendId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.DISABLE_USING.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TalkChatHistory::getReadType), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void queryMyTalkMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        // 分组查询我的最近的聊天消息列表(50条)
        QueryWrapper<TalkChatHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkChatHistory::getReceiveId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime));
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId));
        queryWrapper.last("LIMIT 50");
        queryWrapper.select(MybatisPlusUtil.toColumns(TalkChatHistory::getSendId), MybatisPlusUtil.toColumns(TalkChatHistory::getContent),
            MybatisPlusUtil.toColumns(TalkChatHistory::getCreateTime), MybatisPlusUtil.toColumns(TalkChatHistory::getChatType));
        List<TalkChatHistory> talkChatHistoryList = list(queryWrapper);

        // 根据用户id查询员工数据
        List<String> userIds = talkChatHistoryList.stream()
            .filter(talkChatHistory -> talkChatHistory.getChatType() == TalkChatType.PERSONAL_TO_PERSONAL.getKey())
            .map(TalkChatHistory::getSendId).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> userMap = iAuthUserService.queryUserNameList(userIds);

        // 根据群组id 查询群组数据
        List<String> groupIds = talkChatHistoryList.stream()
            .filter(talkChatHistory -> talkChatHistory.getChatType() == TalkChatType.GROUP_CHAT.getKey())
            .map(TalkChatHistory::getSendId).distinct().collect(Collectors.toList());
        Map<String, CompanyTalkGroup> groupMap = companyTalkGroupService.selectMapByIds(groupIds);

        List<Map<String, Object>> result = new ArrayList<>();
        talkChatHistoryList.forEach(talkChatHistory -> {
            Map<String, Object> bean = new HashMap<>();

            if (talkChatHistory.getChatType() == TalkChatType.PERSONAL_TO_PERSONAL.getKey()) {
                Map<String, Object> user = userMap.get(talkChatHistory.getSendId());
                bean.put("name", user.get("userName").toString());
                bean.put("avatar", user.get("userPhoto").toString());
            } else if (talkChatHistory.getChatType() == TalkChatType.GROUP_CHAT.getKey()) {
                CompanyTalkGroup group = groupMap.get(talkChatHistory.getSendId());
                if (group.getState() != CompanyTalkGroupState.NORMAL.getKey()) {
                    return;
                }
                bean.put("name", group.getGroupName());
                bean.put("avatar", group.getGroupImg());
            }
            bean.put("sendId", talkChatHistory.getSendId());
            bean.put("content", talkChatHistory.getContent());
            bean.put("createTime", talkChatHistory.getCreateTime());
            bean.put("chatType", talkChatHistory.getChatType());
            result.add(bean);
        });
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

}

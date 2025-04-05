package com.skyeye.school.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.chat.dao.UniqueDao;
import com.skyeye.school.chat.entity.ChatHistory;
import com.skyeye.school.chat.entity.Unique;
import com.skyeye.school.chat.service.ChatHistoryService;
import com.skyeye.school.chat.service.UniqueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
@SkyeyeService(name = "聊天会话管理", groupName = "聊天会话管理")
public class UniqueServiceImpl extends SkyeyeBusinessServiceImpl<UniqueDao, Unique> implements UniqueService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ChatHistoryService chatHistoryService;

    public void queryMyChatMessageList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<Unique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Unique::getSendId), userId)
            .or()
            .eq(MybatisPlusUtil.toColumns(Unique::getReceiveId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Unique::getCreateTime));
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(Unique::getUniqueId));
        queryWrapper.last("LIMIT 50");
        List<Unique> uniqueList = list(queryWrapper);

        if (CollectionUtil.isEmpty(uniqueList)) {
            return;
        }
        // 获取所有相关用户ID
        List<String> userIds = uniqueList.stream()
            .flatMap(unique -> {
                if (!unique.getSendId().equals(userId)) {
                    return Stream.of(unique.getSendId());
                } else {
                    return Stream.of(unique.getReceiveId());
                }
            }).distinct().collect(Collectors.toList());

        // 获取教师信息
        Map<String, Map<String, Object>> userMap = iAuthUserService.queryUserNameList(userIds);

        // 获取学生信息
        String userIdsStr = Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds);
        List<Map<String, Object>> studentList = iUserService.queryEntityMationByIds(userIdsStr);
        Map<String, Map<String, Object>> studentMap = studentList.stream()
            .collect(Collectors.toMap(
                student -> (String) student.get("id"),
                student -> student,
                (existing, replacement) -> existing
            ));

        // 获取最后一条聊天记录
        List<String> uniqueIds = uniqueList.stream().map(Unique::getUniqueId).collect(Collectors.toList());
        Map<String, List<ChatHistory>> queryLastChatHistory = chatHistoryService.queryLastChatHistory(uniqueIds);

        // 设置对方信息和最后一条消息
        for (Unique unique : uniqueList) {
            String otherUserId;
            if (unique.getSendId().equals(userId)) {
                otherUserId = unique.getReceiveId();
            } else {
                otherUserId = unique.getSendId();
            }

            // 获取对方信息（教师或学生）
            Map<String, Object> otherUserInfo = userMap.get(otherUserId);
            if (otherUserInfo == null) {
                otherUserInfo = studentMap.get(otherUserId);
            }

            // 获取最后一条消息及其发送者信息
            List<ChatHistory> lastChatHistory = queryLastChatHistory.get(unique.getUniqueId());
            ChatHistory lastMessage = lastChatHistory != null && !lastChatHistory.isEmpty() ? lastChatHistory.get(0) : null;
            unique.setOtherUserMation(otherUserInfo);
            unique.setLastMessage(lastMessage);
        }

        outputObject.setBeans(uniqueList);
        outputObject.settotal(uniqueList.size());
    }

    @Override
    public Unique quesyUniqueIsExist(String uniqueId) {
        QueryWrapper<Unique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Unique::getUniqueId), uniqueId);
        Unique unique = getOne(queryWrapper);
        return unique;
    }

    @Override
    public void deleteMyChatUniqueList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String uniqueId = inputObject.getParams().get("uniqueId").toString();
        QueryWrapper<Unique> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Unique::getUniqueId), uniqueId)
            .and(wrapper -> wrapper.eq(MybatisPlusUtil.toColumns(Unique::getSendId), userId)
                .or()
                .eq(MybatisPlusUtil.toColumns(Unique::getReceiveId), userId));
        boolean success = remove(queryWrapper);
        if (!success) {
            throw new CustomException("删除失败");
        }
    }

}

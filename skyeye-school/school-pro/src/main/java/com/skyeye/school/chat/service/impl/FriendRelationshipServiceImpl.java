/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.chat.dao.FriendRelationshipDao;
import com.skyeye.school.chat.entity.FriendRelationship;
import com.skyeye.school.chat.enums.ChatFriendType;
import com.skyeye.school.chat.service.FriendRelationshipService;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "好友关系", groupName = "好友关系")
public class FriendRelationshipServiceImpl extends SkyeyeBusinessServiceImpl<FriendRelationshipDao, FriendRelationship> implements FriendRelationshipService {

    @Autowired
    private SchoolCommonService schoolCommonService;

    @Override
    public void queryFriendsList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String keyword = commonPageInfo.getKeyword();
        String id = InputObject.getLogParamsStatic().get("id").toString();
        List<FriendRelationship> list = getFriendRelationships(id);
        List<FriendRelationship> filteredList = StrUtil.isNotEmpty(keyword)
            ? filterFriendRelationships(list, keyword)
            : list;

        // 手动分页处理
        int page = commonPageInfo.getPage();
        int limit = commonPageInfo.getLimit();
        int total = filteredList.size();
        int fromIndex = (page - 1) * limit;
        if (fromIndex >= total) {
            outputObject.setBeans(Collections.emptyList());
        } else {
            int toIndex = Math.min(fromIndex + limit, total);
            List<FriendRelationship> pageList = filteredList.subList(fromIndex, toIndex);
            outputObject.setBeans(pageList);
        }
        outputObject.settotal(total);
    }


    public static List<FriendRelationship> filterFriendRelationships(List<FriendRelationship> list, String keyword) {
        List<FriendRelationship> filteredList = new ArrayList<>();
        for (FriendRelationship friend : list) {
            if (friend.getStudentMation() != null && friend.getStudentMation().containsKey("name") &&
                friend.getStudentMation().get("name") != null &&
                friend.getStudentMation().get("name").toString().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(friend);
            } else if (friend.getTeacherMation() != null && friend.getTeacherMation().containsKey("userName") &&
                friend.getTeacherMation().get("userName") != null &&
                friend.getTeacherMation().get("userName").toString().toLowerCase().contains(keyword.toLowerCase())) {
                filteredList.add(friend);
            }
        }
        return filteredList;
    }

    @NotNull
    private List<FriendRelationship> getFriendRelationships(String id) {
        QueryWrapper<FriendRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(FriendRelationship::getCreateTime));
        queryWrapper.eq(MybatisPlusUtil.toColumns(FriendRelationship::getStatus), ChatFriendType.ACCEPTED.getIndex());
        queryWrapper.and(wrapper -> wrapper
            .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), id)
            .or()
            .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), id));
        List<FriendRelationship> list = list(queryWrapper);
        for (FriendRelationship item : list) {
            String remainingId;
            if (item.getUserId().equals(id)) {
                remainingId = item.getFriendId();
            } else {
                remainingId = item.getUserId();
            }
            UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(remainingId);
            if (userOrStudent.getUserOrStudent()) {
                item.setStudentMation(userOrStudent.getDataMation());
            } else {
                item.setTeacherMation(userOrStudent.getDataMation());
            }
        }
        return list;
    }

    @Override
    public void queryNoPageFriendsList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        List<FriendRelationship> list = getFriendRelationships(id);

        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void addFriendRelationship(String id, String applicantId, String recipientId, Integer status, String createId) {
        FriendRelationship friendRelationship = new FriendRelationship();
        friendRelationship.setUserId(applicantId);
        friendRelationship.setFriendId(recipientId);
        friendRelationship.setStatus(status);
        friendRelationship.setTalkRequestId(id);
        createEntity(friendRelationship, createId);
    }

    @Override
    public void changeFriendStatus(String userId, String status) {
        UpdateWrapper<FriendRelationship> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(FriendRelationship::getTalkRequestId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FriendRelationship::getStatus), status);
        update(updateWrapper);
    }

    @Override
    public FriendRelationship queryFriendRelationShip(String holderId, String friendId) {
        QueryWrapper<FriendRelationship> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper.and(wrapper ->
                wrapper.or(wrapperOr -> wrapperOr
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), holderId)
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), friendId))
                    .or(wrapperOr -> wrapperOr
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), holderId)
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), friendId)))
            .eq(MybatisPlusUtil.toColumns(FriendRelationship::getStatus), ChatFriendType.ACCEPTED.getIndex());
        return getOne(friendQueryWrapper, false);
    }

    @Override
    public void queryFriendByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String friendId = inputObject.getParams().get("userId").toString();
        Map<String, Object> dataMation = getAndCheckFriendShip(userId, friendId);
        outputObject.setBean(dataMation);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public Map<String, Object> getAndCheckFriendShip(String userId, String friendId) {
        UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(friendId);
        Map<String, Object> dataMation = userOrStudent.getDataMation();
        if (StrUtil.equals(userId, friendId)) {
            // 自己不能添加自己为好友
            dataMation.put("isFriend", true);
            return dataMation;
        }
        FriendRelationship friendRelationships = queryFriendRelationShip(userId, friendId);
        if (ObjectUtil.isNotEmpty(friendRelationships)) {
            dataMation.put("isFriend", true);
        } else {
            dataMation.put("isFriend", false);
        }
        return dataMation;
    }

}

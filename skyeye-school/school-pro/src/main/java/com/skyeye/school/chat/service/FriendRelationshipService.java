package com.skyeye.school.chat.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.FriendRelationship;

import java.util.List;

public interface FriendRelationshipService extends SkyeyeBusinessService<FriendRelationship> {
    void addFriendRelationship(String id, String applicantId, String recipientId, Integer status, String createId);

    void changeFriendStatus(String userId, String status);

    void queryNoPageFriendsList(InputObject inputObject, OutputObject outputObject);

    List<FriendRelationship> queryFriendList(String holderId,String s);
}

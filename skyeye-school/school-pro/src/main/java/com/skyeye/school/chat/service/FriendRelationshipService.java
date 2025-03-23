/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chat.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.chat.entity.FriendRelationship;

import java.util.List;

public interface FriendRelationshipService extends SkyeyeBusinessService<FriendRelationship> {
    void addFriendRelationship(String id, String applicantId, String recipientId, Integer status, String createId);

    void changeFriendStatus(String userId, String status);

    void queryNoPageFriendsList(InputObject inputObject, OutputObject outputObject);

    List<FriendRelationship> queryFriendList(String holderId, String s);

    void queryFriendByUserId(InputObject inputObject, OutputObject outputObject);
}

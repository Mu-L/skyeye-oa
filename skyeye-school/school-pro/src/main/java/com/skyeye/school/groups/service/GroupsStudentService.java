/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.groups.entity.GroupsStudent;

import java.util.List;
import java.util.Map;

public interface GroupsStudentService extends SkyeyeBusinessService<GroupsStudent> {
    void joinGroups(InputObject inputObject, OutputObject outputObject);

    Map<String, Boolean> checkStudentIsJoined(List<String> groupsIds, String studentNumber);

    void deleteByGroupsIds(List<String> groupsIds);

    List<Map<String, Object>> queryGroupsStudentsByGroupId(String groupId);

    Map<String, Integer> getStudentCountByGroupId(List<String> groupsIds);

    void exitGroups(InputObject inputObject, OutputObject outputObject);
}

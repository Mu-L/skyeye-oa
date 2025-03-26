package com.skyeye.school.groups.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.groups.entity.GroupsStudent;

import java.util.List;
import java.util.Map;

public interface GroupsStudentService extends SkyeyeBusinessService<GroupsStudent> {
    void joinGroups(InputObject inputObject, OutputObject outputObject);

    void selectGroupsByStuNumber(InputObject inputObject, OutputObject outputObject);

    List<Map<String ,Object>> selectAllStudent();
}

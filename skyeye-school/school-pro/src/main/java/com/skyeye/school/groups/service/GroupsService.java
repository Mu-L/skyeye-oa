package com.skyeye.school.groups.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.groups.entity.Groups;
import com.skyeye.school.groups.entity.GroupsInformation;
import com.skyeye.school.subject.entity.SubjectClassesStu;

import java.util.List;

public interface GroupsService extends SkyeyeBusinessService<Groups> {
    void insertList(GroupsInformation groupsInformation, List<SubjectClassesStu> allStudents);

    void deleteGroups(String groupsInformationId);

    QueryWrapper<Groups> selectByGroupsInformationId(String groupsInformationId);

    void deleteGroupsById(InputObject inputObject, OutputObject outputObject);

    void changeState(InputObject inputObject, OutputObject outputObject);
}

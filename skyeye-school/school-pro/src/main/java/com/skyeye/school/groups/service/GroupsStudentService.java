package com.skyeye.school.groups.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.groups.entity.GroupsStudent;

public interface GroupsStudentService extends SkyeyeBusinessService<GroupsStudent> {
    void joinGroups(InputObject inputObject, OutputObject outputObject);

    void selectGroupsByStuNumber(InputObject inputObject, OutputObject outputObject);

}

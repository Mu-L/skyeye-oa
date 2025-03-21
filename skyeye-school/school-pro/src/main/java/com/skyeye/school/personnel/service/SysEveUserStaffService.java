package com.skyeye.school.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.personnel.entity.SysEveUserStaff;

import java.util.List;
import java.util.Map;

public interface SysEveUserStaffService extends SkyeyeBusinessService<SysEveUserStaff> {
    List<SysEveUserStaff> selectByName(String name, String jobNumber);

    void querySysUserStaffByUserId(InputObject inputObject, OutputObject outputObject);

}

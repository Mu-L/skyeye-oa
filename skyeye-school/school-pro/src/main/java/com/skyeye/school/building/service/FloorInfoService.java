package com.skyeye.school.building.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.building.entity.FloorInfo;

public interface FloorInfoService extends SkyeyeBusinessService<FloorInfo> {
    void queryFloorInfosByLocationId(InputObject inputObject, OutputObject outputObject);
}

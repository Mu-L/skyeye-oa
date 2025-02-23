package com.skyeye.eve.multifllblank.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;

import java.util.List;
import java.util.Map;

public interface DwQuMultiFillblankService extends SkyeyeBusinessService<DwQuMultiFillblank> {
    void saveList(List<DwQuMultiFillblank> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    void removeByQuId(String quId);

    List<DwQuMultiFillblank> selectQuMultiFillblank(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);
}

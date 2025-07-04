package com.skyeye.school.lectures.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;

import java.util.List;

public interface LecturesAttenanceRecoredChildService extends SkyeyeBusinessService<LecturesAttenanceRecoredChild> {

    List<LecturesAttenanceRecoredChild> queryChildByAttenanceRecordId(String id);

    void deleteChildByAttenanceRecordId(String id);
}

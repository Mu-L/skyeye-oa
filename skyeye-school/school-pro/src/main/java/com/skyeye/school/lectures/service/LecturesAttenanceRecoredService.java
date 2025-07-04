package com.skyeye.school.lectures.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;

public interface LecturesAttenanceRecoredService extends SkyeyeBusinessService<LecturesAttenanceRecored> {

    LecturesAttenanceRecored queryByAttenanceRecordId(String attenanceRecordId);

}

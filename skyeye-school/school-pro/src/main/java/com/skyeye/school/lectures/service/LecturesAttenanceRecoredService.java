package com.skyeye.school.lectures.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecored;

import java.util.List;

public interface LecturesAttenanceRecoredService extends SkyeyeBusinessService<LecturesAttenanceRecored> {

    List<LecturesAttenanceRecored> queryByAttenanceRecordId(String attenanceRecordId);

    void deleteAttenanceRecoredByReviewModelId(String id);

    List<LecturesAttenanceRecored> getByReviewModelId(String reviewModelId);
}

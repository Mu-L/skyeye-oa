package com.skyeye.school.lectures.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;

import java.util.List;

public interface LecturesAttenanceRecoredChildService extends SkyeyeBusinessService<LecturesAttenanceRecoredChild> {

     List<LecturesAttenanceRecoredChild> queryChildByAttenanceRecordId(String attenanceRecordId);

     void deleteChildByAttenanceRecordId(String id);

     void deleteChildByAttenanceRecordIdList(List<String> idList);

    List<LecturesAttenanceRecoredChild> queryChildByAttenanceRecordIds(List<String> lecturesAttenanceRecoredIds);

}

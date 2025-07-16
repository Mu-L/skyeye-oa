package com.skyeye.school.lectures.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.lectures.entity.LecturesAttenanceRecoredChild;

import java.util.List;
import java.util.Map;

public interface LecturesAttenanceRecoredChildService extends SkyeyeBusinessService<LecturesAttenanceRecoredChild> {

//     Map<String, List<LecturesAttenanceRecoredChild>> queryChildByAttenanceRecordId(List<String> attenanceRecordIds) ;

     List<LecturesAttenanceRecoredChild> queryChildByAttenanceRecordId(String attenanceRecordId);

     void deleteChildByAttenanceRecordId(String id);

     void deleteChildByAttenanceRecordIdList(List<String> idList);

}

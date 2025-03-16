package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;

public interface ScoreTypeService extends SkyeyeBusinessService<ScoreType> {
//    void updateScoreTypeList(InputObject inputObject, OutputObject outputObject);

    void querySameTableDateList(InputObject inputObject, OutputObject outputObject);

    void queryDifferentTableDateList(InputObject inputObject, OutputObject outputObject);

    void createDeFaultInfo(SubjectClasses subjectClasses, String userId);
}

package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreType;
import com.skyeye.school.subject.entity.SubjectClasses;

import java.util.List;

public interface ScoreTypeService extends SkyeyeBusinessService<ScoreType> {

    void querySameTableDateList(InputObject inputObject, OutputObject outputObject);

    void queryDifferentTableDateList(InputObject inputObject, OutputObject outputObject);

    void createDeFaultInfo(SubjectClasses subjectClasses, String userId);

    void writeScoreTypeList(InputObject inputObject, OutputObject outputObject);

    List<ScoreType> queryList(String subjectId,String ClassId);

    ScoreType queryDefaultInfo(String subjectId, String classId);

    List<ScoreType> queryNotDefaultInfo(String subjectId, String classId);

    void queryBySubjectIdAndClassesId(InputObject inputObject, OutputObject outputObject);
}

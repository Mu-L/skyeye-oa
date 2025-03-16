package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.subject.entity.SubjectClasses;

import java.util.List;

public interface ScoreTypeChildService extends SkyeyeBusinessService<ScoreTypeChild> {
    List<ScoreTypeChild> queryListByParentIdList(List<String> list);

    ScoreTypeChild queryByTypeId(String typeId);

    void createDeFaultInfo(SubjectClasses subjectClasses);

    void boundData(InputObject inputObject, OutputObject outputObject);

    void changeProportion(InputObject inputObject, OutputObject outputObject);

    List<ScoreTypeChild> queryListBySubjectIdAndClassId(String subjectId, String classId);

    void deleteByTypeId(String typeId);
}

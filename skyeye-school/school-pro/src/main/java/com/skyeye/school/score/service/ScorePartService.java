package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScorePart;

import java.util.List;

public interface ScorePartService extends SkyeyeBusinessService<ScorePart> {
    List<ScorePart> queryByObjectIdList(List<String> scoreTypeIdList, String stuNo);

    void updateScorePartByStuNoAndWorkId(String stuNo, String workId, String score);

    void updateScorePart(InputObject inputObject, OutputObject outputObject);

    void updateScoreByObjectIdAndStuNo(String parentId, double parentPartScore, String stuNo);

    void deleteByObjectId(String objectId);

    void createScorePart(InputObject inputObject, OutputObject outputObject);
    void midCourse(String stuNo, String subjectId, String classId);

    void createScorePartByWorkId(String objectId, String workId);
}

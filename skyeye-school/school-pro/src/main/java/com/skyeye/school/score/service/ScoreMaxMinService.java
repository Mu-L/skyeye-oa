package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.score.entity.ScoreMaxMin;

public interface ScoreMaxMinService extends SkyeyeBusinessService<ScoreMaxMin> {
    void createDeFaultInfo(String subjectId, String classId, String currentUserId);

    ScoreMaxMin getScoreMaxMinBySubjectIdAndClassId(String subjectId, String classId);
}

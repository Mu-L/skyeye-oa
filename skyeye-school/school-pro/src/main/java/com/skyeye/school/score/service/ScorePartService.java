package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.ScorePart;

import java.util.List;

public interface ScorePartService extends SkyeyeBusinessService<ScorePart> {
    List<ScorePart> queryByObjectIdList(List<String> scoreTypeIdList);

    void updateScorePart(InputObject inputObject, OutputObject outputObject);

    void updateScoreByObjectIdAndStuNo(String parentId, int parentPartScore, String stuNo);
}

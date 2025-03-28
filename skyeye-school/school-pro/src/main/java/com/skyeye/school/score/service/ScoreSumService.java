package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.score.entity.ScorePart;
import com.skyeye.school.score.entity.ScoreSum;

import java.util.List;
import java.util.Map;

public interface ScoreSumService extends SkyeyeBusinessService<ScoreSum> {
    List<ScoreSum> queryByObjectIdList(List<String> scoreTypeIdList);

    void updateScoreByObjectIdAndStuNo(String objectId, double sumScore, String stuNo);

    List<ScoreSum> queryByObjectIdListAndStuNo(List<String> objectIdList, String stuNo);

    void deleteByObjectId(String objectId);

    void updateProportionByObjectId(String objectId, String proportion);

    Map<String, String> getStuNoScoreSumMap(Map<String, List<ScoreSum>> collect);
}

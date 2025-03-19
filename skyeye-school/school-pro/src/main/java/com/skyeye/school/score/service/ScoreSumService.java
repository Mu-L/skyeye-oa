package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.score.entity.ScoreSum;

import java.util.List;

public interface ScoreSumService extends SkyeyeBusinessService<ScoreSum> {
    List<ScoreSum> queryByObjectIdList(List<String> scoreTypeIdList);

    void updataScoreByObjectIdAndStuNo(String objectId, int sumScore, String stuNo);

    List<ScoreSum> queryByObjectIdListAndStuNo(List<String> objectIdList, String stuNo);
}

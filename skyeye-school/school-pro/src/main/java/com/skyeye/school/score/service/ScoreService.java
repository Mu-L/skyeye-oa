package com.skyeye.school.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.school.score.entity.Score;

import java.util.Map;

public interface ScoreService extends SkyeyeBusinessService<Score> {
    void queryMyScoreListByNo(InputObject inputObject, OutputObject outputObject);

    void queryScoreList(InputObject inputObject, OutputObject outputObject);
}

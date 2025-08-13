package com.skyeye.eve.score.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.score.entity.DwQuScore;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DwQuScoreService
 * @Description: 评分题行选项管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface DwQuScoreService extends SkyeyeBusinessService<DwQuScore> {

    void saveList(List<DwQuScore> score, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    List<DwQuScore> selectQuScore(String copyFromId);

    Map<String, List<DwQuScore>> selectByBelongId(List<String> id);

    void removeByQuId(String quId);

    void updateScores(List<DwQuestion> dwQuestionList, String userId);

    void removeByQuIds(List<String> dwQuestionIds);

    void removeByquId(String entityId);

    List<DwQuScore> createScores(List<DwQuestion> dwQuestionList, String userId);

    List<DwQuScore> selectByQuId(String id);
}


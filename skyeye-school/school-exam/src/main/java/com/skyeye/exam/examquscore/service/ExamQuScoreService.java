package com.skyeye.exam.examquscore.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.exam.examquscore.entity.ExamQuScore;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ExamQuScoreService
 * @Description: 评分题行选项管理服务接口层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ExamQuScoreService extends SkyeyeBusinessService<ExamQuScore> {
    void saveList(List<ExamQuScore> list, String quId, String userId);

    void changeVisibility(InputObject inputObject, OutputObject outputObject);

    List<ExamQuScore> selectQuScore(String copyFromId);

    Map<String, List<Map<String, Object>>> selectByBelongId(String id);
}


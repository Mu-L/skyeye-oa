/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.school.score.dao.ScoreMaxMinDao;
import com.skyeye.school.score.entity.ScoreMaxMin;
import com.skyeye.school.score.service.ScoreMaxMinService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: ScoreMaxMinServiceImpl
 * @Description: 班级最高分和最低分管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "班级最高分和最低分记录管理", groupName = "班级最高分和最低分记录管理")
public class ScoreMaxMinServiceImpl extends SkyeyeBusinessServiceImpl<ScoreMaxMinDao, ScoreMaxMin> implements ScoreMaxMinService {

    @Override
    public void createDeFaultInfo(String subjectId, String classId, String currentUserId) {
        ScoreMaxMin scoreMaxMin = new ScoreMaxMin();
        scoreMaxMin.setSubjectId(subjectId);
        scoreMaxMin.setClassId(classId);
        scoreMaxMin.setMaxScore(CommonNumConstants.NUM_ZERO.toString());
        scoreMaxMin.setMinScore(CommonNumConstants.NUM_ZERO.toString());
        super.createEntity(scoreMaxMin, currentUserId);
    }

    @Override
    public void updateScoreById(String id, String scoreMax, String scoreMin, String currentUserId) {
        ScoreMaxMin scoreMaxMin = super.selectById(id);
        scoreMaxMin.setMaxScore(scoreMax);
        scoreMaxMin.setMinScore(scoreMin);
        super.updateEntity(scoreMaxMin, currentUserId);
    }

    @Override
    public void updateScoreById(String id, String score, String currentUserId) {
        ScoreMaxMin scoreMaxMin = super.selectById(id);
        if (Double.parseDouble(CalculationUtil.subtract(score, scoreMaxMin.getMaxScore())) > 0) {
            scoreMaxMin.setMaxScore(score);
        }
        if (Double.parseDouble(CalculationUtil.subtract(scoreMaxMin.getMinScore(), score)) > 0) {
            scoreMaxMin.setMinScore(score);
        }
        super.updateEntity(scoreMaxMin, currentUserId);
    }
}

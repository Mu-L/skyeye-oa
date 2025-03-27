package com.skyeye.school.score.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.score.dao.ScoreMaxMinDao;
import com.skyeye.school.score.entity.ScoreMaxMin;
import com.skyeye.school.score.service.ScoreMaxMinService;
import org.springframework.stereotype.Service;

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
    public ScoreMaxMin getScoreMaxMinBySubjectIdAndClassId(String subjectId, String classId) {
        QueryWrapper<ScoreMaxMin> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreMaxMin::getSubjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(ScoreMaxMin::getClassId), classId);
        return getOne(queryWrapper);
    }
}

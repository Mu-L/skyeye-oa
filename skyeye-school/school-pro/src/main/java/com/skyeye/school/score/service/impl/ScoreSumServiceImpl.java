package com.skyeye.school.score.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.score.dao.ScoreSumDao;
import com.skyeye.school.score.entity.ScoreSum;
import com.skyeye.school.score.service.ScoreSumService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "总成绩管理", groupName = "总成绩管理")
public class ScoreSumServiceImpl extends SkyeyeBusinessServiceImpl<ScoreSumDao, ScoreSum> implements ScoreSumService {
    @Override
    public List<ScoreSum> queryByObjectIdList(List<String> scoreTypeIdList) {
        QueryWrapper<ScoreSum> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreSum::getObjectId), scoreTypeIdList);
        return list(queryWrapper);
    }

    @Override
    public void updataScoreByObjectIdAndStuNo(String objectId, int sumScore, String stuNo) {
        UpdateWrapper<ScoreSum> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ScoreSum::getObjectId), objectId)
            .eq(MybatisPlusUtil.toColumns(ScoreSum::getStuNo), stuNo)
            .set(MybatisPlusUtil.toColumns(ScoreSum::getScore), sumScore);
        update(updateWrapper);
    }

    @Override
    public List<ScoreSum> queryByObjectIdListAndStuNo(List<String> objectIdList, String stuNo) {
        QueryWrapper<ScoreSum> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreSum::getObjectId), objectIdList);
        if (StrUtil.isNotEmpty(stuNo)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreSum::getStuNo), stuNo);
        }
        return list(queryWrapper);
    }
}

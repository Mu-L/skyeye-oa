/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.score.dao.ScoreSumDao;
import com.skyeye.school.score.entity.ScoreSum;
import com.skyeye.school.score.service.ScoreSumService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ScoreSumServiceImpl
 * @Description: 总成绩管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/29 10:53
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "总成绩管理", groupName = "总成绩管理")
public class ScoreSumServiceImpl extends SkyeyeBusinessServiceImpl<ScoreSumDao, ScoreSum> implements ScoreSumService {
    @Override
    public List<ScoreSum> queryByObjectIdList(List<String> scoreTypeIdList) {
        QueryWrapper<ScoreSum> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreSum::getObjectId), scoreTypeIdList)
            .orderByDesc(MybatisPlusUtil.toColumns(ScoreSum::getStuNo));
        return list(queryWrapper);
    }

    @Override
    public void updateScoreByObjectIdAndStuNo(String objectId, double sumScore, String stuNo) {
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

    @Override
    public void deleteByObjectId(String objectId) {
        QueryWrapper<ScoreSum> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ScoreSum::getObjectId), objectId);
        remove(queryWrapper);
    }

    @Override
    public void updateProportionByObjectId(String objectId, String proportion) {
        UpdateWrapper<ScoreSum> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ScoreSum::getObjectId), objectId)
            .set(MybatisPlusUtil.toColumns(ScoreSum::getProportion), proportion);
        update(updateWrapper);
    }

    @Override
    public Map<String, String> getStuNoScoreSumMap(Map<String, List<ScoreSum>> collect){
        Map<String, String> map = new HashMap<>();
        collect.forEach((stuNo, scoreSumList) -> {
            final double[] newSum = {CommonNumConstants.NUM_ZERO};
            for (ScoreSum scoreSum : scoreSumList) {
                String flagSum = CalculationUtil.multiply(scoreSum.getScore(), CalculationUtil.divide(scoreSum.getProportion(), "100"), CommonNumConstants.NUM_TWO);
                newSum[CommonNumConstants.NUM_ZERO] = newSum[CommonNumConstants.NUM_ZERO] + Double.parseDouble(flagSum);
            }
            map.put(stuNo, String.valueOf(newSum[CommonNumConstants.NUM_ZERO]));
        });
        return map;
    }

    @Override
    public void deleteByObjectIdList(List<String> objectIdList) {
        if (CollectionUtil.isEmpty(objectIdList)){
            return;
        }
        QueryWrapper<ScoreSum> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ScoreSum::getObjectId), objectIdList);
        remove(queryWrapper);
    }
}

package com.skyeye.eve.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.score.dao.DwAnScoreDao;
import com.skyeye.eve.score.entity.DwAnScore;
import com.skyeye.eve.score.service.DwAnScoreService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "评分题答卷管理", groupName = "评分题答卷管理")
public class DwAnScoreServiceImpl extends SkyeyeBusinessServiceImpl<DwAnScoreDao, DwAnScore> implements DwAnScoreService {
    @Override
    public List<DwAnScore> selectAnScoreByQuId(String id) {
        QueryWrapper<DwAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnScore::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnScore> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnScore::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public void queryDwAnScoreListById(InputObject inputObject, OutputObject outputObject) {
        String examAnScoreId = inputObject.getParams().get("id").toString();
        QueryWrapper<DwAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examAnScoreId);
        List<DwAnScore> dwAnScoreList = list(queryWrapper);
        outputObject.setBean(dwAnScoreList);
        outputObject.settotal(dwAnScoreList.size());
    }

    @Override
    public Map<String, List<DwAnScore>> selectByQuIdAndStuId(List<String> scoreIds, String studentId) {
        if (CollectionUtil.isEmpty(scoreIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnScore::getQuId), scoreIds)
            .eq(MybatisPlusUtil.toColumns(DwAnScore::getCreateId), studentId);
        Map<String, List<DwAnScore>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnScore::getQuId));
        return stringListMap;
    }
}

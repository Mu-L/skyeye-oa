package com.skyeye.eve.score.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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
import java.util.Set;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "评分题答卷管理", groupName = "评分题答卷管理")
public class DwAnScoreServiceImpl extends SkyeyeBusinessServiceImpl<DwAnScoreDao, DwAnScore> implements DwAnScoreService {

    @Override
    protected void createPostpose(DwAnScore examAnOrder, String userId) {
        List<DwAnScore> dFillblankAn = examAnOrder.getScoreAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(DwAnScore entity, String userId) {
        List<DwAnScore> chenCheckboxAn = entity.getScoreAn();
        QueryWrapper<DwAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID,  entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnScore::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnScore::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnScore::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnScore> examAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnScore> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnScore> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnScore::getId).collect(Collectors.toSet());
        List<DwAnScore> result = examAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnScore::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(TodeleteIds)) {
            deleteById(TodeleteIds);
        }
        if (CollectionUtil.isNotEmpty(NoIdChenCheckbox)) {
            super.createEntity(NoIdChenCheckbox, userId);
        }
        if (CollectionUtil.isNotEmpty(YesIdChenCheckbox)) {
            super.updateEntity(YesIdChenCheckbox, userId);
        }
    }

    @Override
    public DwAnScore selectById(String id) {
        DwAnScore examAnDfillblank = super.selectById(id);
        String belongAnswerId = examAnDfillblank.getBelongAnswerId();
        String belongId = examAnDfillblank.getBelongId();
        String quId = examAnDfillblank.getQuId();
        QueryWrapper<DwAnScore> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnScore::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnScore::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnScore::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        examAnDfillblank.setScoreAn(list(queryWrapper1));
        return examAnDfillblank;
    }

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
    public Map<String, List<DwAnScore>> selectByQuIdAndStuId(List<String> scoreIds, String studentId, String id) {
        if (CollectionUtil.isEmpty(scoreIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwAnScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnScore::getQuId), scoreIds)
            .eq(MybatisPlusUtil.toColumns(DwAnScore::getCreateId), studentId)
            .eq(MybatisPlusUtil.toColumns(DwAnScore::getBelongAnswerId), id);
        Map<String, List<DwAnScore>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnScore::getQuId));
        return stringListMap;
    }
}

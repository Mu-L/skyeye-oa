package com.skyeye.eve.multifllblank.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.multifllblank.dao.DwAnFillblankDao;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;
import com.skyeye.eve.multifllblank.entity.DwAnFillblank;
import com.skyeye.eve.multifllblank.service.DwAnFillblankService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "答卷填空题保存表", groupName = "答卷填空题保存表")
public class DwAnFillblankServiceImpl extends SkyeyeBusinessServiceImpl<DwAnFillblankDao, DwAnFillblank> implements DwAnFillblankService {

    @Override
    public void queryDwAnFillblankListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnFillblank> dwAnFillblankList = list(queryWrapper);

        outputObject.setBean(dwAnFillblankList);
        outputObject.settotal(dwAnFillblankList.size());
    }

    @Override
    protected void createPrepose(DwAnFillblank entity) {
        QueryWrapper<DwAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnFillblank::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnFillblank::getBelongAnswerId), entity.getBelongAnswerId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnFillblank::getBelongId), entity.getBelongId());
        List<DwAnFillblank> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            remove(queryWrapper);
        }
    }

    @Override
    public List<DwAnFillblank> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnFillblank::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnFillblank> selectAnFillblankQuId(String id) {
        QueryWrapper<DwAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnFillblank::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnFillblank>> selectByQuIdAndStuId(List<String> multifillblankIds, String studentId, String id) {
        if (CollectionUtil.isEmpty(multifillblankIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwAnFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnFillblank::getQuId), multifillblankIds)
            .eq(MybatisPlusUtil.toColumns(DwAnFillblank::getCreateId), studentId)
            .eq(MybatisPlusUtil.toColumns(DwAnFillblank::getBelongAnswerId), id);
        Map<String, List<DwAnFillblank>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnFillblank::getQuId));
        return stringListMap;
    }
}

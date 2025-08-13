package com.skyeye.eve.yesno.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.score.entity.DwAnScore;
import com.skyeye.eve.yesno.dao.DwAnYesnoDao;
import com.skyeye.eve.yesno.entity.DwAnYesno;
import com.skyeye.eve.yesno.service.DwAnYesnoService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@SkyeyeService(name = "判断题答卷管理", groupName = "判断题答卷管理")
public class DwAnYesnoServiceImpl extends SkyeyeBusinessServiceImpl<DwAnYesnoDao, DwAnYesno> implements DwAnYesnoService {
    @Override
    public void queryDwAnYesnoListById(InputObject inputObject, OutputObject outputObject) {
        String examAnYesnoId = inputObject.getParams().get("id").toString();
        QueryWrapper<DwAnYesno> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, examAnYesnoId);
        List<DwAnYesno> dwAnYesnoList = list(queryWrapper);
        outputObject.setBean(dwAnYesnoList);
        outputObject.settotal(dwAnYesnoList.size());
    }

    @Override
    protected void createPrepose(DwAnYesno entity) {
        QueryWrapper<DwAnYesno> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnYesno::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnYesno::getBelongAnswerId), entity.getBelongAnswerId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnYesno::getBelongId), entity.getBelongId());
        List<DwAnYesno> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            remove(queryWrapper);
        }
    }

    @Override
    public List<DwAnYesno> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnYesno> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnYesno::getBelongId), surveyId);
        return list(queryWrapper);
    }
}

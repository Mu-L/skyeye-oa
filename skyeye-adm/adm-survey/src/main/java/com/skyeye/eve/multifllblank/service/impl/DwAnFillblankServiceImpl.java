package com.skyeye.eve.multifllblank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.multifllblank.dao.DwAnFillblankDao;
import com.skyeye.eve.multifllblank.entity.DwAnFillblank;
import com.skyeye.eve.multifllblank.service.DwAnFillblankService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "填空题保存表", groupName = "填空题保存表")
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
}

package com.skyeye.eve.multifllblank.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.multifllblank.dao.DwAnDfillblankDao;
import com.skyeye.eve.multifllblank.entity.DwAnDfillblank;
import com.skyeye.eve.multifllblank.service.DwAnDfillblankService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "答卷 多行填空题保存表", groupName = "答卷 多行填空题保存表")
public class DwAnDfillblankServiceImpl extends SkyeyeBusinessServiceImpl<DwAnDfillblankDao,DwAnDfillblank> implements DwAnDfillblankService {
    @Override
    public void queryDwAnDfillblankById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnDfillblank> dwAnDfillblankList = list(queryWrapper);
        outputObject.setBean(dwAnDfillblankList);
        outputObject.settotal(dwAnDfillblankList.size());
    }

    @Override
    protected void validatorEntity(DwAnDfillblank entity) {
        super.validatorEntity(entity);
        System.out.println("fsjiodjiko");
    }

    @Override
    protected void createPrepose(DwAnDfillblank entity) {
        System.out.println("dsqwwd");
    }

    @Override
    public List<DwAnDfillblank> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnDfillblank> selectAnDfillblankQuId(String id) {
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getQuId), id);
        return list(queryWrapper);
    }

}

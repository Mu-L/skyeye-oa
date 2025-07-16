package com.skyeye.eve.multifllblank.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "答卷多行填空题保存表", groupName = "答卷多行填空题保存表")
public class DwAnDfillblankServiceImpl extends SkyeyeBusinessServiceImpl<DwAnDfillblankDao, DwAnDfillblank> implements DwAnDfillblankService {

    @Override
    protected void createPrepose(DwAnDfillblank entity) {
        List<DwAnDfillblank> dFillblankAn = entity.getDFillblankAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, InputObject.getLogParamsStatic().get("id").toString());
        }
    }

    @Override
    protected void updatePostpose(DwAnDfillblank entity, String userId) {
        List<DwAnDfillblank> chenCheckboxAn = entity.getDFillblankAn();
        String id = entity.getId();
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID, id);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnDfillblank> examAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnDfillblank> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnDfillblank> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnDfillblank::getId).collect(Collectors.toSet());
        List<DwAnDfillblank> result = examAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnDfillblank::getId).collect(Collectors.toList());
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
    public List<DwAnDfillblank> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public DwAnDfillblank selectById(String id) {
        DwAnDfillblank examAnDfillblank = super.selectById(id);
        String belongAnswerId = examAnDfillblank.getBelongAnswerId();
        String belongId = examAnDfillblank.getBelongId();
        String quId = examAnDfillblank.getQuId();
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getBelongAnswerId), belongAnswerId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getBelongId), belongId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getQuId), quId);
        queryWrapper.ne(CommonConstants.ID, id);
        examAnDfillblank.setDFillblankAn(list(queryWrapper));
        return examAnDfillblank;
    }

    @Override
    public List<DwAnDfillblank> selectAnDfillblankQuId(String id) {
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnDfillblank>> selectByQuIdAndStuId(List<String> multifillblankIds, String studentId) {
        if (CollectionUtil.isEmpty(multifillblankIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwAnDfillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnDfillblank::getQuId), multifillblankIds)
            .eq(MybatisPlusUtil.toColumns(DwAnDfillblank::getCreateId), studentId);
        Map<String, List<DwAnDfillblank>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnDfillblank::getQuId));
        return stringListMap;
    }

}

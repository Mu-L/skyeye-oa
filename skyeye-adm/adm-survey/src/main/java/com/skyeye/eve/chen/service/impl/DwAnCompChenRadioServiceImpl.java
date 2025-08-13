package com.skyeye.eve.chen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwAnCompChenRadioDao;
import com.skyeye.eve.chen.entity.DwAnChenScore;
import com.skyeye.eve.chen.entity.DwAnCompChenRadio;
import com.skyeye.eve.chen.service.DwAnCompChenRadioService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnCompChenRadioServiceImpl
 * @Description: 答卷复合矩阵单选题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷复合矩阵单选题", groupName = "答卷复合矩阵单选题")
public class DwAnCompChenRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwAnCompChenRadioDao, DwAnCompChenRadio> implements DwAnCompChenRadioService {

    @Override
    protected void createPostpose(DwAnCompChenRadio entity, String userId) {
        List<DwAnCompChenRadio> dFillblankAn = entity.getDwCompChenRadioAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void createPrepose(DwAnCompChenRadio entity) {
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongAnswerId), entity.getBelongAnswerId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongId), entity.getBelongId());
        List<DwAnCompChenRadio> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            remove(queryWrapper);
        }
    }

    @Override
    protected void updatePostpose(DwAnCompChenRadio entity, String userId) {
        List<DwAnCompChenRadio> chenCheckboxAn = entity.getDwCompChenRadioAn();
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID, entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnCompChenRadio> dwAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnCompChenRadio> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnCompChenRadio> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnCompChenRadio::getId).collect(Collectors.toSet());
        List<DwAnCompChenRadio> result = dwAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnCompChenRadio::getId).collect(Collectors.toList());
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
    public DwAnCompChenRadio selectById(String id) {
        DwAnCompChenRadio dwAnChenCheckbox = super.selectById(id);
        String belongAnswerId = dwAnChenCheckbox.getBelongAnswerId();
        String belongId = dwAnChenCheckbox.getBelongId();
        String quId = dwAnChenCheckbox.getQuId();
        QueryWrapper<DwAnCompChenRadio> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        dwAnChenCheckbox.setDwCompChenRadioAn(list(queryWrapper1));
        return dwAnChenCheckbox;
    }


    @Override
    public void queryDwAnCompChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnCompChenRadio> dwAnCompChenRadioList = list(queryWrapper);
        outputObject.setBean(dwAnCompChenRadioList);
        outputObject.settotal(dwAnCompChenRadioList.size());
    }

    @Override
    public List<DwAnCompChenRadio> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnCompChenRadio> selectByQuId(String id) {
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnCompChenRadio>> selectByQuIdAndStuId(List<String> chenIds, String studentId, String id) {
        if (CollectionUtil.isEmpty(chenIds)){
            return new HashMap<>();
        }
        QueryWrapper<DwAnCompChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getQuId), chenIds)
            .eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getCreateId), studentId)
            .eq(MybatisPlusUtil.toColumns(DwAnCompChenRadio::getBelongAnswerId), id);
        Map<String, List<DwAnCompChenRadio>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnCompChenRadio::getQuId));
        return stringListMap;
    }

}

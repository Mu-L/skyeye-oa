/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

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
import com.skyeye.eve.chen.dao.DwAnChenRadioDao;
import com.skyeye.eve.chen.entity.DwAnChenRadio;
import com.skyeye.eve.chen.service.DwAnChenRadioService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnChenRadioServiceImpl
 * @Description: 答卷矩阵单选题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷矩阵单选题", groupName = "答卷矩阵单选题")
public class DwAnChenRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwAnChenRadioDao, DwAnChenRadio> implements DwAnChenRadioService {

    @Override
    protected void createPostpose(DwAnChenRadio entity, String userId) {
        List<DwAnChenRadio> dFillblankAn = entity.getDwChenRadioAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(DwAnChenRadio entity, String userId) {
        List<DwAnChenRadio> chenCheckboxAn = entity.getDwChenRadioAn();
        QueryWrapper<DwAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID, entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnChenRadio> dwAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnChenRadio> NoIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnChenRadio> YesIdChenCheckbox = chenCheckboxAn.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnChenRadio::getId).collect(Collectors.toSet());
        List<DwAnChenRadio> result = dwAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnChenRadio::getId).collect(Collectors.toList());
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
    public void queryDwAnChenRadioListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnChenRadio> dwAnChenRadioList = list(queryWrapper);
        outputObject.setBean(dwAnChenRadioList);
        outputObject.settotal(dwAnChenRadioList.size());
    }

    @Override
    public List<DwAnChenRadio> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public DwAnChenRadio selectById(String id) {
        DwAnChenRadio dwAnChenCheckbox = super.selectById(id);
        String belongAnswerId = dwAnChenCheckbox.getBelongAnswerId();
        String belongId = dwAnChenCheckbox.getBelongId();
        String quId = dwAnChenCheckbox.getQuId();
        QueryWrapper<DwAnChenRadio> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        dwAnChenCheckbox.setDwChenRadioAn(list(queryWrapper1));
        return dwAnChenCheckbox;
    }

    @Override
    public List<DwAnChenRadio> selectByQuId(String id) {
        QueryWrapper<DwAnChenRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenRadio::getQuId), id);
        return list(queryWrapper);
    }

}
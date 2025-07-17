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
import com.skyeye.eve.chen.dao.DwAnChenFbkDao;
import com.skyeye.eve.chen.entity.DwAnChenFbk;
import com.skyeye.eve.chen.service.DwAnChenFbkService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnChenFbkServiceImpl
 * @Description: 答卷矩阵填空题服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷矩阵填空题", groupName = "答卷矩阵填空题")
public class DwAnChenFbkServiceImpl extends SkyeyeBusinessServiceImpl<DwAnChenFbkDao, DwAnChenFbk> implements DwAnChenFbkService {

    @Override
    protected void createPostpose(DwAnChenFbk entity, String userId) {
        List<DwAnChenFbk> dFillblankAn = entity.getDwChenFbkAn();
        if (CollectionUtil.isNotEmpty(dFillblankAn)) {
            super.createEntity(dFillblankAn, userId);
        }
    }

    @Override
    protected void updatePostpose(DwAnChenFbk entity, String userId) {
        List<DwAnChenFbk> chenFbkList = entity.getDwChenFbkAn();
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(CommonConstants.ID, entity.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getBelongId), entity.getBelongId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getQuId), entity.getQuId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getBelongAnswerId), entity.getBelongAnswerId());
        List<DwAnChenFbk> dwAnChenCheckboxList = list(queryWrapper);//数据库数据
        List<DwAnChenFbk> NoIdChenCheckbox = chenFbkList.stream().filter(
            e -> StrUtil.isEmpty(e.getId())).collect(Collectors.toList());//id为空的数据
        List<DwAnChenFbk> YesIdChenCheckbox = chenFbkList.stream().filter(
            e -> StrUtil.isNotEmpty(e.getId())).collect(Collectors.toList());//id不为空的数据
        Set<String> yesIdSet = YesIdChenCheckbox.stream().map(DwAnChenFbk::getId).collect(Collectors.toSet());
        List<DwAnChenFbk> result = dwAnChenCheckboxList.stream().filter(
            e -> !yesIdSet.contains(e.getId())).collect(Collectors.toList());
        List<String> TodeleteIds = result.stream().map(DwAnChenFbk::getId).collect(Collectors.toList());
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
    public void queryDwAnChenFbkListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnChenFbk> dwAnChenFbkList = list(queryWrapper);
        outputObject.setBean(dwAnChenFbkList);
        outputObject.settotal(dwAnChenFbkList.size());
    }

    @Override
    public List<DwAnChenFbk> selectBySurveyId(String surveyId) {
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public List<DwAnChenFbk> selectByQuId(String id) {
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getQuId), id);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnChenFbk>> selectByQuIdAndStuId(List<String> chenIds, String studentId, String id) {
        if (CollectionUtil.isEmpty(chenIds)){
            return new HashMap<>();
        }
        QueryWrapper<DwAnChenFbk> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnChenFbk::getQuId), chenIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getCreateId), studentId)
            .eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getBelongAnswerId), id);
        Map<String, List<DwAnChenFbk>> stringListMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnChenFbk::getQuId));
        return stringListMap;
    }

    @Override
    public DwAnChenFbk selectById(String id) {
        DwAnChenFbk dwAnChenCheckbox = super.selectById(id);
        String belongAnswerId = dwAnChenCheckbox.getBelongAnswerId();
        String belongId = dwAnChenCheckbox.getBelongId();
        String quId = dwAnChenCheckbox.getQuId();
        QueryWrapper<DwAnChenFbk> queryWrapper1 = new QueryWrapper<>();
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getBelongAnswerId), belongAnswerId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getBelongId), belongId);
        queryWrapper1.eq(MybatisPlusUtil.toColumns(DwAnChenFbk::getQuId), quId);
        queryWrapper1.ne(CommonConstants.ID, id);
        dwAnChenCheckbox.setDwChenFbkAn(list(queryWrapper1));
        return dwAnChenCheckbox;
    }

}














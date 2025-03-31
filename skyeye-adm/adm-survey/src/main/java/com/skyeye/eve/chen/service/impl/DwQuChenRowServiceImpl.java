/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwQuChenRowDao;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.chen.service.DwQuChenRowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DwQuChenRowServiceImpl
 * @Description: 矩陈题行选项服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "矩陈题行选项", groupName = "矩陈题行选项", manageShow = false)
public class DwQuChenRowServiceImpl extends SkyeyeBusinessServiceImpl<DwQuChenRowDao, DwQuChenRow> implements DwQuChenRowService {

    @Autowired
    private DwQuChenRowService dwQuChenRowService;

    @Override
    public void saveRowEntity(List<DwQuChenRow> quRow, String userId) {
        createEntity(quRow, userId);
    }

    @Override
    public void updateRowEntity(List<DwQuChenRow> editquRow, String userId) {
        updateEntity(editquRow, userId);
    }

    @Override
    public QueryWrapper<DwQuChenRow> QueryExamQuChenRowList(String quId) {
        QueryWrapper<DwQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), quId);
        return queryWrapper;
    }

    @Override
    public Integer QueryvisibilityInRow(String quId, String createId) {
        QueryWrapper<DwQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), quId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenRow::getCreateId), createId);
        DwQuChenRow one = dwQuChenRowService.getOne(queryWrapper);
        return one.getVisibility();
    }

    @Override
    public void changeVisibility(String quId, String createId) {
        UpdateWrapper<DwQuChenRow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), quId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenRow::getCreateId), createId);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuChenRow::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuChenRow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuChenRow> selectQuChenRow(String copyFromId) {
        QueryWrapper<DwQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuChenRow::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuChenRow>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), id);
        List<DwQuChenRow> list = list(queryWrapper);
        Map<String, List<DwQuChenRow>> result = list.stream().collect(Collectors.groupingBy(DwQuChenRow::getQuId));
        return result;
    }

}

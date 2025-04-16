/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwQuChenRowDao;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.chen.service.DwQuChenRowService;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.radio.entity.DwQuRadio;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
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
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuRadio::getOrderById));
        List<DwQuChenRow> list = list(queryWrapper);
        Map<String, List<DwQuChenRow>> result = list.stream().collect(Collectors.groupingBy(DwQuChenRow::getQuId));
        return result;
    }

    @Override
    public void createChenRows(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuChenRow> insertList = new ArrayList<>();
        List<DwQuChenRow> updateList = new ArrayList<>();
        Map<String, List<DwQuChenRow>> quRadioMap = new HashMap<>();

        for (DwQuestion dwQuestion : dwQuestionList) {
            String quId = dwQuestion.getId();
            List<DwQuChenRow> radios = dwQuestion.getRowTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (DwQuChenRow radio : radios) {
                DwQuChenRow bean = new DwQuChenRow();
                BeanUtil.copyProperties(radio, bean);
                if (ToolUtil.isBlank(radio.getOptionId())) {
                    bean.setQuId(quId);
                    bean.setVisibility(1);
                    bean.setCreateId(userId);
                    bean.setCreateTime(DateUtil.getTimeAndToString());
                    insertList.add(bean);
                } else {
                    bean.setId(bean.getOptionId());
                    updateList.add(bean);
                }
            }
        }

        if (CollectionUtil.isNotEmpty(insertList)) {
            super.createEntity(insertList, userId);
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            super.updateEntity(updateList, userId);
        }
    }

    @Override
    public void updateChenRow(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuChenRow> insertList = new ArrayList<>();
        List<DwQuChenRow> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<DwQuChenRow>> existingRadiosMap = loadExistingRadios(dwQuestionList);

        for (DwQuestion dwQuestion : dwQuestionList) {
            List<DwQuChenRow> radios = dwQuestion.getRowTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = dwQuestion.getId();
            List<DwQuChenRow> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(DwQuChenRow::getOptionId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(DwQuChenRow::getId)
                .filter(id -> !newIds.contains(id))
                .forEach(needDeleteIds::add);

            // 处理插入/更新
            processRadioOptions(radios, quId, userId, insertList, updateList);
        }
        List<String> needDeleteIdList = new ArrayList<>(needDeleteIds);
        // 批量数据库操作
        if (!needDeleteIds.isEmpty()) {
            deleteById(needDeleteIdList);
        }
        createChenRows(dwQuestionList, userId);
    }

    @Override
    public void removeByQuIds(List<String> dwQuestionIds) {
        QueryWrapper<DwQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), dwQuestionIds);
        remove(queryWrapper);
    }


    private Map<String, List<DwQuChenRow>> loadExistingRadios(List<DwQuestion> dwQuestions) {
        List<String> quIds = dwQuestions.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(DwQuChenRow::getQuId));
    }

    private List<DwQuChenRow> selectByQuIds(List<String> quIds) {
        QueryWrapper<DwQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuChenRow::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<DwQuChenRow> radios, String quId,
                                     String userId, List<DwQuChenRow> insertList,
                                     List<DwQuChenRow> updateList) {
        for (DwQuChenRow radio : radios) {
            DwQuChenRow bean = new DwQuChenRow();
            BeanUtil.copyProperties(radio, bean);

            if (ToolUtil.isBlank(radio.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                insertList.add(bean);
            } else {
                bean.setId(bean.getOptionId());
                updateList.add(bean);
            }
        }
    }
}

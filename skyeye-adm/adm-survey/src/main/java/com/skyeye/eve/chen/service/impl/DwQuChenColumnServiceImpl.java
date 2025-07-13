package com.skyeye.eve.chen.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.chen.dao.DwQuChenColumnDao;
import com.skyeye.eve.chen.entity.DwQuChenColumn;
import com.skyeye.eve.chen.entity.DwQuChenRow;
import com.skyeye.eve.chen.service.DwQuChenColumnService;
import com.skyeye.eve.chen.service.DwQuChenRowService;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.radio.entity.DwQuRadio;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnCompChenRadioServiceImpl
 * @Description: 矩陈题列选项服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "矩陈题-列选项管理", groupName = "矩陈题-列选项管理", manageShow = false)
public class DwQuChenColumnServiceImpl extends SkyeyeBusinessServiceImpl<DwQuChenColumnDao, DwQuChenColumn> implements DwQuChenColumnService {

    @Autowired
    private DwQuChenRowService dwQuChenRowService;

    @Override
    protected QueryWrapper<DwQuChenColumn> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuChenColumn> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), commonPageInfo.getHolderId());
            dwQuChenRowService.QueryExamQuChenRowList(commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuChenColumn> column, List<DwQuChenRow> row, String quId, String userId) {
        List<DwQuChenColumn> quColumn = new ArrayList<>();
        List<DwQuChenColumn> editquColumn = new ArrayList<>();
        for (int i = 0; i < column.size(); i++) {
            DwQuChenColumn object = column.get(i);
            DwQuChenColumn bean = new DwQuChenColumn();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quColumn.add(bean);
            } else {
                bean.setId(object.getId());
                editquColumn.add(bean);
            }
        }
        if (!quColumn.isEmpty()) {
            createEntity(quColumn, userId);

        }
        if (!editquColumn.isEmpty()) {
            updateEntity(editquColumn, userId);
        }

        List<DwQuChenRow> quRow = new ArrayList<>();
        List<DwQuChenRow> editquRow = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            DwQuChenRow object = row.get(i);
            DwQuChenRow bean = new DwQuChenRow();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRow.add(bean);
            } else {
                bean.setId(object.getId());
                editquRow.add(bean);
            }
        }
        if (!quRow.isEmpty()) {
            dwQuChenRowService.saveRowEntity(quRow, userId);
        }
        if (!editquRow.isEmpty()) {
            dwQuChenRowService.updateRowEntity(editquRow, userId);
        }
    }

//    @Override
//    protected void deletePreExecution(DwQuChenColumn entity) {
//        String createId = entity.getCreateId();
//        String quId = entity.getQuId();
//        Integer queryvisibility = dwQuChenRowService.QueryvisibilityInRow(quId, createId);
////        Integer visibility = entity.getVisibility();
////        if (visibility.equals(CommonNumConstants.NUM_ONE) && queryvisibility.equals(CommonNumConstants.NUM_ONE)) {
////            throw new CustomException("该选项已显示，请先隐藏再删除");
////        }
//    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String quId = map.get("quId").toString();
        String createId = map.get("createId").toString();
        dwQuChenRowService.changeVisibility(quId, createId);
        UpdateWrapper<DwQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuChenColumn::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void createChenColumns(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuChenColumn> insertList = new ArrayList<>();
        List<DwQuChenColumn> updateList = new ArrayList<>();
        Map<String, List<DwQuChenColumn>> quRadioMap = new HashMap<>();

        for (DwQuestion dwQuestion : dwQuestionList) {
            String quId = dwQuestion.getId();
            List<DwQuChenColumn> radios = dwQuestion.getColumnTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (DwQuChenColumn radio : radios) {
                DwQuChenColumn bean = new DwQuChenColumn();
                BeanUtil.copyProperties(radio, bean);
                if (ToolUtil.isBlank(radio.getId())) {
                    bean.setQuId(quId);
                    bean.setVisibility(1);
                    bean.setCreateId(userId);
                    bean.setCreateTime(DateUtil.getTimeAndToString());
                    insertList.add(bean);
                } else {
                    bean.setId(bean.getId());
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
        dwQuChenRowService.createChenRows(dwQuestionList, userId);
    }

    @Override
    public void removeByQuId(String quId) {
        dwQuChenRowService.removeByQuId(quId);
        UpdateWrapper<DwQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuChenColumn> selectQuChenColumn(String copyFromId) {
        QueryWrapper<DwQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuChenColumn::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuChenColumn>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), id);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuRadio::getOrderById));
        List<DwQuChenColumn> list = list(queryWrapper);
        Map<String, List<DwQuChenColumn>> result = list.stream().collect(Collectors.groupingBy(DwQuChenColumn::getQuId));
        return result;
    }

    @Override
    public void updateChenColumn(List<DwQuestion> dwQuestionList, String userId) {
        dwQuChenRowService.updateChenRow(dwQuestionList, userId);
        List<DwQuChenColumn> insertList = new ArrayList<>();
        List<DwQuChenColumn> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<DwQuChenColumn>> existingRadiosMap = loadExistingRadios(dwQuestionList);

        for (DwQuestion dwQuestion : dwQuestionList) {
            List<DwQuChenColumn> radios = dwQuestion.getColumnTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = dwQuestion.getId();
            List<DwQuChenColumn> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(DwQuChenColumn::getId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(DwQuChenColumn::getId)
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
        createChenColumns(dwQuestionList, userId);
    }

    @Override
    public void removeByQuIds(List<String> dwQuestionIds) {
        dwQuChenRowService.removeByQuIds(dwQuestionIds);
        QueryWrapper<DwQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), dwQuestionIds);
        remove(queryWrapper);
    }

    private Map<String, List<DwQuChenColumn>> loadExistingRadios(List<DwQuestion> dwQuestions) {
        List<String> quIds = dwQuestions.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(DwQuChenColumn::getQuId));
    }

    private List<DwQuChenColumn> selectByQuIds(List<String> quIds) {
        QueryWrapper<DwQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuChenColumn::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<DwQuChenColumn> radios, String quId,
                                     String userId, List<DwQuChenColumn> insertList,
                                     List<DwQuChenColumn> updateList) {
        for (DwQuChenColumn radio : radios) {
            DwQuChenColumn bean = new DwQuChenColumn();
            BeanUtil.copyProperties(radio, bean);

            if (ToolUtil.isBlank(radio.getId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                insertList.add(bean);
            } else {
                bean.setId(bean.getId());
                updateList.add(bean);
            }
        }
    }
}

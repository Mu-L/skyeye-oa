package com.skyeye.eve.radio.service.impl;

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
import com.skyeye.common.util.question.CheckType;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.radio.dao.DwQuRadioDao;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.radio.service.DwQuRadioService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DwQuRadioServiceImpl
 * @Description: 单选题选项服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "单选题选项", groupName = "单选题选项")
public class DwQuRadioServiceImpl extends SkyeyeBusinessServiceImpl<DwQuRadioDao, DwQuRadio> implements DwQuRadioService {

    @Autowired
    private DwQuRadioService dwQuRadioService;

    @Override
    protected QueryWrapper<DwQuRadio> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuRadio> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuRadio> list, String quId, String userId) {
        List<DwQuRadio> quRadio = new ArrayList<>();
        List<DwQuRadio> editquRadio = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DwQuRadio object = list.get(i);
            DwQuRadio bean = new DwQuRadio();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setIsNote(object.getIsNote());
            bean.setOptionTitle(object.getOptionTitle());
            bean.setIsDefaultAnswer(object.getIsDefaultAnswer());
            if (object.getCheckType() != null) {
                if (!ToolUtil.isNumeric(object.getCheckType().toString())) {
                    bean.setCheckType(CheckType.valueOf(object.getCheckType().toString()).getIndex());
                } else {
                    bean.setCheckType(object.getCheckType());
                }
            }
            bean.setIsRequiredFill(object.getIsRequiredFill());
            if (ToolUtil.isBlank(object.getId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRadio.add(bean);
            } else {
                bean.setId(object.getId());
                editquRadio.add(bean);
            }
        }
        if (CollectionUtils.isNotEmpty(quRadio)) {
            dwQuRadioService.createEntity(quRadio, userId);
        }
        if (CollectionUtils.isNotEmpty(editquRadio)) {
            dwQuRadioService.updateEntity(editquRadio, userId);
        }
    }

//    @Override
//    protected void deletePreExecution(DwQuRadio entity) {
//        Integer visibility = entity.getVisibility();
//        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
//            throw new CustomException("该选项已显示，请先隐藏再删除");
//        }
//    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuRadio::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    public void createRadios(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuRadio> insertList = new ArrayList<>();
        List<DwQuRadio> updateList = new ArrayList<>();
        Map<String, List<DwQuRadio>> quRadioMap = new HashMap<>();

        for (DwQuestion dwQuestion : dwQuestionList) {
            String quId = dwQuestion.getId();
            List<DwQuRadio> radios = dwQuestion.getRadioTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (DwQuRadio radio : radios) {
                DwQuRadio bean = new DwQuRadio();
                BeanUtil.copyProperties(radio, bean);
                if (radio.getCheckType() != null && !ToolUtil.isNumeric(radio.getCheckType().toString())) {
                    bean.setCheckType(CheckType.valueOf(radio.getCheckType().toString()).getIndex());
                }
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
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuRadio> selectQuRadio(String copyFromId) {
        QueryWrapper<DwQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuRadio::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuRadio>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), id);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuRadio::getOrderById));
        List<DwQuRadio> list = list(queryWrapper);
        Map<String, List<DwQuRadio>> result = list.stream().collect(Collectors.groupingBy(DwQuRadio::getQuId));
        return result;
    }

    @Override
    public void updateRadios(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuRadio> insertList = new ArrayList<>();
        List<DwQuRadio> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<DwQuRadio>> existingRadiosMap = loadExistingRadios(dwQuestionList);

        for (DwQuestion dwQuestion : dwQuestionList) {
            List<DwQuRadio> radios = dwQuestion.getRadioTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = dwQuestion.getId();
            List<DwQuRadio> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(DwQuRadio::getId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(DwQuRadio::getId)
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
        createRadios(dwQuestionList, userId);

    }

    @Override
    public void removeByQuIds(List<String> dwQuestionIds) {
        QueryWrapper<DwQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), dwQuestionIds);
        remove(queryWrapper);
    }

    private Map<String, List<DwQuRadio>> loadExistingRadios(List<DwQuestion> dwQuestions) {
        List<String> quIds = dwQuestions.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(DwQuRadio::getQuId));
    }

    private List<DwQuRadio> selectByQuIds(List<String> quIds) {
        QueryWrapper<DwQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuRadio::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<DwQuRadio> radios, String quId,
                                     String userId, List<DwQuRadio> insertList,
                                     List<DwQuRadio> updateList) {
        for (DwQuRadio radio : radios) {
            DwQuRadio bean = new DwQuRadio();
            BeanUtil.copyProperties(radio, bean);

            // CheckType转换逻辑
            if (radio.getCheckType() != null && !ToolUtil.isNumeric(radio.getCheckType().toString())) {
                bean.setCheckType(CheckType.valueOf(radio.getCheckType().toString()).getIndex());
            }

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


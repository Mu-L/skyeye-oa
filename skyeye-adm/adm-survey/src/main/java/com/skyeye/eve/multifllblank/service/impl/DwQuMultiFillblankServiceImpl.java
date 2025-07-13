package com.skyeye.eve.multifllblank.service.impl;

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
import com.skyeye.eve.multifllblank.dao.DwQuMultiFillblankDao;
import com.skyeye.eve.multifllblank.entity.DwQuMultiFillblank;
import com.skyeye.eve.multifllblank.service.DwQuMultiFillblankService;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.radio.entity.DwQuRadio;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "多行填空题选项管理", groupName = "多行填空题选项管理")
public class DwQuMultiFillblankServiceImpl extends SkyeyeBusinessServiceImpl<DwQuMultiFillblankDao, DwQuMultiFillblank> implements DwQuMultiFillblankService {

    @Override
    protected QueryWrapper<DwQuMultiFillblank> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuMultiFillblank> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuMultiFillblank> list, String quId, String userId) {
        List<DwQuMultiFillblank> quMultiFillblank = new ArrayList<>();
        List<DwQuMultiFillblank> editquMultiFillblank = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DwQuMultiFillblank object = list.get(i);
            DwQuMultiFillblank bean = new DwQuMultiFillblank();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            if (StrUtil.isNotEmpty(object.getIsDefaultAnswer())) {
                bean.setIsDefaultAnswer(object.getIsDefaultAnswer());
            }
            if (ToolUtil.isBlank(object.getId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quMultiFillblank.add(bean);
            } else {
                bean.setId(object.getId());
                editquMultiFillblank.add(bean);
            }
        }
        if (!quMultiFillblank.isEmpty()) {
            createEntity(quMultiFillblank, userId);
        }
        if (!editquMultiFillblank.isEmpty()) {
            updateEntity(editquMultiFillblank, userId);
        }
    }

//    @Override
//    protected void deletePreExecution(DwQuMultiFillblank entity) {
//        Integer visibility = entity.getVisibility();
//        if (visibility.equals(CommonNumConstants.NUM_ONE)) {
//            throw new CustomException("该选项已显示，请先隐藏再删除");
//        }
//    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void createMultiFillblanks(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuMultiFillblank> insertList = new ArrayList<>();
        List<DwQuMultiFillblank> updateList = new ArrayList<>();
        Map<String, List<DwQuMultiFillblank>> quRadioMap = new HashMap<>();

        for (DwQuestion dwQuestion : dwQuestionList) {
            String quId = dwQuestion.getId();
            List<DwQuMultiFillblank> radios = dwQuestion.getMultifillblankTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (DwQuMultiFillblank radio : radios) {
                DwQuMultiFillblank bean = new DwQuMultiFillblank();
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

        if (!insertList.isEmpty()) {
            createEntity(insertList, userId);
        }
        if (!updateList.isEmpty()) {
            updateEntity(updateList, userId);
        }
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuMultiFillblank> selectQuMultiFillblank(String copyFromId) {
        QueryWrapper<DwQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuMultiFillblank>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId), id);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuRadio::getOrderById));
        List<DwQuMultiFillblank> list = list(queryWrapper);
        Map<String, List<DwQuMultiFillblank>> result = list.stream().collect(Collectors.groupingBy(DwQuMultiFillblank::getQuId));
        return result;
    }

    @Override
    public void updateMultiFillblanks(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuMultiFillblank> insertList = new ArrayList<>();
        List<DwQuMultiFillblank> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<DwQuMultiFillblank>> existingRadiosMap = loadExistingRadios(dwQuestionList);

        for (DwQuestion dwQuestion : dwQuestionList) {
            List<DwQuMultiFillblank> radios = dwQuestion.getMultifillblankTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = dwQuestion.getId();
            List<DwQuMultiFillblank> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(DwQuMultiFillblank::getId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(DwQuMultiFillblank::getId)
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
        createMultiFillblanks(dwQuestionList, userId);
    }

    @Override
    public void removeByQuIds(List<String> dwQuestionIds) {
        QueryWrapper<DwQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId), dwQuestionIds);
        remove(queryWrapper);
    }


    private Map<String, List<DwQuMultiFillblank>> loadExistingRadios(List<DwQuestion> dwQuestions) {
        List<String> quIds = dwQuestions.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(DwQuMultiFillblank::getQuId));
    }

    private List<DwQuMultiFillblank> selectByQuIds(List<String> quIds) {
        QueryWrapper<DwQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuMultiFillblank::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<DwQuMultiFillblank> radios, String quId,
                                     String userId, List<DwQuMultiFillblank> insertList,
                                     List<DwQuMultiFillblank> updateList) {
        for (DwQuMultiFillblank radio : radios) {
            DwQuMultiFillblank bean = new DwQuMultiFillblank();
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

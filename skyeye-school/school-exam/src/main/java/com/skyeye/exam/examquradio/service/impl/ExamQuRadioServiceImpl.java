package com.skyeye.exam.examquradio.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.exam.examquradio.dao.ExamQuRadioDao;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import com.skyeye.exam.examquradio.service.ExamQuRadioService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamQuRadioServiceImpl
 * @Description: 单选题选项表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "单选题选项表管理", groupName = "单选题选项表管理")
public class ExamQuRadioServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuRadioDao, ExamQuRadio> implements ExamQuRadioService {

    @Override
    protected QueryWrapper<ExamQuRadio> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuRadio> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuRadio> list, String quId, String userId) {
        List<ExamQuRadio> quRadio = new ArrayList<>();
        List<ExamQuRadio> editquRadio = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExamQuRadio object = list.get(i);
            ExamQuRadio bean = new ExamQuRadio();
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
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRadio.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquRadio.add(bean);
            }
        }
        if (!quRadio.isEmpty()) {
            createEntity(quRadio, userId);
        }
        if (!editquRadio.isEmpty()) {
            updateEntity(editquRadio, userId);
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuRadio::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuRadio> selectQuRadio(String copyFromId) {
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuRadio::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getBelongId), id);
        List<ExamQuRadio> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item -> {
            String quId = item.getQuId();
            if (result.containsKey(quId)) {
                result.get(quId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            } else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(quId, tmp);
            }
        });
        return result;
    }

    @Override
    public void deleteByQuestionId(String entityId) {
        UpdateWrapper<ExamQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), entityId);
        remove(updateWrapper);
    }

    @Override
    public Map<String, List<ExamQuRadio>> selectByQuestionIds(List<String> questionIdList) {
        if (questionIdList.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), questionIdList);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuRadio::getOrderById));
        List<ExamQuRadio> list = list(queryWrapper);
        Map<String, List<ExamQuRadio>> collect = list.stream().collect(Collectors.groupingBy(ExamQuRadio::getQuId));
        return collect;
    }

    @Override
    public void createRadios(List<Question> questionList, String userId) {
        List<ExamQuRadio> insertList = new ArrayList<>();
        List<ExamQuRadio> updateList = new ArrayList<>();
        Map<String, List<ExamQuRadio>> quRadioMap = new HashMap<>();

        for (Question question : questionList) {
            String quId = question.getId();
            List<ExamQuRadio> radios = question.getRadioTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (ExamQuRadio radio : radios) {
                ExamQuRadio bean = new ExamQuRadio();
                BeanUtil.copyProperties(radio, bean);
                if (radio.getCheckType() != null && !ToolUtil.isNumeric(radio.getCheckType().toString())) {
                    bean.setCheckType(CheckType.valueOf(radio.getCheckType().toString()).getIndex());
                }
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
    public void removeByQuIds(List<String> questionIds) {
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), questionIds);
        remove(queryWrapper);
    }

    @Override
    public void updateRadios(List<Question> questionList, String userId) {
        List<ExamQuRadio> insertList = new ArrayList<>();
        List<ExamQuRadio> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<ExamQuRadio>> existingRadiosMap = loadExistingRadios(questionList);

        for (Question question : questionList) {
            List<ExamQuRadio> radios = question.getRadioTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = question.getId();
            List<ExamQuRadio> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(ExamQuRadio::getOptionId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(ExamQuRadio::getId)
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
        createRadios(questionList, userId);

    }


    private Map<String, List<ExamQuRadio>> loadExistingRadios(List<Question> questions) {
        List<String> quIds = questions.stream()
            .map(Question::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(ExamQuRadio::getQuId));
    }

    private List<ExamQuRadio> selectByQuIds(List<String> quIds) {
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<ExamQuRadio> radios, String quId,
                                     String userId, List<ExamQuRadio> insertList,
                                     List<ExamQuRadio> updateList) {
        for (ExamQuRadio radio : radios) {
            ExamQuRadio bean = new ExamQuRadio();
            BeanUtil.copyProperties(radio, bean);

            // CheckType转换逻辑
            if (radio.getCheckType() != null && !ToolUtil.isNumeric(radio.getCheckType().toString())) {
                bean.setCheckType(CheckType.valueOf(radio.getCheckType().toString()).getIndex());
            }

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

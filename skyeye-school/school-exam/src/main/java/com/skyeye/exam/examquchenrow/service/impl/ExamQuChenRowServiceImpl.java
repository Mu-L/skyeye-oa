package com.skyeye.exam.examquchenrow.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examquestion.entity.Question;
import com.skyeye.exam.examquchenrow.dao.ExamQuChenRowDao;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;
import com.skyeye.exam.examquchenrow.service.ExamQuChenRowService;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "矩陈题-行选项管理", groupName = "矩陈题-行选项管理")
public class ExamQuChenRowServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuChenRowDao, ExamQuChenRow> implements ExamQuChenRowService {

    @Autowired
    private ExamQuChenRowService examQuChenRowService;

    @Override
    public void saveRowEntity(List<ExamQuChenRow> quRow, String userId) {
        createEntity(quRow, userId);
    }

    @Override
    public void updateRowEntity(List<ExamQuChenRow> editquRow, String userId) {
        updateEntity(editquRow, userId);
    }

    @Override
    public QueryWrapper<ExamQuChenRow> QueryExamQuChenRowList(String quId) {
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quId);
        return queryWrapper;
    }

    @Override
    public void changeVisibility(String quId, String createId) {
        UpdateWrapper<ExamQuChenRow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getCreateId), createId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuChenRow::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuChenRow> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuChenRow> selectQuChenRow(String copyFromId) {
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuChenRow::getOrderBy));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenRow::getBelongId), id);
        List<ExamQuChenRow> list = list(queryWrapper);
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
    public Map<String, List<ExamQuChenRow>> selectByQuestionIds(List<String> questionIdList) {
        if (questionIdList.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), questionIdList);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuRadio::getOrderById));
        List<ExamQuChenRow> list = list(queryWrapper);
        Map<String, List<ExamQuChenRow>> collect = list.stream().collect(Collectors.groupingBy(ExamQuChenRow::getQuId));
        return collect;
    }

    @Override
    public void removeByQuIds(List<String> questionIds) {
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), questionIds);
        remove(queryWrapper);
    }

    @Override
    public void updateChenRow(List<Question> questionList, String userId) {
        List<ExamQuChenRow> insertList = new ArrayList<>();
        List<ExamQuChenRow> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<ExamQuChenRow>> existingRadiosMap = loadExistingRadios(questionList);

        for (Question question : questionList) {
            List<ExamQuChenRow> radios = question.getRowTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = question.getId();
            List<ExamQuChenRow> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(ExamQuChenRow::getOptionId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(ExamQuChenRow::getId)
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
        createChenRows(questionList, userId);
    }

    @Override
    public void createChenRows(List<Question> questionList, String userId) {
        List<ExamQuChenRow> insertList1 = new ArrayList<>();
        List<ExamQuChenRow> updateList1 = new ArrayList<>();
        Map<String, List<ExamQuChenRow>> quRadioMap1 = new HashMap<>();

        for (Question question : questionList) {
            String quId = question.getId();
            List<ExamQuChenRow> rowTd = question.getRowTd();
            if (CollectionUtils.isEmpty(rowTd)) continue;
            quRadioMap1.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(rowTd);
            for (ExamQuChenRow radio : rowTd) {
                ExamQuChenRow bean = new ExamQuChenRow();
                BeanUtil.copyProperties(radio, bean);
                if (ToolUtil.isBlank(radio.getOptionId())) {
                    bean.setQuId(quId);
                    bean.setVisibility(1);
                    bean.setCreateId(userId);
                    bean.setCreateTime(DateUtil.getTimeAndToString());
                    insertList1.add(bean);
                } else {
                    bean.setId(bean.getOptionId());
                    updateList1.add(bean);
                }
            }
        }

        if (CollectionUtil.isNotEmpty(insertList1)) {
            super.createEntity(insertList1, userId);
        }
        if (CollectionUtil.isNotEmpty(updateList1)) {
            super.updateEntity(updateList1, userId);
        }
    }

    private Map<String, List<ExamQuChenRow>> loadExistingRadios(List<Question> questions) {
        List<String> quIds = questions.stream()
            .map(Question::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(ExamQuChenRow::getQuId));
    }

    private List<ExamQuChenRow> selectByQuIds(List<String> quIds) {
        QueryWrapper<ExamQuChenRow> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuChenRow::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<ExamQuChenRow> radios, String quId,
                                     String userId, List<ExamQuChenRow> insertList,
                                     List<ExamQuChenRow> updateList) {
        for (ExamQuChenRow radio : radios) {
            ExamQuChenRow bean = new ExamQuChenRow();
            BeanUtil.copyProperties(radio, bean);

            if (ToolUtil.isBlank(radio.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                insertList.add(bean);
            } else {
                updateList.add(bean);
            }
        }
    }
}

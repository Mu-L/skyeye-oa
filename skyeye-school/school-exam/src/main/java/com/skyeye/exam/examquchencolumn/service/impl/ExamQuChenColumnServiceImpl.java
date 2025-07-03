package com.skyeye.exam.examquchencolumn.service.impl;

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
import com.skyeye.exam.examquestion.entity.Question;
import com.skyeye.exam.examquchencolumn.dao.ExamQuChenColumnDao;
import com.skyeye.exam.examquchencolumn.entity.ExamQuChenColumn;
import com.skyeye.exam.examquchencolumn.service.ExamQuChenColumnService;
import com.skyeye.exam.examquchenrow.entity.ExamQuChenRow;
import com.skyeye.exam.examquchenrow.service.ExamQuChenRowService;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "矩陈题-列选项管理", groupName = "矩陈题-列选项管理")
public class ExamQuChenColumnServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuChenColumnDao, ExamQuChenColumn> implements ExamQuChenColumnService {

    @Autowired
    private ExamQuChenRowService examQuChenRowService;

    @Override
    protected QueryWrapper<ExamQuChenColumn> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuChenColumn> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), commonPageInfo.getHolderId());
            examQuChenRowService.QueryExamQuChenRowList(commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuChenColumn> column, List<ExamQuChenRow> row, String quId, String userId) {
        List<ExamQuChenColumn> quColumn = new ArrayList<>();
        List<ExamQuChenColumn> editquColumn = new ArrayList<>();
        for (int i = 0; i < column.size(); i++) {
            ExamQuChenColumn object = column.get(i);
            ExamQuChenColumn bean = new ExamQuChenColumn();
            bean.setOrderById(object.getOrderById());
            bean.setOrderBy(object.getOrderBy());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quColumn.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquColumn.add(bean);
            }
        }
        if (!quColumn.isEmpty()) {
            createEntity(quColumn, userId);

        }
        if (!editquColumn.isEmpty()) {
            updateEntity(editquColumn, userId);
        }

        List<ExamQuChenRow> quRow = new ArrayList<>();
        List<ExamQuChenRow> editquRow = new ArrayList<>();
        for (int i = 0; i < row.size(); i++) {
            ExamQuChenRow object = row.get(i);
            ExamQuChenRow bean = new ExamQuChenRow();
            bean.setOrderById(object.getOrderById());
            bean.setOrderBy(object.getOrderBy());
            bean.setOptionName(object.getOptionName());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRow.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquRow.add(bean);
            }
        }
        if (!quRow.isEmpty()) {
            examQuChenRowService.saveRowEntity(quRow, userId);
        }
        if (!editquRow.isEmpty()) {
            examQuChenRowService.updateRowEntity(editquRow, userId);
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String quId = map.get("quId").toString();
        String createId = map.get("createId").toString();
        examQuChenRowService.changeVisibility(quId, createId);
        UpdateWrapper<ExamQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuChenColumn::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        examQuChenRowService.removeByQuId(quId);
        UpdateWrapper<ExamQuChenColumn> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuChenColumn> selectQuChenColumn(String copyFromId) {
        QueryWrapper<ExamQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuChenColumn::getOrderBy));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuChenColumn::getBelongId), id);
        List<ExamQuChenColumn> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item->{
            String quId = item.getQuId();
            if(result.containsKey(quId)){
                result.get(quId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            }else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(quId,tmp);
            }
        });
        return result;
    }

    @Override
    public Map<String, List<ExamQuChenColumn>> selectByQuestionIds(List<String> questionIdList) {
        if (questionIdList.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), questionIdList);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuRadio::getOrderById));
        List<ExamQuChenColumn> list = list(queryWrapper);
        Map<String, List<ExamQuChenColumn>> collect = list.stream().collect(Collectors.groupingBy(ExamQuChenColumn::getQuId));
        return collect;
    }

    @Override
    public void createChenColumns(List<Question> questionList, String userId) {
        List<ExamQuChenColumn> insertList = new ArrayList<>();
        List<ExamQuChenColumn> updateList = new ArrayList<>();
        Map<String, List<ExamQuChenColumn>> quRadioMap = new HashMap<>();

        for (Question question : questionList) {
            String quId = question.getId();
            List<ExamQuChenColumn> radios = question.getColumnTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (ExamQuChenColumn radio : radios) {
                ExamQuChenColumn bean = new ExamQuChenColumn();
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
            super.createEntity(insertList,userId);
        }
        if (CollectionUtil.isNotEmpty(updateList)) {
            super.updateEntity(updateList,userId);
        }
        examQuChenRowService.createChenRows(questionList,userId);
    }

    @Override
    public void removeByQuIds(List<String> questionIds) {
        examQuChenRowService.removeByQuIds(questionIds);
        QueryWrapper<ExamQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), questionIds);
        remove(queryWrapper);
    }

    @Override
    public void updateChenColumn(List<Question> questionList, String userId) {
        examQuChenRowService.updateChenRow(questionList, userId);
        List<ExamQuChenColumn> insertList = new ArrayList<>();
        List<ExamQuChenColumn> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<ExamQuChenColumn>> existingRadiosMap = loadExistingRadios(questionList);

        for (Question question : questionList) {
            List<ExamQuChenColumn> radios = question.getColumnTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = question.getId();
            List<ExamQuChenColumn> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(ExamQuChenColumn::getOptionId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(ExamQuChenColumn::getId)
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
        createChenColumns(questionList, userId);
    }
    private Map<String, List<ExamQuChenColumn>> loadExistingRadios(List<Question> questions) {
        List<String> quIds = questions.stream()
            .map(Question::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(ExamQuChenColumn::getQuId));
    }

    private List<ExamQuChenColumn> selectByQuIds(List<String> quIds) {
        QueryWrapper<ExamQuChenColumn> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuChenColumn::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<ExamQuChenColumn> radios, String quId,
                                     String userId, List<ExamQuChenColumn> insertList,
                                     List<ExamQuChenColumn> updateList) {
        for (ExamQuChenColumn radio : radios) {
            ExamQuChenColumn bean = new ExamQuChenColumn();
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

package com.skyeye.exam.examquorderby.service.impl;

import cn.hutool.core.bean.BeanUtil;
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
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.exam.examquorderby.dao.ExamQuOrderbyDao;
import com.skyeye.exam.examquorderby.entity.ExamQuOrderby;
import com.skyeye.exam.examquorderby.service.ExamQuOrderbyService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排序题行选项管理", groupName = "排序题行选项管理")
public class ExamQuOrderbyServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuOrderbyDao, ExamQuOrderby> implements ExamQuOrderbyService {

    @Override
    protected QueryWrapper<ExamQuOrderby> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuOrderby> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuOrderby> score, String quId, String userId) {
        List<ExamQuOrderby> quOrderBy = new ArrayList<>();
        List<ExamQuOrderby> editquOrderBy = new ArrayList<>();
        for (int i = 0; i < score.size(); i++) {
            ExamQuOrderby object = score.get(i);
            ExamQuOrderby bean = new ExamQuOrderby();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quOrderBy.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquOrderBy.add(bean);
            }
        }
        if (!quOrderBy.isEmpty()) {
            createEntity(quOrderBy, userId);
        }
        if (!editquOrderBy.isEmpty()) {
            updateEntity(editquOrderBy, userId);
        }
        quOrderBy.addAll(editquOrderBy);
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuOrderby::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuOrderby> selectQuOrderby(String copyFromId) {
        QueryWrapper<ExamQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuOrderby::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuOrderby::getBelongId), id);
        List<ExamQuOrderby> list = list(queryWrapper);
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
    public Map<String, List<ExamQuOrderby>> selectByQuestionIds(List<String> questionIdList) {
        if (questionIdList.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), questionIdList);
        List<ExamQuOrderby> list = list(queryWrapper);
        Map<String, List<ExamQuOrderby>> collect = list.stream().collect(Collectors.groupingBy(ExamQuOrderby::getQuId));
        return collect;
    }

    @Override
    public void createOrderbys(List<Question> questionList, String userId) {
        List<ExamQuOrderby> insertList = new ArrayList<>();
        List<ExamQuOrderby> updateList = new ArrayList<>();
        Map<String, List<ExamQuOrderby>> quRadioMap = new HashMap<>();

        for (Question question : questionList) {
            String quId = question.getId();
            List<ExamQuOrderby> radios = question.getOrderByTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (ExamQuOrderby radio : radios) {
                ExamQuOrderby bean = new ExamQuOrderby();
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

        if (!insertList.isEmpty()) {
            createEntity(insertList, userId);
        }
        if (!updateList.isEmpty()) {
            updateEntity(updateList, userId);
        }
    }

    @Override
    public void removeByQuIds(List<String> questionIds) {
        QueryWrapper<ExamQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), questionIds);
        remove(queryWrapper);
    }

    @Override
    public void updateOrderbys(List<Question> questionList, String userId) {
        List<ExamQuOrderby> insertList = new ArrayList<>();
        List<ExamQuOrderby> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<ExamQuOrderby>> existingRadiosMap = loadExistingRadios(questionList);

        for (Question question : questionList) {
            List<ExamQuOrderby> radios = question.getOrderByTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = question.getId();
            List<ExamQuOrderby> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(ExamQuOrderby::getOptionId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(ExamQuOrderby::getId)
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
        createOrderbys(questionList, userId);
    }

    private Map<String, List<ExamQuOrderby>> loadExistingRadios(List<Question> questions) {
        List<String> quIds = questions.stream()
            .map(Question::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(ExamQuOrderby::getQuId));
    }

    private List<ExamQuOrderby> selectByQuIds(List<String> quIds) {
        QueryWrapper<ExamQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuOrderby::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<ExamQuOrderby> radios, String quId,
                                     String userId, List<ExamQuOrderby> insertList,
                                     List<ExamQuOrderby> updateList) {
        for (ExamQuOrderby radio : radios) {
            ExamQuOrderby bean = new ExamQuOrderby();
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

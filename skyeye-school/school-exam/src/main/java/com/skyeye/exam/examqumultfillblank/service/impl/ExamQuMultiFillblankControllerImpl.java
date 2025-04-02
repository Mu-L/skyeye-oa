package com.skyeye.exam.examqumultfillblank.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.question.CheckType;
import com.skyeye.eve.examquestion.entity.Question;
import com.skyeye.exam.examqumultfillblank.dao.ExamQuMultiFillblankDao;
import com.skyeye.exam.examqumultfillblank.entity.ExamQuMultiFillblank;
import com.skyeye.exam.examqumultfillblank.service.ExamQuMultiFillblankService;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "多行填空题管理", groupName = "多行填空题管理")
public class ExamQuMultiFillblankControllerImpl extends SkyeyeBusinessServiceImpl<ExamQuMultiFillblankDao, ExamQuMultiFillblank> implements ExamQuMultiFillblankService {

    @Override
    protected QueryWrapper<ExamQuMultiFillblank> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuMultiFillblank> list, String quId, String userId) {
        List<ExamQuMultiFillblank> quMultiFillblank = new ArrayList<>();
        List<ExamQuMultiFillblank> editquMultiFillblank = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExamQuMultiFillblank object = list.get(i);
            ExamQuMultiFillblank bean = new ExamQuMultiFillblank();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            if (StrUtil.isNotEmpty(object.getIsDefaultAnswer())) {
                bean.setIsDefaultAnswer(object.getIsDefaultAnswer());
            }
            if (ToolUtil.isBlank(object.getOptionId())) {
                bean.setQuId(quId);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quMultiFillblank.add(bean);
            } else {
                bean.setId(object.getOptionId());
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

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuMultiFillblank> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuMultiFillblank> selectQuMultiFillblank(String copyFromId) {
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getBelongId), id);
        List<ExamQuMultiFillblank> list = list(queryWrapper);
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
    public Map<String, List<ExamQuMultiFillblank>> selectByQuestionIds(List<String> questionIdList) {
        if (questionIdList.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId), questionIdList);
        List<ExamQuMultiFillblank> list = list(queryWrapper);
        Map<String, List<ExamQuMultiFillblank>> collect = list.stream().collect(Collectors.groupingBy(ExamQuMultiFillblank::getQuId));
        return collect;
    }

    @Override
    public void createMultiFillblanks(List<Question> questionList, String userId) {
        List<ExamQuMultiFillblank> insertList = new ArrayList<>();
        List<ExamQuMultiFillblank> updateList = new ArrayList<>();
        Map<String, List<ExamQuMultiFillblank>> quRadioMap = new HashMap<>();

        for (Question question : questionList) {
            String quId = question.getId();
            List<ExamQuMultiFillblank> radios = question.getMultifillblankTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (ExamQuMultiFillblank radio : radios) {
                ExamQuMultiFillblank bean = new ExamQuMultiFillblank();
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

        if (!insertList.isEmpty()) {
            createEntity(insertList,userId);
        }
        if (!updateList.isEmpty()) {
            updateEntity(updateList,userId);
        }
    }

    @Override
    public void removeByQuIds(List<String> questionIds) {
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId), questionIds);
        remove(queryWrapper);
    }

    @Override
    public void updateMultiFillblanks(List<Question> questionList, String userId) {
        List<ExamQuMultiFillblank> insertList = new ArrayList<>();
        List<ExamQuMultiFillblank> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<ExamQuMultiFillblank>> existingRadiosMap = loadExistingRadios(questionList);

        for (Question question : questionList) {
            List<ExamQuMultiFillblank> radios = question.getMultifillblankTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = question.getId();
            List<ExamQuMultiFillblank> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(ExamQuMultiFillblank::getOptionId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(ExamQuMultiFillblank::getId)
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
        createMultiFillblanks(questionList, userId);
    }

    private Map<String, List<ExamQuMultiFillblank>> loadExistingRadios(List<Question> questions) {
        List<String> quIds = questions.stream()
            .map(Question::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(ExamQuMultiFillblank::getQuId));
    }

    private List<ExamQuMultiFillblank> selectByQuIds(List<String> quIds) {
        QueryWrapper<ExamQuMultiFillblank> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuMultiFillblank::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<ExamQuMultiFillblank> radios, String quId,
                                     String userId, List<ExamQuMultiFillblank> insertList,
                                     List<ExamQuMultiFillblank> updateList) {
        for (ExamQuMultiFillblank radio : radios) {
            ExamQuMultiFillblank bean = new ExamQuMultiFillblank();
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

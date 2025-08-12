package com.skyeye.eve.orderby.service.impl;

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
import com.skyeye.eve.orderby.dao.DwQuOrderbyDao;
import com.skyeye.eve.orderby.entity.DwQuOrderby;
import com.skyeye.eve.orderby.service.DwQuOrderbyService;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "排序题行选项管理", groupName = "排序题行选项管理")
public class DwQuOrderbyServiceImpl extends SkyeyeBusinessServiceImpl<DwQuOrderbyDao, DwQuOrderby> implements DwQuOrderbyService {

    @Autowired
    private DwQuestionLogicService dwQuestionLogicService;

    @Override
    protected QueryWrapper<DwQuOrderby> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuOrderby> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuOrderby> orderby, String quId, String userId) {
        List<DwQuOrderby> quOrderBy = new ArrayList<>();
        List<DwQuOrderby> editquOrderBy = new ArrayList<>();
        for (int i = 0; i < orderby.size(); i++) {
            DwQuOrderby object = orderby.get(i);
            DwQuOrderby bean = new DwQuOrderby();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            if (ToolUtil.isBlank(object.getId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quOrderBy.add(bean);
            } else {
                bean.setId(object.getId());
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
        UpdateWrapper<DwQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuOrderby::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public List<DwQuOrderby> createOrderbys(List<DwQuestion> questionList, String userId) {
        List<DwQuOrderby> insertList = new ArrayList<>();
        List<DwQuOrderby> updateList = new ArrayList<>();
        Map<String, List<DwQuOrderby>> quRadioMap = new HashMap<>();

        for (DwQuestion dwQuestion : questionList) {
            String quId = dwQuestion.getId();
            List<DwQuOrderby> radios = dwQuestion.getOrderByTd();
            if (CollectionUtils.isEmpty(radios)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(radios);

            for (DwQuOrderby radio : radios) {
                DwQuOrderby bean = new DwQuOrderby();
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

        if (!insertList.isEmpty()) {
            super.createEntity(insertList, userId);
            return insertList;
        }
        if (!updateList.isEmpty()) {
            super.updateEntity(updateList, userId);
            return updateList;

        }
        return Collections.emptyList();
    }


    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuOrderby> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuOrderby> selectQuOrderby(String copyFromId) {
        QueryWrapper<DwQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuOrderby::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuOrderby>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), id);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuOrderby::getOrderById));
        List<DwQuOrderby> list = list(queryWrapper);
        Map<String, List<DwQuOrderby>> result = list.stream().collect(Collectors.groupingBy(DwQuOrderby::getQuId));
        return result;
    }

    @Override
    public void updateOrderbys(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuOrderby> insertList = new ArrayList<>();
        List<DwQuOrderby> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 问题Id和选项的映射
        Map<String, List<DwQuOrderby>> existingRadiosMap = loadExistingRadios(dwQuestionList);

        for (DwQuestion dwQuestion : dwQuestionList) {
            List<DwQuOrderby> radios = dwQuestion.getOrderByTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = dwQuestion.getId();
            List<DwQuOrderby> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 收集需要删除的ID
            Set<String> newIds = radios.stream()
                .map(DwQuOrderby::getId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(DwQuOrderby::getId)
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
        createOrderbys(dwQuestionList, userId);
    }

    @Override
    protected void deletePreExecution(List<String> ids) {
        if (CollectionUtil.isNotEmpty(ids)) {
            dwQuestionLogicService.deleteByCkQuId(ids);
        }
    }

    @Override
    public void removeByQuIds(List<String> dwQuestionIds) {
        QueryWrapper<DwQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), dwQuestionIds);
        remove(queryWrapper);
    }

    private Map<String, List<DwQuOrderby>> loadExistingRadios(List<DwQuestion> dwQuestions) {
        List<String> quIds = dwQuestions.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(DwQuOrderby::getQuId));
    }

    private List<DwQuOrderby> selectByQuIds(List<String> quIds) {
        QueryWrapper<DwQuOrderby> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuOrderby::getQuId), quIds);
        return list(queryWrapper);
    }

    private void processRadioOptions(List<DwQuOrderby> radios, String quId,
                                     String userId, List<DwQuOrderby> insertList,
                                     List<DwQuOrderby> updateList) {
        for (DwQuOrderby radio : radios) {
            DwQuOrderby bean = new DwQuOrderby();
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

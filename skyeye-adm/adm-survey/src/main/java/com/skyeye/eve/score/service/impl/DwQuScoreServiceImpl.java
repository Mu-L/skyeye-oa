package com.skyeye.eve.score.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.entity.DwQuestionLogic;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import com.skyeye.eve.radio.entity.DwQuRadio;
import com.skyeye.eve.score.dao.DwQuScoreDao;
import com.skyeye.eve.score.entity.DwQuScore;
import com.skyeye.eve.score.service.DwQuScoreService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DwQuScoreServiceImpl
 * @Description: 公评分题行选项管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "评分题选项管理", groupName = "评分题选项管理")
public class DwQuScoreServiceImpl extends SkyeyeBusinessServiceImpl<DwQuScoreDao, DwQuScore> implements DwQuScoreService {

    @Autowired
    private DwQuestionLogicService dwQuestionLogicService;

    @Override
    protected QueryWrapper<DwQuScore> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuScore> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuScore> score, String quId, String userId) {
        List<DwQuScore> quScore = new ArrayList<>();
        List<DwQuScore> editquScore = new ArrayList<>();
        for (int i = 0; i < score.size(); i++) {
            DwQuScore object = score.get(i);
            DwQuScore bean = new DwQuScore();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            if (ToolUtil.isBlank(object.getId())) {
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quScore.add(bean);
            } else {
                bean.setId(object.getId());
                editquScore.add(bean);
            }
        }
        if (!quScore.isEmpty()) {
            createEntity(quScore, userId);
        }
        if (!editquScore.isEmpty()) {
            updateEntity(editquScore, userId);
        }
        quScore.addAll(editquScore);
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuScore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuScore::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public List<DwQuScore> selectQuScore(String copyFromId) {
        QueryWrapper<DwQuScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuScore::getOrderById));
//        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuScore::getVisibility), CommonNumConstants.NUM_ONE);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuScore>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuScore::getQuId), id);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuRadio::getOrderById));
        List<DwQuScore> list = list(queryWrapper);
        Map<String, List<DwQuScore>> result = list.stream().collect(Collectors.groupingBy(DwQuScore::getQuId));
        return result;
    }

    @Override
    public List<DwQuScore> createScores(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuScore> insertList = new ArrayList<>();
        List<DwQuScore> updateList = new ArrayList<>();
        Map<String, List<DwQuScore>> quRadioMap = new HashMap<>();

        for (DwQuestion dwQuestion : dwQuestionList) {
            String quId = dwQuestion.getId();
            List<DwQuScore> scores = dwQuestion.getScoreTd();
            if (CollectionUtils.isEmpty(scores)) continue;

            quRadioMap.computeIfAbsent(quId, k -> new ArrayList<>()).addAll(scores);

            for (DwQuScore radio : scores) {
                DwQuScore bean = new DwQuScore();
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
        UpdateWrapper<DwQuScore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public void updateScores(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuScore> insertList = new ArrayList<>();
        List<DwQuScore> updateList = new ArrayList<>();
        Set<String> needDeleteIds = new HashSet<>();
        // 数据库中问题Id和选项的映射
        Map<String, List<DwQuScore>> existingRadiosMap = loadExistingRadios(dwQuestionList);

        for (DwQuestion dwQuestion : dwQuestionList) {
            List<DwQuScore> radios = dwQuestion.getScoreTd();
            if (CollectionUtils.isEmpty(radios)) {
                continue;
            }
            String quId = dwQuestion.getId();
            // 数据库中对应的问题id下的评分题
            List<DwQuScore> existingRadios = existingRadiosMap.getOrDefault(quId, Collections.emptyList());

            // 现在传进来的评分提选项id集合
            Set<String> newIds = radios.stream()
                .map(DwQuScore::getId)
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());

            existingRadios.stream()
                .map(DwQuScore::getId)
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
        createScores(dwQuestionList, userId);
    }

    @Override
    protected void deletePreExecution(List<String> ids) {
        if (CollectionUtil.isNotEmpty(ids)) {
            dwQuestionLogicService.deleteByCkQuId(ids);
        }
    }


    @Override
    public void removeByQuIds(List<String> dwQuestionIds) {
        QueryWrapper<DwQuScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuScore::getQuId), dwQuestionIds);
        remove(queryWrapper);
    }

    @Override
    public void removeByquId(String entityId) {
        UpdateWrapper<DwQuScore> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuScore::getQuId), entityId);
        remove(updateWrapper);
    }

    private Map<String, List<DwQuScore>> loadExistingRadios(List<DwQuestion> dwQuestions) {
        List<String> quIds = dwQuestions.stream()
            .map(DwQuestion::getId)
            .collect(Collectors.toList());
        return selectByQuIds(quIds).stream()
            .collect(Collectors.groupingBy(DwQuScore::getQuId));
    }

    private List<DwQuScore> selectByQuIds(List<String> quIds) {
        QueryWrapper<DwQuScore> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuScore::getQuId), quIds);
        return list(queryWrapper);
    }


    private void processRadioOptions(List<DwQuScore> radios, String quId,
                                     String userId, List<DwQuScore> insertList,
                                     List<DwQuScore> updateList) {
        for (DwQuScore radio : radios) {
            DwQuScore bean = new DwQuScore();
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

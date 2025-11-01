/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.exam.examquestionlogic.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exam.examquestion.entity.Question;
import com.skyeye.exam.examquestionlogic.dao.ExamQuestionLogicDao;
import com.skyeye.exam.examquestionlogic.entity.ExamQuestionLogic;
import com.skyeye.exam.examquestionlogic.service.ExamQuestionLogicService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamQuestionLogicServiceImpl
 * @Description: 题目逻辑设置管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "题目逻辑设置管理", groupName = "题目逻辑设置管理")
public class ExamQuestionLogicServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuestionLogicDao, ExamQuestionLogic> implements ExamQuestionLogicService {

    @Override
    public List<ExamQuestionLogic> setLogics(String quId, List<ExamQuestionLogic> questionLogic, String userId) {
        List<ExamQuestionLogic> insertList = new ArrayList<>();
        List<ExamQuestionLogic> editList = new ArrayList<>();
        for (int i = 0; i < questionLogic.size(); i++) {
            ExamQuestionLogic logic = questionLogic.get(i);
            ExamQuestionLogic bean = new ExamQuestionLogic();
            bean.setCkQuId(logic.getCkQuId());
            bean.setTitle(logic.getTitle());
            bean.setLogicType(logic.getLogicType());
            bean.setScoreNum(logic.getScoreNum());
            if (StrUtil.isNotEmpty(logic.getCgQuItemId())) {
                bean.setCgQuItemId(logic.getCgQuItemId());
                bean.setCkQuId(logic.getCkQuId());
            }
            if (StrUtil.isNotEmpty(logic.getGeLe())) {
                bean.setGeLe(logic.getGeLe());
            }
            if (ToolUtil.isBlank(logic.getId())) {
                bean.setSkQuId(quId);
                bean.setVisibility(1);
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                insertList.add(bean);
            } else {
                bean.setId(logic.getId());
                editList.add(bean);
            }
        }
        if (!insertList.isEmpty()) {
            createEntity(questionLogic, userId);
        }
        if (!editList.isEmpty()) {
            updateEntity(questionLogic, userId);
        }
        insertList.addAll(editList);
        return insertList;
    }

    @Override
    public Map<String, List<ExamQuestionLogic>> selectByQuestionIds(List<String> questionIds) {
        if (CollectionUtil.isEmpty(questionIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuestionLogic> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuestionLogic::getCkQuId), questionIds);
        List<ExamQuestionLogic> list = list(queryWrapper);
        Map<String, List<ExamQuestionLogic>> collect = list.stream().collect(Collectors.groupingBy(ExamQuestionLogic::getCkQuId));
        return collect;
    }

    @Override
    public void createLogics(List<Question> questionList, String userId) {
        List<ExamQuestionLogic> insertList = new ArrayList<>();
        List<ExamQuestionLogic> updateList = new ArrayList<>();
        Set<String> processedQuIds = new HashSet<>();

        // 数据收集阶段
        for (Question question : questionList) {
            if (!CommonNumConstants.NUM_TWO.equals(question.getTag())) continue;

            String quId = question.getId();
            List<ExamQuestionLogic> logics = question.getQuestionLogic();
            if (CollectionUtils.isEmpty(logics)) continue;
            processedQuIds.add(quId);
            for (ExamQuestionLogic logic : logics) {
                ExamQuestionLogic bean = new ExamQuestionLogic();
                // 属性拷贝
                BeanUtil.copyProperties(logic, bean);
                if (StrUtil.isNotEmpty(logic.getCgQuItemId())) {
                    bean.setCgQuItemId(logic.getCgQuItemId());
                }
                if (StrUtil.isNotEmpty(logic.getGeLe())) {
                    bean.setGeLe(logic.getGeLe());
                }
                if (ToolUtil.isBlank(logic.getId())) {
                    bean.setSkQuId(quId);
                    bean.setVisibility(1);
                    bean.setCreateId(userId);
                    bean.setCreateTime(DateUtil.getTimeAndToString());
                    insertList.add(bean);
                } else {
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
}

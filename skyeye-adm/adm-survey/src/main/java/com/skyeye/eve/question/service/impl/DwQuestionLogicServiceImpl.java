package com.skyeye.eve.question.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.question.dao.DwQuestionLogicDao;
import com.skyeye.eve.question.entity.DwQuestion;
import com.skyeye.eve.question.entity.DwQuestionLogic;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "题目逻辑设置管理", groupName = "题目逻辑设置管理")
public class DwQuestionLogicServiceImpl extends SkyeyeBusinessServiceImpl<DwQuestionLogicDao, DwQuestionLogic> implements DwQuestionLogicService {

    @Override
    public List<DwQuestionLogic> setLogics(String quId, List<DwQuestionLogic> questionLogic, String userId) {
        List<DwQuestionLogic> insertList = new ArrayList<>();
        List<DwQuestionLogic> editList = new ArrayList<>();
        for (int i = 0; i < questionLogic.size(); i++) {
            DwQuestionLogic logic = questionLogic.get(i);
            DwQuestionLogic bean = new DwQuestionLogic();
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
                bean.setId(ToolUtil.getSurFaceId());
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
    public List<DwQuestionLogic> selectByQuestionId(String ckQuId) {
        QueryWrapper<DwQuestionLogic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuestionLogic::getCkQuId), ckQuId);
        List<DwQuestionLogic> list = list(queryWrapper);
        return list;
    }

    @Override
    public Map<String, List<DwQuestionLogic>> selectByQuestionIds(List<String> questionIds) {
        if (CollectionUtil.isEmpty(questionIds)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuestionLogic> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuestionLogic::getCkQuId), questionIds);
        List<DwQuestionLogic> list = list(queryWrapper);
        Map<String, List<DwQuestionLogic>> result = list.stream().collect(Collectors.groupingBy(DwQuestionLogic::getCkQuId));
        return result;
    }

    @Override
    public void queryDwQuestionLogicList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwQuestionLogic> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwQuestionLogic::getCreateTime));
        List<DwQuestionLogic> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryMyDwQuestionLogicList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwQuestionLogic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuestionLogic::getCreateId), inputObject.getParams().get("id").toString());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwQuestionLogic::getCreateTime));
        List<DwQuestionLogic> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void createLogics(List<DwQuestion> dwQuestionList, String userId) {
        List<DwQuestionLogic> insertList = new ArrayList<>();
        List<DwQuestionLogic> updateList = new ArrayList<>();
        Set<String> processedQuIds = new HashSet<>();

        // 数据收集阶段
        for (DwQuestion dwQuestion : dwQuestionList) {
            if (!CommonNumConstants.NUM_TWO.equals(dwQuestion.getTag())) continue;

            String quId = dwQuestion.getId();
            List<DwQuestionLogic> logics = dwQuestion.getQuestionLogic();
            if (CollectionUtils.isEmpty(logics)) continue;
            processedQuIds.add(quId);
            for (DwQuestionLogic logic : logics) {
                DwQuestionLogic bean = new DwQuestionLogic();
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

    @Override
    public List<DwQuestionLogic> selectByDwQuestionIdList(List<String> dwQuestionIdList) {
        if (CollectionUtil.isEmpty(dwQuestionIdList)) {
            return new ArrayList<>();
        }
        QueryWrapper<DwQuestionLogic> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuestionLogic::getCkQuId), dwQuestionIdList);
        return list(queryWrapper);
    }
}



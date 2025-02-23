package com.skyeye.eve.question.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.question.dao.DwQuestionLogicDao;
import com.skyeye.eve.question.entity.DwQuestionLogic;
import com.skyeye.eve.question.service.DwQuestionLogicService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public Map<String, List<Map<String, Object>>> selectByQuestionIds(List<String> questionIds) {
        if (questionIds.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuestionLogic> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuestionLogic::getCkQuId), questionIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuestionLogic::getVisibility), 1);
        List<DwQuestionLogic> list = list(queryWrapper);
        Map<String, List<Map<String, Object>>> result = new HashMap<>();
        list.forEach(item -> {
            String ckQuId = item.getCkQuId();
            if (result.containsKey(ckQuId)) {
                result.get(ckQuId).add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
            } else {
                List<Map<String, Object>> tmp = new ArrayList<>();
                tmp.add(JSONUtil.toBean(JSONUtil.toJsonStr(item), null));
                result.put(ckQuId, tmp);
            }
        });
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
}

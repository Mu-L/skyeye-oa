package com.skyeye.exam.examquchckbox.service.impl;

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
import com.skyeye.exam.examquchckbox.dao.ExamQuCheckboxDao;
import com.skyeye.exam.examquchckbox.entity.ExamQuCheckbox;
import com.skyeye.exam.examquchckbox.service.ExamQuCheckboxService;
import com.skyeye.exam.examquestionlogic.service.ExamQuestionLogicService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "多选题选项表管理", groupName = "多选题选项表管理")
public class ExamQuCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuCheckboxDao, ExamQuCheckbox> implements ExamQuCheckboxService {

    @Autowired
    private ExamQuestionLogicService examQuestionLogicService;

    @Override
    protected QueryWrapper<ExamQuCheckbox> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuCheckbox> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuCheckbox::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuCheckbox> list, String quId, String userId) {
        List<ExamQuCheckbox> quCheckbox = new ArrayList<>();
        List<ExamQuCheckbox> editquCheck = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExamQuCheckbox object = list.get(i);
            ExamQuCheckbox bean = new ExamQuCheckbox();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setOptionTitle(object.getOptionTitle());
            bean.setIsNote(object.getIsNote());
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
                bean.setQuId(object.getQuId());
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quCheckbox.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquCheck.add(bean);
            }
        }
        if (!quCheckbox.isEmpty()) {
            createEntity(quCheckbox, userId);
        }
        if (!editquCheck.isEmpty()) {
            updateEntity(editquCheck, userId);
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuCheckbox> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuCheckbox::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuCheckbox> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuCheckbox::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuCheckbox> selectQuChenbox(String copyFromId) {
        QueryWrapper<ExamQuCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuCheckbox::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuCheckbox::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuCheckbox::getBelongId), id);
        List<ExamQuCheckbox> list = list(queryWrapper);
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
    public Map<String, List<ExamQuCheckbox>> selectByQuestionIds(List<String> questionIdList) {
        if (questionIdList.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuCheckbox::getQuId), questionIdList);
        List<ExamQuCheckbox> list = list(queryWrapper);
        Map<String, List<ExamQuCheckbox>> collect = list.stream().collect(Collectors.groupingBy(ExamQuCheckbox::getQuId));
        return collect;
    }
}

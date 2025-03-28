package com.skyeye.exam.examquradio.service.impl;

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
import com.skyeye.exam.examquradio.dao.ExamQuRadioDao;
import com.skyeye.exam.examquradio.entity.ExamQuRadio;
import com.skyeye.exam.examquradio.service.ExamQuRadioService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ExamQuRadioServiceImpl
 * @Description: 单选题选项表管理服务层
 * @author: skyeye云系列--lqy
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "单选题选项表管理", groupName = "单选题选项表管理")
public class ExamQuRadioServiceImpl extends SkyeyeBusinessServiceImpl<ExamQuRadioDao, ExamQuRadio> implements ExamQuRadioService {

    @Override
    protected QueryWrapper<ExamQuRadio> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ExamQuRadio> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<ExamQuRadio> list, String quId, String userId) {
        List<ExamQuRadio> quRadio = new ArrayList<>();
        List<ExamQuRadio> editquRadio = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            ExamQuRadio object = list.get(i);
            ExamQuRadio bean = new ExamQuRadio();
            bean.setOrderById(object.getOrderById());
            bean.setOptionName(object.getOptionName());
            bean.setIsNote(object.getIsNote());
            bean.setOptionTitle(object.getOptionTitle());
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
                bean.setQuId(quId);
                bean.setVisibility(1);
                bean.setId(ToolUtil.getSurFaceId());
                bean.setCreateId(userId);
                bean.setCreateTime(DateUtil.getTimeAndToString());
                quRadio.add(bean);
            } else {
                bean.setId(object.getOptionId());
                editquRadio.add(bean);
            }
        }
        if (!quRadio.isEmpty()) {
            createEntity(quRadio, userId);
        }
        if (!editquRadio.isEmpty()) {
            updateEntity(editquRadio, userId);
        }
    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<ExamQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamQuRadio::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<ExamQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<ExamQuRadio> selectQuRadio(String copyFromId) {
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ExamQuRadio::getOrderById));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<Map<String, Object>>> selectByBelongId(String id) {
        if (StrUtil.isEmpty(id)){
            return new HashMap<>();
        }
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getBelongId), id);
        List<ExamQuRadio> list = list(queryWrapper);
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
    public void deleteByQuestionId(String entityId) {
        UpdateWrapper<ExamQuRadio> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), entityId);
        remove(updateWrapper);
    }

    @Override
    public Map<String, List<ExamQuRadio>> selectByQuestionIds(List<String> questionIdList) {
        if (questionIdList.isEmpty()) {
            return new HashMap<>();
        }
        QueryWrapper<ExamQuRadio> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamQuRadio::getQuId), questionIdList);
        List<ExamQuRadio> list = list(queryWrapper);
        Map<String, List<ExamQuRadio>> collect = list.stream().collect(Collectors.groupingBy(ExamQuRadio::getQuId));
        return collect;
    }
}

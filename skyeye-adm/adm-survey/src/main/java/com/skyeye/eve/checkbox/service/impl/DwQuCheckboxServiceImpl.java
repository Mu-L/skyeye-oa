/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.checkbox.service.impl;

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
import com.skyeye.common.util.question.CheckType;
import com.skyeye.eve.checkbox.dao.DwQuCheckboxDao;
import com.skyeye.eve.checkbox.entity.DwQuCheckbox;
import com.skyeye.eve.checkbox.service.DwQuCheckboxService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DwQuCheckboxServiceImpl
 * @Description: 多选题选项服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷单选题选项", groupName = "答卷单选题选项", manageShow = false)
public class DwQuCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<DwQuCheckboxDao, DwQuCheckbox> implements DwQuCheckboxService {

    @Override
    protected QueryWrapper<DwQuCheckbox> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<DwQuCheckbox> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuCheckbox::getQuId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void saveList(List<DwQuCheckbox> list, String quId, String userId) {
        List<DwQuCheckbox> quCheckbox = new ArrayList<>();
        List<DwQuCheckbox> editquCheck = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            DwQuCheckbox object = list.get(i);
            DwQuCheckbox bean = new DwQuCheckbox();
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
            } else {
                bean.setCheckType(null);
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

//    @Override
//    protected void deletePreExecution(DwQuCheckbox entity) {
//        Integer visibility = entity.getVisibility();
//        if (visibility == 1){
//            throw new CustomException("该选项已显示，请先隐藏再删除");
//        }
//    }

    @Override
    public void changeVisibility(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        UpdateWrapper<DwQuCheckbox> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(DwQuCheckbox::getVisibility), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void removeByQuId(String quId) {
        UpdateWrapper<DwQuCheckbox> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(DwQuCheckbox::getQuId), quId);
        remove(updateWrapper);
    }

    @Override
    public List<DwQuCheckbox> selectQuChenbox(String copyFromId) {
        QueryWrapper<DwQuCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwQuCheckbox::getQuId), copyFromId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(DwQuCheckbox::getOrderById));
//        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamQuCheckbox::getVisibility),CommonNumConstants.NUM_ONE);
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<DwQuCheckbox>> selectByBelongId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwQuCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwQuCheckbox::getQuId), id);
        List<DwQuCheckbox> list = list(queryWrapper);
        Map<String, List<DwQuCheckbox>> result = list.stream().collect(Collectors.groupingBy(DwQuCheckbox::getQuId));
        return result;
    }

}

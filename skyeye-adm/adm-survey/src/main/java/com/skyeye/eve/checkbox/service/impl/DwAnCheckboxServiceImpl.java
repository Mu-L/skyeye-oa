/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.checkbox.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.checkbox.dao.DwAnCheckboxDao;
import com.skyeye.eve.checkbox.entity.DwAnCheckbox;
import com.skyeye.eve.checkbox.service.DwAnCheckboxService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: DwAnCheckboxServiceImpl
 * @Description: 答卷多选题保存服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/5/16 23:20
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye-report Inc. All rights reserved.
 * 注意：本内容具体规则请参照readme执行，地址：https://gitee.com/doc_wei01/skyeye-report/blob/master/README.md
 */
@Service
@SkyeyeService(name = "答卷多选题保存", groupName = "答卷多选题保存", manageShow = false)
public class DwAnCheckboxServiceImpl extends SkyeyeBusinessServiceImpl<DwAnCheckboxDao, DwAnCheckbox> implements DwAnCheckboxService {

    @Override
    protected void createPrepose(DwAnCheckbox entity) {
        String quItemId = entity.getQuItemId();
        String[] splitArray = quItemId.split(",");
        List<String> resultList = Arrays.asList(splitArray);
        List<DwAnCheckbox> dwAnCheckboxList = new ArrayList<>();
        for (String quAnswerId : resultList) {
            DwAnCheckbox dwAnCheckbox = new DwAnCheckbox();
            dwAnCheckbox.setQuItemId(quAnswerId);
            dwAnCheckboxList.add(dwAnCheckbox);
        }
        super.createEntity(dwAnCheckboxList, StrUtil.EMPTY);
    }

    @Override
    public void queryDwAnCheckboxListById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        List<DwAnCheckbox> examAnCheckboxList = list(queryWrapper);
        outputObject.setBean(examAnCheckboxList);
        outputObject.settotal(examAnCheckboxList.size());
    }


    @Override
    public List<DwAnCheckbox> selectAnCheckBoxByQuId(String id) {
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCheckbox::getQuId), id);
        return list(queryWrapper);
    }



    @Override
    public List<DwAnCheckbox> slectBySurveyId(String surveyId) {
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCheckbox::getBelongId), surveyId);
        return list(queryWrapper);
    }

    @Override
    public void deleteBySurveyId(String surveyId) {
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwAnCheckbox::getBelongId), surveyId);
        remove(queryWrapper);
    }

    @Override
    public Map<String, List<DwAnCheckbox>> selectByQuId(List<String> id) {
        if (CollectionUtil.isEmpty(id)) {
            return new HashMap<>();
        }
        QueryWrapper<DwAnCheckbox> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(DwAnCheckbox::getQuId),id);
        Map<String, List<DwAnCheckbox>> cheneckBoxMap = list(queryWrapper).stream().collect(Collectors.groupingBy(DwAnCheckbox::getQuId));
        return cheneckBoxMap;
    }

}

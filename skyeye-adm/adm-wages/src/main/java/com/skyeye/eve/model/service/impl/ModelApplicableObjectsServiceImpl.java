/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.model.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.model.dao.ModelApplicableObjectsDao;
import com.skyeye.eve.model.entity.ModelApplicableObjects;
import com.skyeye.eve.model.service.ModelApplicableObjectsService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ModelApplicableObjectsServiceImpl
 * @Description: 薪资模板适用对象服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/21 13:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "薪资模板适用对象管理", groupName = "薪资模板适用对象管理", manageShow = false)
public class ModelApplicableObjectsServiceImpl extends SkyeyeBusinessServiceImpl<ModelApplicableObjectsDao, ModelApplicableObjects> implements ModelApplicableObjectsService {

    @Override
    public void deleteApplicableObjectsByPId(String modelId) {
        QueryWrapper<ModelApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ModelApplicableObjects::getModelId), modelId);
        remove(queryWrapper);
    }

    @Override
    public void saveApplicableObjects(String modelId, List<ModelApplicableObjects> applicableObjectsList) {
        deleteApplicableObjectsByPId(modelId);
        if (CollectionUtil.isNotEmpty(applicableObjectsList)) {
            for (ModelApplicableObjects applicableObjects : applicableObjectsList) {
                applicableObjects.setModelId(modelId);
            }
            createEntity(applicableObjectsList, StrUtil.EMPTY);
        }
    }

    @Override
    public List<ModelApplicableObjects> queryApplicableObjectsByPId(String modelId) {
        QueryWrapper<ModelApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ModelApplicableObjects::getModelId), modelId);
        List<ModelApplicableObjects> applicableObjectsList = list(queryWrapper);
        return applicableObjectsList;
    }

    @Override
    public Map<String, List<ModelApplicableObjects>> queryApplicableObjectsByPId(List<String> modelId) {
        QueryWrapper<ModelApplicableObjects> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ModelApplicableObjects::getModelId), modelId);
        List<ModelApplicableObjects> applicableObjectsList = list(queryWrapper);
        Map<String, List<ModelApplicableObjects>> listMap = applicableObjectsList.stream()
            .collect(Collectors.groupingBy(ModelApplicableObjects::getModelId));
        return listMap;
    }

}

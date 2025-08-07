/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dynamic.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DataCommonUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dynamic.dao.DynamicAttrValueDao;
import com.skyeye.dynamic.entity.DynamicAttrValue;
import com.skyeye.dynamic.entity.DynamicAttrValueApi;
import com.skyeye.dynamic.service.DynamicAttrValueService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: DynamicAttrValueServiceImpl
 * @Description: 动态属性值服务层实现类--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/13 14:14
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "动态属性值管理", groupName = "动态属性值管理", tenant = TenantEnum.NO_ISOLATION)
public class DynamicAttrValueServiceImpl extends SkyeyeBusinessServiceImpl<DynamicAttrValueDao, DynamicAttrValue> implements DynamicAttrValueService {

    @Override
    public void writeDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        DynamicAttrValue dynamicAttrValue = inputObject.getParams(DynamicAttrValue.class);
        String currentTime = DateUtil.getTimeAndToString();
        dynamicAttrValue.setCreateTime(currentTime);
        dynamicAttrValue.setLastUpdateTime(currentTime);

        remove(dynamicAttrValue.getObjectAppId(), dynamicAttrValue.getObjectId(), dynamicAttrValue.getObjectKey());

        DataCommonUtil.setId(dynamicAttrValue);
        save(dynamicAttrValue);
    }

    private void remove(String appId, String objectId, String objectKey) {
        QueryWrapper<DynamicAttrValue> deleteWrapper = new QueryWrapper<>();
        log.info("appId:{}, objectId:{}, objectKey:{}", appId, objectId, objectKey);
        deleteWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectAppId), appId);
        deleteWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectId), objectId);
        deleteWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectKey), objectKey);
        remove(deleteWrapper);
    }

    @Override
    public void writeBatchDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        DynamicAttrValueApi dynamicAttrValueApi = inputObject.getParams(DynamicAttrValueApi.class);
        List<DynamicAttrValue> dynamicAttrValueList = dynamicAttrValueApi.getDynamicAttrValueList();
        String currentTime = DateUtil.getTimeAndToString();

        // 批量删除
        QueryWrapper<DynamicAttrValue> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.or(wrapper -> {
            for (DynamicAttrValue dynamicAttrValue : dynamicAttrValueList) {
                wrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectAppId), dynamicAttrValue.getObjectAppId());
                wrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectId), dynamicAttrValue.getObjectId());
                wrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectKey), dynamicAttrValue.getObjectKey());
            }
        });
        remove(deleteWrapper);

        for (DynamicAttrValue dynamicAttrValue : dynamicAttrValueList) {
            dynamicAttrValue.setCreateTime(currentTime);
            dynamicAttrValue.setLastUpdateTime(currentTime);
            DataCommonUtil.setId(dynamicAttrValue);
        }
        saveBatch(dynamicAttrValueList);
    }

    @Override
    public void queryDynamicAttrValueList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        String objectAppId = params.get("objectAppId").toString();
        QueryWrapper<DynamicAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectAppId), objectAppId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectKey), objectKey);
        DynamicAttrValue dynamicAttrValue = getOne(queryWrapper, false);
        outputObject.setBean(dynamicAttrValue);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryBatchDynamicAttrValueList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> list = JSONUtil.toList(params.get("list").toString(), null);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        String objectAppId = list.get(0).get("objectAppId").toString();
        String objectKey = list.get(0).get("objectKey").toString();
        List<String> objectIdList = list.stream()
            .map(map -> map.get("objectId").toString()).distinct()
            .collect(Collectors.toList());
        QueryWrapper<DynamicAttrValue> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectAppId), objectAppId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectKey), objectKey);
        queryWrapper.in(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectId), objectIdList);
        List<DynamicAttrValue> dynamicAttrValueList = list(queryWrapper);
        outputObject.setBeans(dynamicAttrValueList);
        outputObject.settotal(dynamicAttrValueList.size());
    }

    @Override
    public void deleteDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectAppId = params.get("objectAppId").toString();
        String objectId = params.get("objectId").toString();
        String objectKey = params.get("objectKey").toString();
        remove(objectAppId, objectId, objectKey);
    }

    @Override
    public void deleteBatchDynamicAttrValue(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        List<Map<String, Object>> list = JSONUtil.toList(params.get("list").toString(), null);
        // 批量删除
        QueryWrapper<DynamicAttrValue> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.or(wrapper -> {
            for (Map<String, Object> dynamicAttrValue : list) {
                wrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectAppId), dynamicAttrValue.get("objectAppId").toString());
                wrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectId), dynamicAttrValue.get("objectId").toString());
                wrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectKey), dynamicAttrValue.get("objectKey").toString());
            }
        });
        remove(deleteWrapper);
    }
}

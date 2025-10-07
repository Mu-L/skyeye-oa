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

        // 分批删除，避免SQL过长
        batchDeleteDynamicAttrValues(dynamicAttrValueList);

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

        // 分批删除，避免SQL过长
        batchDeleteDynamicAttrValuesFromMap(list);
    }

    /**
     * 分批删除动态属性值，避免SQL过长导致性能问题
     * 优化策略：按ObjectAppId和ObjectKey分组，ObjectId使用IN查询
     *
     * @param dynamicAttrValueList 要删除的动态属性值列表
     */
    private void batchDeleteDynamicAttrValues(List<DynamicAttrValue> dynamicAttrValueList) {
        if (CollectionUtil.isEmpty(dynamicAttrValueList)) {
            return;
        }

        // 按ObjectAppId和ObjectKey分组，收集ObjectId列表
        Map<String, List<String>> groupedData = dynamicAttrValueList.stream()
            .collect(Collectors.groupingBy(
                item -> item.getObjectAppId() + "|" + item.getObjectKey(),
                Collectors.mapping(DynamicAttrValue::getObjectId, Collectors.toList())
            ));

        // 分批处理每个分组
        for (Map.Entry<String, List<String>> entry : groupedData.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            String objectAppId = keyParts[0];
            String objectKey = keyParts[1];
            List<String> objectIdList = entry.getValue();

            // 对ObjectId列表进行分批处理
            for (int i = 0; i < objectIdList.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, objectIdList.size());
                List<String> batchObjectIds = objectIdList.subList(i, endIndex);

                QueryWrapper<DynamicAttrValue> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectAppId), objectAppId);
                deleteWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectKey), objectKey);
                deleteWrapper.in(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectId), batchObjectIds);

                remove(deleteWrapper);
                log.info("批量删除动态属性值，分组: {}-{}, ObjectId数量: {}", objectAppId, objectKey, batchObjectIds.size());
            }
        }
    }

    /**
     * 分批删除动态属性值（从Map列表），避免SQL过长导致性能问题
     * 优化策略：按ObjectAppId和ObjectKey分组，ObjectId使用IN查询
     *
     * @param list 要删除的动态属性值Map列表
     */
    private void batchDeleteDynamicAttrValuesFromMap(List<Map<String, Object>> list) {
        if (CollectionUtil.isEmpty(list)) {
            return;
        }

        // 按ObjectAppId和ObjectKey分组，收集ObjectId列表
        Map<String, List<String>> groupedData = list.stream()
            .collect(Collectors.groupingBy(
                item -> item.get("objectAppId").toString() + "|" + item.get("objectKey").toString(),
                Collectors.mapping(item -> item.get("objectId").toString(), Collectors.toList())
            ));

        // 分批处理每个分组
        for (Map.Entry<String, List<String>> entry : groupedData.entrySet()) {
            String[] keyParts = entry.getKey().split("\\|");
            String objectAppId = keyParts[0];
            String objectKey = keyParts[1];
            List<String> objectIdList = entry.getValue();

            // 对ObjectId列表进行分批处理
            for (int i = 0; i < objectIdList.size(); i += BATCH_SIZE) {
                int endIndex = Math.min(i + BATCH_SIZE, objectIdList.size());
                List<String> batchObjectIds = objectIdList.subList(i, endIndex);

                QueryWrapper<DynamicAttrValue> deleteWrapper = new QueryWrapper<>();
                deleteWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectAppId), objectAppId);
                deleteWrapper.eq(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectKey), objectKey);
                deleteWrapper.in(MybatisPlusUtil.toColumns(DynamicAttrValue::getObjectId), batchObjectIds);

                remove(deleteWrapper);
                log.info("批量删除动态属性值，分组: {}-{}, ObjectId数量: {}", objectAppId, objectKey, batchObjectIds.size());
            }
        }
    }
}

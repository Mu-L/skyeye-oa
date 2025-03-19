/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.property.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.exception.CustomException;
import com.skyeye.property.dao.ReportPropertyDao;
import com.skyeye.property.entity.Property;
import com.skyeye.property.entity.PropertyValue;
import com.skyeye.property.service.ReportPropertyService;
import com.skyeye.property.service.ReportPropertyValueService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ReportPropertyServiceImpl
 * @Description: 模型---样式属性管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/9/5 16:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "样式属性管理", groupName = "样式属性管理")
public class ReportPropertyServiceImpl extends SkyeyeBusinessServiceImpl<ReportPropertyDao, Property> implements ReportPropertyService {

    @Autowired
    private ReportPropertyValueService reportPropertyValueService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置属性值
        List<String> ids = beans.stream().filter(property -> Integer.parseInt(property.get("optional").toString()) == WhetherEnum.ENABLE_USING.getKey())
            .map(property -> property.get("id").toString()).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(ids)) {
            Map<String, List<PropertyValue>> map = reportPropertyValueService.queryByPropertyId(ids);
            beans.forEach(property -> {
                if (Integer.parseInt(property.get("optional").toString()) == WhetherEnum.ENABLE_USING.getKey()) {
                    List<PropertyValue> propertyValueList = map.get(property.get("id").toString());
                    if (CollectionUtil.isEmpty(propertyValueList)) {
                        return;
                    }
                    PropertyValue defaultProVal = propertyValueList.stream().filter(propertyValue -> propertyValue.getDefaultChoose().equals(WhetherEnum.ENABLE_USING.getKey()))
                        .findFirst().orElse(new PropertyValue());
                    property.put("defaultValue", defaultProVal.getValue());
                }
            });
        }
        return beans;
    }

    @Override
    public void validatorEntity(Property entity) {
        super.validatorEntity(entity);
        // 当optional=1时, 需要解析 propertyValueList. 当optional=0时, defaultValue为必填
        if (entity.getOptional().equals(WhetherEnum.DISABLE_USING.getKey())) {
            if (StrUtil.isEmpty(entity.getDefaultValue())) {
                throw new CustomException("标识属性值为不可选时, 属性默认值必填");
            }
        }
    }

    @Override
    public void writePostpose(Property entity, String userId) {
        super.writePostpose(entity, userId);
        // 当optional=1时, 需要解析 propertyValueList. 当optional=0时, defaultValue为必填
        if (entity.getOptional().equals(WhetherEnum.ENABLE_USING.getKey())) {
            reportPropertyValueService.save(entity.getId(), entity.getPropertyValueList());
        }
    }

    @Override
    public Property getDataFromDb(String id) {
        Property property = super.getDataFromDb(id);
        if (property.getOptional().equals(WhetherEnum.ENABLE_USING.getKey())) {
            List<PropertyValue> propertyValueList = reportPropertyValueService.queryByPropertyId(id);
            PropertyValue defaultProVal = propertyValueList.stream().filter(propertyValue -> propertyValue.getDefaultChoose().equals(WhetherEnum.ENABLE_USING.getKey()))
                .findFirst().orElse(new PropertyValue());
            property.setPropertyValueList(propertyValueList);
            property.setDefaultValue(defaultProVal.getValue());
        }
        return property;
    }

    @Override
    public List<Property> getDataFromDb(List<String> idList) {
        List<Property> propertyList = super.getDataFromDb(idList);

        // 设置属性值
        List<String> ids = propertyList.stream().filter(property -> property.getOptional().equals(WhetherEnum.ENABLE_USING.getKey()))
            .map(Property::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(ids)) {
            Map<String, List<PropertyValue>> map = reportPropertyValueService.queryByPropertyId(ids);
            propertyList.forEach(property -> {
                if (property.getOptional().equals(WhetherEnum.ENABLE_USING.getKey())) {
                    List<PropertyValue> propertyValueList = map.get(property.getId());
                    PropertyValue defaultProVal = propertyValueList.stream().filter(propertyValue -> propertyValue.getDefaultChoose().equals(WhetherEnum.ENABLE_USING.getKey()))
                        .findFirst().orElse(new PropertyValue());
                    property.setPropertyValueList(propertyValueList);
                    property.setDefaultValue(defaultProVal.getValue());
                }
            });
        }

        return propertyList;
    }

    @Override
    public void deletePostpose(Property entity) {
        if (entity.getOptional().equals(WhetherEnum.ENABLE_USING.getKey())) {
            reportPropertyValueService.deleteByPropertyId(entity.getId());
        }
    }

}

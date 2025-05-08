/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.field.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.WagesConstant;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.field.dao.WagesFieldTypeDao;
import com.skyeye.eve.field.entity.FieldStaffLink;
import com.skyeye.eve.field.entity.FieldType;
import com.skyeye.eve.field.service.FieldStaffLinkService;
import com.skyeye.eve.field.service.WagesFieldTypeService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: WagesFieldTypeServiceImpl
 * @Description: 薪资字段管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/11/26 9:11
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "薪资字段", groupName = "薪资字段")
public class WagesFieldTypeServiceImpl extends SkyeyeBusinessServiceImpl<WagesFieldTypeDao, FieldType> implements WagesFieldTypeService {

    @Autowired
    private FieldStaffLinkService fieldStaffLinkService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryWagesFieldTypeList(pageInfo);
        return beans;
    }

    @Override
    public void validatorEntity(FieldType entity) {
        // 和系统默认的key做比较
        Map<String, Object> defaultKey = WagesConstant.DEFAULT_WAGES_FIELD_TYPE.getList().stream()
            .filter(item -> entity.getKey().equals(item.get("key").toString())).findFirst().orElse(null);
        // 操作的key在默认定义的key中是否包含
        if (CollectionUtil.isNotEmpty(defaultKey)) {
            throw new CustomException("this ['key'] is Already exists simple default key.");
        }
        super.validatorEntity(entity);
    }

    @Override
    public void createPostpose(FieldType entity, String userId) {
        // 保存完字段信息后，为每个员工加上该薪资字段
        insertWagesFieldTypeKeyToStaff(entity.getKey());
    }

    private void insertWagesFieldTypeKeyToStaff(String key) {
        List<Map<String, Object>> staff = skyeyeBaseMapper.queryAllStaffMationList();
        List<FieldStaffLink> fieldStaffLinkList = new ArrayList<>();
        staff.forEach(bean -> {
            FieldStaffLink fieldStaffLink = new FieldStaffLink();
            fieldStaffLink.setStaffId(bean.get("id").toString());
            fieldStaffLink.setFieldTypeKey(key);
            fieldStaffLinkList.add(fieldStaffLink);
        });
        fieldStaffLinkService.saveOrUpdateEntity(fieldStaffLinkList, StrUtil.EMPTY, true);
    }

    @Override
    public void updatePrepose(FieldType entity) {
        FieldType oldBean = selectById(entity.getId());
        if (!StrUtil.equals(oldBean.getKey(), entity.getKey())) {
            // 修改员工之前绑定的薪资字段key
            fieldStaffLinkService.updateStaffFiledKey(oldBean.getKey(), entity.getKey());
        }
    }

    @Override
    public void deletePostpose(FieldType entity) {
        // 删除员工绑定的薪资字段key
        fieldStaffLinkService.deleteStaffFiledKey(entity.getKey());
    }

    @Override
    public void queryEnableWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<FieldType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(FieldType::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<FieldType> list = list(queryWrapper);
        list.forEach(bean -> {
            bean.setId(bean.getKey());
        });
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    @IgnoreTenant
    public void querySysWagesFieldTypeList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = WagesConstant.DEFAULT_WAGES_FIELD_TYPE.getList();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 根据字段key批量获取薪资字段信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryWagesFieldListByKeys(InputObject inputObject, OutputObject outputObject) {
        String keys = inputObject.getParams().get("keys").toString();
        List<String> keyList = Arrays.asList(keys.split(CommonCharConstants.COMMA_MARK));
        keyList = keyList.stream().filter(str -> !ToolUtil.isBlank(str)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(keyList)) {
            return;
        }
        // 从数据库中获取
        QueryWrapper<FieldType> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(FieldType::getKey), keyList);
        List<FieldType> fieldTypeList = list(queryWrapper);
        // 从枚举中获取
        List<String> finalKeyList = keyList;
        WagesConstant.DEFAULT_WAGES_FIELD_TYPE.getList().stream()
            .filter(bean -> finalKeyList.contains(bean.get("key").toString()))
            .forEach(bean -> {
                FieldType fieldType = new FieldType();
                fieldType.setKey(bean.get("key").toString());
                fieldType.setName(bean.get("name").toString());
                fieldTypeList.add(fieldType);
            });
        outputObject.setBeans(fieldTypeList);
        outputObject.settotal(fieldTypeList.size());
    }

    @Override
    public Map<String, FieldType> queryAllFieldTypeMap() {
        List<FieldType> fieldTypes = queryAllWagesFieldTypeList();
        return fieldTypes.stream().collect(Collectors.toMap(FieldType::getKey, bean -> bean));
    }

    @Override
    public List<FieldType> queryAllWagesFieldTypeList() {
        return list();
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ordertype.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.ordertype.dao.SealOrderTypeDao;
import com.skyeye.ordertype.entity.SealOrderType;
import com.skyeye.ordertype.entity.SealOrderTypeAllowStaff;
import com.skyeye.ordertype.service.SealOrderTypeAllowStaffService;
import com.skyeye.ordertype.service.SealOrderTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SealOrderTypeServiceImpl
 * @Description: 工单类型服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工单类型", groupName = "工单类型")
public class SealOrderTypeServiceImpl extends SkyeyeBusinessServiceImpl<SealOrderTypeDao, SealOrderType> implements SealOrderTypeService {

    @Autowired
    private SealOrderTypeAllowStaffService sealOrderTypeAllowStaffService;

    @Override
    protected void createPrepose(SealOrderType entity) {
        super.createPrepose(entity);
        // 设置默认的工单提交时间：开始时间00:00，结束时间23:59
        entity.setStartTime("00:00");
        entity.setEndTime("23:59");
        // 默认允许所有人接单
        entity.setIsAllowAllStaff(WhetherEnum.ENABLE_USING.getKey());
    }

    @Override
    public String updateEntity(SealOrderType entity, String userId) {
        SealOrderType oldEntity = selectById(entity.getId());
        // 普通编辑操作只更新以下信息，其他的信息默认从数据库中获取
        oldEntity.setEnabled(entity.getEnabled());
        oldEntity.setCodeNumber(entity.getCodeNumber());
        oldEntity.setName(entity.getName());
        oldEntity.setRemark(entity.getRemark());

        return super.updateEntity(oldEntity, userId);
    }

    @Override
    protected void writePostpose(SealOrderType entity, String userId) {
        super.writePostpose(entity, userId);
        if (WhetherEnum.ENABLE_USING.getKey().equals(entity.getIsAllowAllStaff())) {
            // 如果允许所有人接单，那么就把工单类型允许的接单人数据全部删除
            sealOrderTypeAllowStaffService.deleteByOrderTypeId(entity.getId());
        } else if (WhetherEnum.DISABLE_USING.getKey().equals(entity.getIsAllowAllStaff())) {
            // 如果不允许所有人接单，那么就只保存特定人
            sealOrderTypeAllowStaffService.saveList(entity.getId(), entity.getAllowedStaffId());
        }
    }

    @Override
    protected void deletePostpose(String id) {
        // 删除允许接单的人
        sealOrderTypeAllowStaffService.deleteByOrderTypeId(id);
    }

    @Override
    public SealOrderType getDataFromDb(String id) {
        SealOrderType sealOrderType = super.getDataFromDb(id);
        // 查询允许接单的人
        List<SealOrderTypeAllowStaff> sealOrderTypeAllowStaffs = sealOrderTypeAllowStaffService.selectByOrderTypeId(id);
        List<String> staffIdList = sealOrderTypeAllowStaffs.stream().map(SealOrderTypeAllowStaff::getStaffId).distinct().collect(Collectors.toList());
        sealOrderType.setAllowedStaffId(staffIdList);
        return sealOrderType;
    }

    @Override
    public SealOrderType selectById(String id) {
        SealOrderType sealOrderType = super.selectById(id);
        if (WhetherEnum.DISABLE_USING.getKey().equals(sealOrderType.getIsAllowAllStaff()) && CollectionUtil.isNotEmpty(sealOrderType.getAllowedStaffId())) {
            // 如果不允许所有人接单，那么就只查询特定人
            Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(sealOrderType.getAllowedStaffId());
            sealOrderType.setAllowedStaffMation(staffMap.values().stream().collect(Collectors.toList()));
        }
        return sealOrderType;
    }

    @Override
    protected List<SealOrderType> getDataFromDb(List<String> idList) {
        List<SealOrderType> sealOrderTypeList = super.getDataFromDb(idList);
        // 查询允许接单的人
        Map<String, List<SealOrderTypeAllowStaff>> selectedByOrderTypeIds = sealOrderTypeAllowStaffService.selectByOrderTypeIds(idList);
        sealOrderTypeList.forEach(sealOrderType -> {
            List<SealOrderTypeAllowStaff> sealOrderTypeAllowStaffs = selectedByOrderTypeIds.get(sealOrderType.getId());
            if (CollectionUtil.isEmpty(sealOrderTypeAllowStaffs)) {
                return;
            }
            List<String> staffIdList = sealOrderTypeAllowStaffs.stream().map(SealOrderTypeAllowStaff::getStaffId).distinct().collect(Collectors.toList());
            sealOrderType.setAllowedStaffId(staffIdList);
        });
        return sealOrderTypeList;
    }

    @Override
    public List<SealOrderType> selectByIds(String... ids) {
        List<SealOrderType> sealOrderTypeList = super.selectByIds(ids);

        // 收集所有需要查询员工信息的工单类型的员工ID
        List<String> allStaffIds = sealOrderTypeList.stream()
            .filter(sealOrderType -> WhetherEnum.DISABLE_USING.getKey().equals(sealOrderType.getIsAllowAllStaff())
                && CollectionUtil.isNotEmpty(sealOrderType.getAllowedStaffId()))
            .flatMap(sealOrderType -> sealOrderType.getAllowedStaffId().stream())
            .distinct()
            .collect(Collectors.toList());

        // 批量查询所有员工信息
        final Map<String, Map<String, Object>> allStaffMap = CollectionUtil.isNotEmpty(allStaffIds)
            ? iAuthUserService.queryUserMationListByStaffIds(allStaffIds)
            : new HashMap<>();

        // 为每个工单类型设置对应的员工信息
        sealOrderTypeList.forEach(sealOrderType -> {
            if (WhetherEnum.DISABLE_USING.getKey().equals(sealOrderType.getIsAllowAllStaff())
                && CollectionUtil.isNotEmpty(sealOrderType.getAllowedStaffId())) {
                // 如果不允许所有人接单，那么就只查询特定人
                List<Map<String, Object>> staffMationList = sealOrderType.getAllowedStaffId().stream()
                    .filter(staffId -> allStaffMap.containsKey(staffId))
                    .map(allStaffMap::get)
                    .collect(Collectors.toList());
                sealOrderType.setAllowedStaffMation(staffMationList);
            }
        });

        return sealOrderTypeList;
    }

    @Override
    public void queryEnabledSealOrderTypeList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<SealOrderType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealOrderType::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(SealOrderType::getCreateTime));
        List<SealOrderType> typeList = list(queryWrapper);
        outputObject.setBeans(typeList);
        outputObject.settotal(typeList.size());
    }

}


/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.ordertype.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.ordertype.dao.SealOrderTypeAllowStaffDao;
import com.skyeye.ordertype.entity.SealOrderTypeAllowStaff;
import com.skyeye.ordertype.service.SealOrderTypeAllowStaffService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SealOrderTypeAllowStaffServiceImpl
 * @Description: 工单类型允许的接单人服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工单类型允许的接单人设置", groupName = "工单类型设置", allowDynamicAttrKey = false)
public class SealOrderTypeAllowStaffServiceImpl extends SkyeyeBusinessServiceImpl<SealOrderTypeAllowStaffDao, SealOrderTypeAllowStaff> implements SealOrderTypeAllowStaffService {

    @Override
    public void saveList(String orderTypeId, List<String> staffIds) {
        deleteByOrderTypeId(orderTypeId);
        if (CollectionUtil.isNotEmpty(staffIds)) {
            staffIds = staffIds.stream()
                .filter(StrUtil::isNotEmpty)
                .distinct().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(staffIds)) {
                return;
            }
            List<SealOrderTypeAllowStaff> beans = new ArrayList<>();
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            for (String staffId : staffIds) {
                SealOrderTypeAllowStaff sealOrderTypeAllowStaff = new SealOrderTypeAllowStaff();
                sealOrderTypeAllowStaff.setOrderTypeId(orderTypeId);
                sealOrderTypeAllowStaff.setStaffId(staffId);
                beans.add(sealOrderTypeAllowStaff);
            }
            createEntity(beans, userId);
        }
    }

    @Override
    public void deleteByOrderTypeId(String orderTypeId) {
        QueryWrapper<SealOrderTypeAllowStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealOrderTypeAllowStaff::getOrderTypeId), orderTypeId);
        remove(queryWrapper);
    }

    @Override
    public List<SealOrderTypeAllowStaff> selectByOrderTypeId(String orderTypeId) {
        QueryWrapper<SealOrderTypeAllowStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SealOrderTypeAllowStaff::getOrderTypeId), orderTypeId);
        List<SealOrderTypeAllowStaff> list = list(queryWrapper);
        return list;
    }

    @Override
    public Map<String, List<SealOrderTypeAllowStaff>> selectByOrderTypeIds(List<String> orderTypeIds) {
        if (CollectionUtil.isEmpty(orderTypeIds)) {
            return new HashMap<>();
        }
        QueryWrapper<SealOrderTypeAllowStaff> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SealOrderTypeAllowStaff::getOrderTypeId), orderTypeIds);
        List<SealOrderTypeAllowStaff> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(SealOrderTypeAllowStaff::getOrderTypeId));
    }
}

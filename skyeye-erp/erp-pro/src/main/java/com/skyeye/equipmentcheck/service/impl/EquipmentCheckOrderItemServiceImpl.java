package com.skyeye.equipmentcheck.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentcheck.dao.EquipmentCheckOrderItemDao;
import com.skyeye.equipmentcheck.entity.EquipmentCheckOrderItem;
import com.skyeye.equipmentcheck.service.EquipmentCheckOrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: EquipmentCheckOrderItemServiceImpl
 * @Description: 设备点检单明细服务实现层
 */
@Service
@SkyeyeService(name = "设备点检单明细", groupName = "设备点检", manageShow = false)
public class EquipmentCheckOrderItemServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentCheckOrderItemDao, EquipmentCheckOrderItem>
    implements EquipmentCheckOrderItemService {

    @Override
    public void saveList(String parentId, List<EquipmentCheckOrderItem> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (EquipmentCheckOrderItem item : beans) {
                item.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<EquipmentCheckOrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckOrderItem::getParentId), parentId);
        remove(queryWrapper);
    }

    @Override
    public List<EquipmentCheckOrderItem> selectByParentId(String parentId) {
        QueryWrapper<EquipmentCheckOrderItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckOrderItem::getParentId), parentId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(EquipmentCheckOrderItem::getSortNo));
        return list(queryWrapper);
    }
}


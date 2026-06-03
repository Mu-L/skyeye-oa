package com.skyeye.equipmentcheckstandard.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.equipmentcheckstandard.dao.EquipmentCheckStandardItemDao;
import com.skyeye.equipmentcheckstandard.entity.EquipmentCheckStandardItem;
import com.skyeye.equipmentcheckstandard.service.EquipmentCheckStandardItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: EquipmentCheckStandardItemServiceImpl
 * @Description: 设备点检标准明细服务实现层
 */
@Service
@SkyeyeService(name = "设备点检标准明细", groupName = "设备点检", manageShow = false)
public class EquipmentCheckStandardItemServiceImpl extends SkyeyeBusinessServiceImpl<EquipmentCheckStandardItemDao, EquipmentCheckStandardItem>
    implements EquipmentCheckStandardItemService {

    //批量保存点检标准明细
    @Override
    public void saveList(String parentId, List<EquipmentCheckStandardItem> beans) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (EquipmentCheckStandardItem item : beans) {
                item.setParentId(parentId);
            }
            createEntity(beans, StrUtil.EMPTY);
        }
    }

    //根据父ID删除点检标准明细
    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<EquipmentCheckStandardItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckStandardItem::getParentId), parentId);
        remove(queryWrapper);
    }

    //根据父ID查询点检标准明细
    @Override
    public List<EquipmentCheckStandardItem> selectByParentId(String parentId) {
        QueryWrapper<EquipmentCheckStandardItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EquipmentCheckStandardItem::getParentId), parentId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(EquipmentCheckStandardItem::getSortNo));
        return list(queryWrapper);
    }
}


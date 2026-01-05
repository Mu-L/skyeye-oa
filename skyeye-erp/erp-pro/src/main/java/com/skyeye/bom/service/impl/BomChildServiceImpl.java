/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.dao.BomChildDao;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.bom.entity.BomProcedureConsumables;
import com.skyeye.bom.service.BomChildService;
import com.skyeye.bom.service.BomProcedureConsumablesService;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: BomChildServiceImpl
 * @Description: bom表子件清单服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/27 14:47
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "bom表子件清单", groupName = "bom清单管理", manageShow = false)
public class BomChildServiceImpl extends SkyeyeBusinessServiceImpl<BomChildDao, BomChild> implements BomChildService {

    @Autowired
    private BomProcedureConsumablesService bomProcedureConsumablesService;

    @Override
    protected void createPrepose(List<BomChild> entity) {
        BomChild child = entity.stream().findFirst().orElse(new BomChild());
        deleteBomChildByBomId(child.getBomId());
    }

    @Override
    protected void createPostpose(List<BomChild> entity, String userId) {
        if (CollectionUtil.isEmpty(entity)) {
            return;
        }
        String bomId = entity.get(0).getBomId();
        // 保存工序耗材信息
        List<BomProcedureConsumables> list = new ArrayList<>();
        entity.forEach(bomChild -> {
            if (CollectionUtil.isNotEmpty(bomChild.getProcedureConsumablesList())) {
                bomChild.getProcedureConsumablesList().forEach(bomConsumables -> {
                    bomConsumables.setBomChildId(bomChild.getId());
                    bomConsumables.setBomId(bomChild.getBomId());
                });
                list.addAll(bomChild.getProcedureConsumablesList());
            }
        });
        bomProcedureConsumablesService.saveList(bomId, list);

    }

    @Override
    public void deleteBomChildByBomId(String bomId) {
        if (StrUtil.isEmpty(bomId)) {
            return;
        }
        // 先查询所有子件ID，用于删除工序耗材
        QueryWrapper<BomChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BomChild::getBomId), bomId);
        // 删除BOM子件
        remove(queryWrapper);
        // 删除工序耗材
        bomProcedureConsumablesService.deleteByBomId(bomId);
    }

    @Override
    public List<BomChild> queryBomChildByBomId(String bomId) {
        QueryWrapper<BomChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BomChild::getBomId), bomId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(BomChild::getOrderBy));
        List<BomChild> bomChildren = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(bomChildren)) {
            // 设置耗材信息
            List<String> bomChildIdList = bomChildren.stream().map(BomChild::getId).collect(Collectors.toList());
            Map<String, List<BomProcedureConsumables>> listMap = bomProcedureConsumablesService.queryListByBomChildIds(bomChildIdList);
            bomChildren.forEach(bomChild -> {
                bomChild.setProcedureConsumablesList(listMap.get(bomChild.getId()));
            });
        }
        return bomChildren;
    }

    @Override
    public Map<String, List<BomChild>> queryBomChildByBomId(List<String> bomIds) {
        QueryWrapper<BomChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(BomChild::getBomId), bomIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(BomChild::getOrderBy));
        List<BomChild> bomChildren = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(bomChildren)) {
            // 设置耗材信息
            List<String> bomChildIdList = bomChildren.stream().map(BomChild::getId).collect(Collectors.toList());
            Map<String, List<BomProcedureConsumables>> listMap = bomProcedureConsumablesService.queryListByBomChildIds(bomChildIdList);
            bomChildren.forEach(bomChild -> {
                bomChild.setProcedureConsumablesList(listMap.get(bomChild.getId()));
            });
        }
        Map<String, List<BomChild>> listMap = bomChildren.stream().collect(Collectors.groupingBy(BomChild::getBomId));
        return listMap;
    }
}

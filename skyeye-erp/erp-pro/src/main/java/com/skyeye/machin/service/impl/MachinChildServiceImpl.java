/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.entity.Bom;
import com.skyeye.bom.service.BomService;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.machin.dao.MachinChildDao;
import com.skyeye.machin.entity.MachinChild;
import com.skyeye.machin.service.MachinChildService;
import com.skyeye.machinprocedure.entity.MachinProcedure;
import com.skyeye.machinprocedure.service.MachinProcedureService;
import com.skyeye.procedure.entity.WayProcedure;
import com.skyeye.procedure.service.WayProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MachinChildServiceImpl
 * @Description: 加工单子单据服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/10/3 13:29
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "加工单子单据", groupName = "加工单管理", manageShow = false)
public class MachinChildServiceImpl extends SkyeyeBusinessServiceImpl<MachinChildDao, MachinChild> implements MachinChildService {

    @Autowired
    private WayProcedureService wayProcedureService;

    @Autowired
    private BomService bomService;

    @Autowired
    private MachinProcedureService machinProcedureService;

    @Override
    public void deleteByParentId(String parentId) {
        QueryWrapper<MachinChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinChild::getParentId), parentId);
        remove(queryWrapper);
        // 删除加工单子单据工序信息
        machinProcedureService.deleteByParentId(parentId);
    }

    @Override
    public List<MachinChild> selectByParentId(String parentId) {
        QueryWrapper<MachinChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MachinChild::getParentId), parentId);
        List<MachinChild> machinChildList = list(queryWrapper);
        return machinChildList;
    }

    @Override
    public List<MachinChild> selectByParentId(List<String> parentIds) {
        QueryWrapper<MachinChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(MachinChild::getParentId), parentIds);
        List<MachinChild> machinChildList = list(queryWrapper);
        return machinChildList;
    }

    @Override
    public Map<String, List<MachinChild>> selectMapByParentId(List<String> parentIds) {
        if (CollectionUtil.isEmpty(parentIds)) {
            return MapUtil.newHashMap();
        }
        List<MachinChild> machinChildList = selectByParentId(parentIds);
        return machinChildList.stream().collect(Collectors.groupingBy(MachinChild::getParentId));
    }

    @Override
    public void saveList(String parentId, List<MachinChild> machinChildList) {
        deleteByParentId(parentId);
        if (CollectionUtil.isNotEmpty(machinChildList)) {
            for (MachinChild machinChild : machinChildList) {
                machinChild.setParentId(parentId);
            }
            createEntity(machinChildList, StrUtil.EMPTY);
        }
    }

    @Override
    public void createPostpose(List<MachinChild> machinChildList, String userId) {
        // 加工单id
        String machinId = machinChildList.stream().findFirst().get().getParentId();
        // 加工单子单据工序信息对象
        List<MachinProcedure> machinProcedureList = new ArrayList<>();
        machinChildList.forEach(machinChild -> {
            // 设置加工单子单据的工序信息
            resetMachinProcedure(machinProcedureList, machinChild.getWayProcedureId(), machinChild.getMaterialId(), machinChild.getNormsId(),
                machinChild.getId(), StrUtil.EMPTY);
            // 设置加工单子单据bom清单的工序信息
            if (StrUtil.isNotEmpty(machinChild.getBomId())) {
                Bom bom = bomService.selectById(machinChild.getBomId());
                bom.getBomChildList().forEach(bomChild -> {
                    resetMachinProcedure(machinProcedureList, bomChild.getWayProcedureId(), bomChild.getMaterialId(), bomChild.getNormsId(),
                        machinChild.getId(), bomChild.getId());
                });
            }
        });
        machinProcedureService.saveList(machinId, machinProcedureList);
    }

    private void resetMachinProcedure(List<MachinProcedure> machinProcedureList, String wayProcedureId,
                                      String materialId, String normsId, String childId, String bomChildId) {
        if (StrUtil.isNotEmpty(wayProcedureId)) {
            // 获取工艺信息
            WayProcedure wayProcedure = wayProcedureService.selectById(wayProcedureId);
            wayProcedure.getWorkProcedureList().forEach(wayProcedureChild -> {
                MachinProcedure machinProcedure = new MachinProcedure();
                machinProcedure.setChildId(childId);
                machinProcedure.setBomChildId(bomChildId);
                machinProcedure.setMaterialId(materialId);
                machinProcedure.setNormsId(normsId);
                machinProcedure.setWayProcedureId(wayProcedureId);
                machinProcedure.setProcedureId(wayProcedureChild.getProcedureId());
                machinProcedure.setOrderBy(wayProcedureChild.getOrderBy());
                machinProcedureList.add(machinProcedure);
            });
        }
    }
}

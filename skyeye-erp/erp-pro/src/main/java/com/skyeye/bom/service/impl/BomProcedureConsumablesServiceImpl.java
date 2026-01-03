/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.dao.BomProcedureConsumablesDao;
import com.skyeye.bom.entity.BomProcedureConsumables;
import com.skyeye.bom.service.BomProcedureConsumablesService;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: BomProcedureConsumablesServiceImpl
 * @Description: BOM工序耗材服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/03
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "BOM工序耗材", groupName = "bom清单管理", manageShow = false)
public class BomProcedureConsumablesServiceImpl extends SkyeyeBusinessServiceImpl<BomProcedureConsumablesDao, BomProcedureConsumables> implements BomProcedureConsumablesService {

    @Override
    public List<BomProcedureConsumables> queryListByBomChildId(String bomChildId) {
        if (StrUtil.isEmpty(bomChildId)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<BomProcedureConsumables> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BomProcedureConsumables::getBomChildId), bomChildId);
        List<BomProcedureConsumables> list = list(queryWrapper);
        return list;
    }

    @Override
    public Map<String, List<BomProcedureConsumables>> queryListByBomChildIds(List<String> bomChildIds) {
        if (CollectionUtil.isEmpty(bomChildIds)) {
            return new HashMap<>();
        }
        QueryWrapper<BomProcedureConsumables> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(BomProcedureConsumables::getBomChildId), bomChildIds);
        List<BomProcedureConsumables> list = list(queryWrapper);
        return list.stream().collect(Collectors.groupingBy(BomProcedureConsumables::getBomChildId));
    }

    @Override
    public void deleteByBomId(String bomId) {
        if (StrUtil.isEmpty(bomId)) {
            return;
        }
        QueryWrapper<BomProcedureConsumables> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BomProcedureConsumables::getBomChildId), bomId);
        remove(queryWrapper);
    }

    @Override
    public void saveList(String bomId, List<BomProcedureConsumables> consumablesList) {
        // 先删除原有的
        deleteByBomId(bomId);
        // 保存新的
        if (CollectionUtil.isNotEmpty(consumablesList)) {
            saveBatch(consumablesList);
        }
    }

}


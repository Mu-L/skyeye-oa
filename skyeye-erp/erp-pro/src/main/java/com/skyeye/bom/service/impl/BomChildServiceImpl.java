/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.bom.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.bom.dao.BomChildDao;
import com.skyeye.bom.entity.BomChild;
import com.skyeye.bom.service.BomChildService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.material.entity.MaterialNorms;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.procedure.entity.WayProcedure;
import com.skyeye.procedure.service.WayProcedureService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
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
    private MaterialNormsService materialNormsService;

    @Autowired
    private WayProcedureService wayProcedureService;

    /**
     * 计算耗材总费用
     *
     * @param bomChildList
     * @return
     */
    @Override
    public String calcConsumablesPrice(List<BomChild> bomChildList) {
        String allConsumablesPrice = "0";
        // 获取规格id
        List<String> normsIds = bomChildList.stream().map(BomChild::getNormsId).collect(Collectors.toList());
        Map<String, MaterialNorms> materialNormsMap = materialNormsService.selectMapByIds(normsIds);
        for (BomChild bomChild : bomChildList) {
            MaterialNorms materialNorms = materialNormsMap.get(bomChild.getNormsId());
            // 单个子件耗材总费用  所需要的数量 * 成本价
            String consumablesPrice = CalculationUtil.multiply(String.valueOf(bomChild.getNeedNum()), materialNorms.getEstimatePurchasePrice());
            bomChild.setConsumablesPrice(consumablesPrice);
            allConsumablesPrice = CalculationUtil.add(allConsumablesPrice, consumablesPrice, CommonNumConstants.NUM_TWO);
        }
        return allConsumablesPrice;
    }

    @Override
    public String calcProcedurePrice(List<BomChild> bomChildList) {
        String allProcedurePrice = "0";
        // 查询工艺信息
        List<String> wayProcedureIdList = bomChildList.stream()
            .filter(bean -> StrUtil.isNotEmpty(bean.getWayProcedureId()))
            .map(BomChild::getWayProcedureId).distinct().collect(Collectors.toList());
        Map<String, WayProcedure> wayProcedureMap = new HashMap<>();
        if (CollectionUtil.isNotEmpty(wayProcedureIdList)) {
            wayProcedureMap = wayProcedureService.selectMapByIds(wayProcedureIdList);
        }
        for (BomChild bomChild : bomChildList) {
            String allPrice = bomChild.getConsumablesPrice();
            if (StrUtil.isEmpty(bomChild.getWayProcedureId())) {
                bomChild.setAllPrice(allPrice);
                continue;
            }
            WayProcedure wayProcedure = wayProcedureMap.get(bomChild.getWayProcedureId());
            if (ObjectUtil.isEmpty(wayProcedure)) {
                bomChild.setAllPrice(allPrice);
                continue;
            }
            allProcedurePrice = CalculationUtil.add(allProcedurePrice, wayProcedure.getAllPrice(), CommonNumConstants.NUM_TWO);
            // 子件清单总价
            allPrice = CalculationUtil.add(allPrice, wayProcedure.getAllPrice(), CommonNumConstants.NUM_TWO);
            bomChild.setAllPrice(allPrice);
        }
        return allProcedurePrice;
    }

    @Override
    protected void createPrepose(List<BomChild> entity) {
        BomChild child = entity.stream().findFirst().orElse(new BomChild());
        deleteBomChildByBomId(child.getBomId());
    }

    @Override
    public void deleteBomChildByBomId(String bomId) {
        if (StrUtil.isEmpty(bomId)) {
            return;
        }
        QueryWrapper<BomChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BomChild::getBomId), bomId);
        remove(queryWrapper);
    }

    @Override
    public List<BomChild> queryBomChildByBomId(String bomId) {
        QueryWrapper<BomChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(BomChild::getBomId), bomId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(BomChild::getOrderBy));
        List<BomChild> bomChildren = list(queryWrapper);
        return bomChildren;
    }

    @Override
    public Map<String, List<BomChild>> queryBomChildByBomId(List<String> bomIds) {
        QueryWrapper<BomChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(BomChild::getBomId), bomIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(BomChild::getOrderBy));
        List<BomChild> bomChildren = list(queryWrapper);
        Map<String, List<BomChild>> listMap = bomChildren.stream().collect(Collectors.groupingBy(BomChild::getBomId));
        return listMap;
    }
}

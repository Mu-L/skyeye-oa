/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.procedure.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.procedure.dao.WayProcedureChildDao;
import com.skyeye.procedure.entity.WayProcedureChild;
import com.skyeye.procedure.service.WayProcedureChildService;
import org.springframework.stereotype.Service;

import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: WayProcedureChildServiceImpl
 * @Description: 工艺路线关联的工序服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/26 20:25
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "工艺路线关联的工序", groupName = "工艺路线管理", manageShow = false)
public class WayProcedureChildServiceImpl extends SkyeyeBusinessServiceImpl<WayProcedureChildDao, WayProcedureChild> implements WayProcedureChildService {

    @Override
    public void deleteWayProcedureByWayId(String wayId) {
        QueryWrapper<WayProcedureChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WayProcedureChild::getWayId), wayId);
        remove(queryWrapper);
    }

    @Override
    public void saveWayProcedure(String wayId, List<WayProcedureChild> wayProcedureList, String userId) {
        deleteWayProcedureByWayId(wayId);
        if (CollectionUtil.isNotEmpty(wayProcedureList)) {
            for (WayProcedureChild wayProcedureChild : wayProcedureList) {
                wayProcedureChild.setWayId(wayId);
                // 保存时计算并设置标准工时(分钟/件)
                wayProcedureChild.setStandardTimeMinutes(calcStandardTimeMinutes(wayProcedureChild));
            }
            createEntity(wayProcedureList, userId);
        }
    }

    @Override
    public List<WayProcedureChild> queryWayProcedureByWayId(String wayId) {
        QueryWrapper<WayProcedureChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(WayProcedureChild::getWayId), wayId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(WayProcedureChild::getOrderBy));
        List<WayProcedureChild> wayProcedureList = list(queryWrapper);
        return wayProcedureList;
    }

    @Override
    public String calcOrderAllTotalPrice(List<WayProcedureChild> wayProcedureChildList) {
        String totalPrice = "0";
        for (WayProcedureChild wayProcedureChild : wayProcedureChildList) {
            // 计算子单据总价：单价相加
            totalPrice = CalculationUtil.add(totalPrice, wayProcedureChild.getPrice());
        }
        return totalPrice;
    }

    @Override
    public Map<String, List<WayProcedureChild>> queryWayProcedureByWayId(List<String> wayIds) {
        QueryWrapper<WayProcedureChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(WayProcedureChild::getWayId), wayIds);
        List<WayProcedureChild> wayProcedureChildList = list(queryWrapper);
        Map<String, List<WayProcedureChild>> listMap = wayProcedureChildList.stream().collect(Collectors.groupingBy(WayProcedureChild::getWayId));
        return listMap;
    }

    /**
     * 根据定额能力计算标准工时(分钟/件)：60/定额能力(件/小时)
     */
    private String calcStandardTimeMinutes(WayProcedureChild wayProcedureChild) {
        if (wayProcedureChild == null) {
            return null;
        }
        Integer quotaCapacity = wayProcedureChild.getQuotaCapacity();
        if (quotaCapacity == null || quotaCapacity <= 0) {
            return null;
        }
        return CalculationUtil.divide("60", String.valueOf(quotaCapacity), 4, RoundingMode.HALF_UP);
    }

}

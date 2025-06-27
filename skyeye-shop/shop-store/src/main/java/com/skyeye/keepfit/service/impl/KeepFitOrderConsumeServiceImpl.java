/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.keepfit.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.exception.CustomException;
import com.skyeye.keepfit.dao.KeepFitOrderConsumeDao;
import com.skyeye.keepfit.entity.KeepFitOrderConsume;
import com.skyeye.keepfit.service.KeepFitOrderConsumeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: KeepFitOrderConsumeServiceImpl
 * @Description: 保养订单关联耗材服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 20:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "保养订单关联耗材", groupName = "保养订单管理", manageShow = false)
public class KeepFitOrderConsumeServiceImpl extends SkyeyeBusinessServiceImpl<KeepFitOrderConsumeDao, KeepFitOrderConsume> implements KeepFitOrderConsumeService {

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Override
    public String calculationTotalPrice(List<KeepFitOrderConsume> keepFitOrderConsumeList) {
        String payablePrice = "0";
        if (CollectionUtil.isNotEmpty(keepFitOrderConsumeList)) {
            List<String> normsId = keepFitOrderConsumeList.stream()
                .map(KeepFitOrderConsume::getNormsId).distinct().collect(Collectors.toList());
            Map<String, Map<String, Object>> normsMap = iMaterialNormsService
                .queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(normsId));

            for (KeepFitOrderConsume bean : keepFitOrderConsumeList) {
                Map<String, Object> norms = normsMap.get(bean.getNormsId());
                if (CollectionUtil.isEmpty(norms) || StrUtil.isEmpty(norms.get("retailPrice").toString())) {
                    throw new CustomException("耗材不存在，请刷新后重试.");
                }
                String retailPrice = norms.get("retailPrice").toString();
                bean.setUnitPrice(retailPrice);
                String allPrice = CalculationUtil.multiply(CommonNumConstants.NUM_TWO, String.valueOf(bean.getOperNumber()), retailPrice);
                bean.setAllPrice(allPrice);
                payablePrice = CalculationUtil.add(payablePrice, allPrice, CommonNumConstants.NUM_TWO);
            }
        }
        return payablePrice;
    }

    @Override
    public void deleteByOrderId(String orderId) {
        QueryWrapper<KeepFitOrderConsume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KeepFitOrderConsume::getOrderId), orderId);
        remove(queryWrapper);
    }

    @Override
    public List<KeepFitOrderConsume> selectByOrderId(String orderId) {
        QueryWrapper<KeepFitOrderConsume> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(KeepFitOrderConsume::getOrderId), orderId);
        List<KeepFitOrderConsume> keepFitOrderConsumeList = list(queryWrapper);
        return keepFitOrderConsumeList;
    }

    @Override
    public void saveList(String orderId, List<KeepFitOrderConsume> keepFitOrderConsumeList) {
        deleteByOrderId(orderId);
        if (CollectionUtil.isNotEmpty(keepFitOrderConsumeList)) {
            for (KeepFitOrderConsume keepFitOrderConsume : keepFitOrderConsumeList) {
                keepFitOrderConsume.setOrderId(orderId);
            }
            createEntity(keepFitOrderConsumeList, StrUtil.EMPTY);
        }
    }

    @Override
    public List<KeepFitOrderConsume> selectByOrderIds(List<String> keepFitOrderIdList) {
        List<KeepFitOrderConsume> keepFitOrderConsumeList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(keepFitOrderIdList)){
            QueryWrapper<KeepFitOrderConsume> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(MybatisPlusUtil.toColumns(KeepFitOrderConsume::getOrderId), keepFitOrderIdList);
            keepFitOrderConsumeList =  list(queryWrapper);
        }
        return keepFitOrderConsumeList;
    }
}

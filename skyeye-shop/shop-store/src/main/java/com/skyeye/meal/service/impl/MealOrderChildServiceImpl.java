/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.meal.dao.MealOrderChildDao;
import com.skyeye.meal.entity.MealOrderChild;
import com.skyeye.meal.entity.ShopMeal;
import com.skyeye.meal.service.MealOrderChildService;
import com.skyeye.meal.service.MealOrderService;
import com.skyeye.meal.service.MealRefundOrderService;
import com.skyeye.meal.service.ShopMealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: MealOrderChildServiceImpl
 * @Description: 套餐订单所选套餐服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 9:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "套餐订单所选套餐", groupName = "套餐订单管理")
public class MealOrderChildServiceImpl extends SkyeyeBusinessServiceImpl<MealOrderChildDao, MealOrderChild> implements MealOrderChildService {

    @Autowired
    private ShopMealService shopMealService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private MealOrderService mealOrderService;

    @Autowired
    private MealRefundOrderService mealRefundOrderService;

    @Override
    public String calculationTotalPrice(String objectId, String objectKey, List<MealOrderChild> mealOrderChildList) {
        String payablePrice = CommonNumConstants.NUM_ZERO.toString();
        List<String> mealIds = mealOrderChildList.stream().map(MealOrderChild::getMealId).distinct().collect(Collectors.toList());
        Map<String, ShopMeal> shopMealMap = shopMealService.selectMapByIds(mealIds);
        for (MealOrderChild bean : mealOrderChildList) {
            bean.setObjectId(objectId);
            bean.setObjectKey(objectKey);
            ShopMeal shopMeal = shopMealMap.get(bean.getMealId());
            if (ObjectUtil.isEmpty(shopMeal) || StrUtil.isEmpty(shopMeal.getId())) {
                throw new CustomException("套餐不存在，请刷新后重试.");
            }
            bean.setMealPrice(shopMeal.getShowPrice());
            payablePrice = CalculationUtil.add(payablePrice, shopMeal.getShowPrice(), CommonNumConstants.NUM_TWO);
        }
        return payablePrice;
    }

    @Override
    public void deleteByOrderId(String orderId) {
        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getOrderId), orderId);
        remove(queryWrapper);
    }

    @Override
    public List<MealOrderChild> selectByOrderId(String orderId) {
        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getOrderId), orderId);
        return list(queryWrapper);
    }

    @Override
    public void saveList(String orderId, List<MealOrderChild> mealOrderChildList) {
        deleteByOrderId(orderId);
        if (CollectionUtil.isNotEmpty(mealOrderChildList)) {
            for (MealOrderChild mealOrderChild : mealOrderChildList) {
                mealOrderChild.setOrderId(orderId);
                mealOrderChild.setState(WhetherEnum.DISABLE_USING.getKey());
            }
            createEntity(mealOrderChildList, StrUtil.EMPTY);
        }
    }

    @Override
    public void queryMealMationByObjectId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());

        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getObjectId), commonPageInfo.getObjectId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(MealOrderChild::getStartTime));
        List<MealOrderChild> mealOrderChildList = list(queryWrapper);
        shopMealService.setDataMation(mealOrderChildList, MealOrderChild::getMealId);
        // 产品信息
        iMaterialService.setDataMation(mealOrderChildList, MealOrderChild::getMaterialId);
        // 规格信息
        iMaterialNormsService.setDataMation(mealOrderChildList, MealOrderChild::getNormsId);
        // 设置是否下达退款单
        mealRefundOrderService.setWhetherMealRefundOrder(mealOrderChildList);
        outputObject.setBeans(mealOrderChildList);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void updateStateISUseByOrderId(String orderId) {
        UpdateWrapper<MealOrderChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getOrderId), orderId);
        updateWrapper.set(MybatisPlusUtil.toColumns(MealOrderChild::getState), WhetherEnum.ENABLE_USING.getKey());
        update(updateWrapper);
    }

    @Override
    public void updateStateISNotUseById(String id) {
        UpdateWrapper<MealOrderChild> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(MealOrderChild::getState), WhetherEnum.DISABLE_USING.getKey());
        update(updateWrapper);
        // 刷新缓存
        MealOrderChild mealOrderChild = selectById(id);
        mealOrderService.refreshCache(mealOrderChild.getOrderId());
    }

    @Override
    public void queryMealMationByMaterial(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String objectId = params.get("objectId").toString();
        String materialId = params.get("materialId").toString();
        String normsId = params.get("normsId").toString();
        String codeNum = params.get("codeNum").toString();

        QueryWrapper<MealOrderChild> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getMaterialId), materialId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getNormsId), normsId);
        if (StrUtil.isNotEmpty(codeNum)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getCodeNum), codeNum);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrderChild::getState), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(MealOrderChild::getStartTime));
        List<MealOrderChild> mealOrderChildList = list(queryWrapper);
        shopMealService.setDataMation(mealOrderChildList, MealOrderChild::getMealId);
        // 产品信息
        iMaterialService.setDataMation(mealOrderChildList, MealOrderChild::getMaterialId);
        // 规格信息
        iMaterialNormsService.setDataMation(mealOrderChildList, MealOrderChild::getNormsId);
        outputObject.setBeans(mealOrderChildList);
        outputObject.settotal(mealOrderChildList.size());
    }

}

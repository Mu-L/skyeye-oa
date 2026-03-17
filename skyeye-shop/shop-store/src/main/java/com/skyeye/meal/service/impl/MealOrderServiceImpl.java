/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import com.skyeye.exception.CustomException;
import com.skyeye.meal.classenum.ShopMealOrderState;
import com.skyeye.meal.dao.MealOrderDao;
import com.skyeye.meal.entity.MealOrder;
import com.skyeye.meal.entity.MealOrderChild;
import com.skyeye.meal.service.MealOrderChildService;
import com.skyeye.meal.service.MealOrderService;
import com.skyeye.meal.service.ShopMealService;
import com.skyeye.service.MemberService;
import com.skyeye.store.service.ShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MealOrderServiceImpl
 * @Description: 套餐订单管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/6 19:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "套餐订单管理", groupName = "套餐订单管理")
public class MealOrderServiceImpl extends SkyeyeBusinessServiceImpl<MealOrderDao, MealOrder> implements MealOrderService {

    @Autowired
    private ShopMealService shopMealService;

    @Autowired
    private MealOrderChildService mealOrderChildService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Autowired
    private ShopStoreService shopStoreService;

    @Autowired
    private MemberService memberService;

    @Override
    public QueryWrapper<MealOrder> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<MealOrder> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.equals(commonPageInfo.getType(), "Store")) {
            // 门店下的订单
            queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrder::getStoreId), commonPageInfo.getHolderId());
        } else if (StrUtil.equals(commonPageInfo.getType(), "All")) {
            // 所有订单
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        shopStoreService.setMationForMap(beans, "storeId", "storeMation");
        iSysDictDataService.setNameForMap(beans, "natureId", "natureName");
        memberService.setMationForMap(beans, "objectId", "objectMation");
        return beans;
    }

    @Override
    public void createPrepose(MealOrder entity) {
        entity.setState(ShopMealOrderState.NO_PAYING.getKey());
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        entity.setPayPrice(null);
        entity.setPayTime(null);
        // 计算应付金额
        String payablePrice = mealOrderChildService.calculationTotalPrice(entity.getObjectId(), entity.getObjectKey(), entity.getMealList());
        entity.setPayablePrice(payablePrice);
    }

    @Override
    public void createPostpose(MealOrder entity, String userId) {
        // 保存订单与套餐的关系表
        mealOrderChildService.saveList(entity.getId(), entity.getMealList());
    }

    @Override
    public void deletePostpose(String id) {
        mealOrderChildService.deleteByOrderId(id);
    }

    @Override
    public MealOrder getDataFromDb(String id) {
        MealOrder mealOrder = super.getDataFromDb(id);
        mealOrder.setMealList(mealOrderChildService.selectByOrderId(id));
        return mealOrder;
    }

    @Override
    public MealOrder selectById(String id) {
        MealOrder mealOrder = super.selectById(id);
        iSysDictDataService.setDataMation(mealOrder, MealOrder::getNatureId);
        iAuthUserService.setDataMation(mealOrder, MealOrder::getCreateId);
        mealOrder.getMealList().forEach(mealOrderChild -> {
            Map<String, Object> codeNumMation = new HashMap<>();
            codeNumMation.put("name", mealOrderChild.getCodeNum());
            mealOrderChild.setCodeNumMation(codeNumMation);
        });

        shopMealService.setDataMation(mealOrder.getMealList(), MealOrderChild::getMealId);
        // 产品信息
        iMaterialService.setDataMation(mealOrder.getMealList(), MealOrderChild::getMaterialId);
        // 规格信息
        iMaterialNormsService.setDataMation(mealOrder.getMealList(), MealOrderChild::getNormsId);
        // 会员信息
        memberService.setDataMation(mealOrder, MealOrder::getObjectId);
        // 门店信息
        shopStoreService.setDataMation(mealOrder, MealOrder::getStoreId);
        return mealOrder;
    }

    /**
     * 支付订单完成后的回调
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void mealOrderNotify(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String outTradeNo = params.get("outTradeNo").toString();
        // 实际支付的订单金额:单位 分
        String totalFee = params.get("totalFee").toString();
        // 转为元
        totalFee = CalculationUtil.divide(totalFee, "100", CommonNumConstants.NUM_TWO);
        MealOrder mealOrder = queryMealOrderByOddNumber(outTradeNo);
        if (mealOrder.getState() == ShopMealOrderState.NO_PAYING.getKey()) {
            UpdateWrapper<MealOrder> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, mealOrder.getId());
            updateWrapper.set(MybatisPlusUtil.toColumns(MealOrder::getState), ShopMealOrderState.PAY.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(MealOrder::getPayPrice), totalFee);
            updateWrapper.set(MybatisPlusUtil.toColumns(MealOrder::getPayTime), DateUtil.getTimeAndToString());
            update(updateWrapper);
            // 更新套餐子订单状态为可以使用
            mealOrderChildService.updateStateISUseByOrderId(mealOrder.getId());
            refreshCache(mealOrder.getId());
        } else {
            throw new CustomException("订单状态已改变，不允许支付.");
        }
    }

    private MealOrder queryMealOrderByOddNumber(String oddNumber) {
        QueryWrapper<MealOrder> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MealOrder::getOddNumber), oddNumber);
        MealOrder mealOrder = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(mealOrder)) {
            throw new CustomException("订单不存在");
        }
        return mealOrder;
    }

    @Override
    public void deletePreExecution(MealOrder entity) {
        if (entity.getState() != ShopMealOrderState.NO_PAYING.getKey()) {
            throw new CustomException("该订单状态不允许删除.");
        }
    }

    /**
     * 套餐订单状态修改
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void updateMealOrderState(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        Integer state = Integer.parseInt(params.get("state").toString());
        editOrderStateById(id, state);
    }

    @Override
    public void editOrderStateById(String id, Integer state) {
        UpdateWrapper<MealOrder> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(MealOrder::getState), state);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryMealOrderListByCodeNum(InputObject inputObject, OutputObject outputObject) {
        String codeNum = inputObject.getParams().get("codeNum").toString();
        List<MealOrderChild> mealOrderChildList = mealOrderChildService.queryListByCodeNum(codeNum);
        shopMealService.setDataMation(mealOrderChildList, MealOrderChild::getMealId);
        List<Map<String, String>> beans = new ArrayList<>();
        for (MealOrderChild mealOrderChild : mealOrderChildList) {
            if (ObjectUtil.isNotEmpty(mealOrderChild.getMealMation())) {
                Map<String, String> bean = new HashMap<>();
                bean.put("id", mealOrderChild.getId());
                bean.put("name", mealOrderChild.getMealMation().getName());
                beans.add(bean);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

}

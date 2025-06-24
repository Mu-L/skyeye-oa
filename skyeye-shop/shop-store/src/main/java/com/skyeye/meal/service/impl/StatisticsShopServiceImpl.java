/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.eve.service.ISysDictDataService;
import com.skyeye.keepfit.classenum.KeepFitOrderState;
import com.skyeye.meal.classenum.ShopMealOrderState;
import com.skyeye.meal.dao.StatisticsShopDao;
import com.skyeye.meal.service.StatisticsShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: StatisticsShopServiceImpl
 * @Description: 商城统计服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/12 23:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "商城统计", groupName = "商城统计", manageShow = false)
public class StatisticsShopServiceImpl implements StatisticsShopService {

    @Autowired
    private StatisticsShopDao statisticsShopDao;

    @Autowired
    private ISysDictDataService iSysDictDataService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    /**
     * 统计分析
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryStatisticsShop(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        params.put("tenantId", tenantId);
        // 套餐订单状态
        params.put("mealStateList", Arrays.asList(ShopMealOrderState.PAY.getKey()));
        // 保养订单状态
        params.put("keepFitStateList", Arrays.asList(KeepFitOrderState.PAY.getKey(), KeepFitOrderState.FIT_COMPLATE.getKey(),
            KeepFitOrderState.PAY_VERIFICATION.getKey()));
        Map<String, Object> result = new HashMap<>();
        // 获取指定日期范围内的购买套餐(已支付)的会员数
        String mealOrderMemberByNum = statisticsShopDao.queryMealOrderMemberByNum(params);
        result.put("mealOrderMemberByNum", mealOrderMemberByNum);
        // 获取指定日期范围内的购买套餐(已支付)的数量
        String mealOrderNum = statisticsShopDao.queryMealOrderNum(params);
        result.put("mealOrderNum", mealOrderNum);
        // 获取指定日期范围内的保养订单(已支付，保养完成，已核销)的数量
        String keepFitOrderNum = statisticsShopDao.queryKeepFitOrderNum(params);
        result.put("keepFitOrderNum", keepFitOrderNum);
        // 获取指定日期范围内的保养订单(已支付，保养完成，已核销)的金额
        String keepFitOrderPrice = statisticsShopDao.queryKeepFitOrderPrice(params);
        result.put("keepFitOrderPrice", keepFitOrderPrice);
        String startTime = params.get("startTime").toString();
        String endTime = params.get("endTime").toString();
        List<String> month = DateUtil.getMonth(startTime, endTime);
        params.put("month", month);
        // 按年月获取指定日期范围内的购买套餐(已支付)的数量
        List<Map<String, Object>> monthMealOrderNum = statisticsShopDao.queryMonthMealOrderNum(params);
        result.put("monthMealOrderNum", monthMealOrderNum);
        // 按年月获取指定日期范围内的保养订单(已支付，保养完成，已核销)的数量
        List<Map<String, Object>> monthKeepFitOrderNum = statisticsShopDao.queryMonthKeepFitOrderNum(params);
        result.put("monthKeepFitOrderNum", monthKeepFitOrderNum);
        // 按年月获取指定日期范围内门店的购买套餐(已支付)的数量
        List<Map<String, Object>> storeMealOrderNum = statisticsShopDao.queryStoreMealOrderNum(params);
        result.put("storeMealOrderNum", storeMealOrderNum);
        // 按年月获取指定日期范围内门店的保养订单(已支付，保养完成，已核销)的数量
        List<Map<String, Object>> storeKeepFitOrderNum = statisticsShopDao.queryStoreKeepFitOrderNum(params);
        result.put("storeKeepFitOrderNum", storeKeepFitOrderNum);
        // 按年月获取指定日期范围内性质的套餐订单(已支付)的数量
        List<Map<String, Object>> natureMealOrderNum = statisticsShopDao.queryNatureMealOrderNum(params);
        iSysDictDataService.setNameForMap(natureMealOrderNum, "natureId", "name");
        result.put("natureMealOrderNum", natureMealOrderNum);

        outputObject.setBean(result);
    }
}

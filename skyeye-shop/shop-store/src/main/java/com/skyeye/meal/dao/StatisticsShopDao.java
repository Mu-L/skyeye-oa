/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.dao;

import com.skyeye.annotation.tenant.IgnoreTenant;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: StatisticsShopDao
 * @Description: 商城统计数据接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/13 19:32
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface StatisticsShopDao {

    @IgnoreTenant
    String queryMealOrderMemberByNum(Map<String, Object> params);

    @IgnoreTenant
    String queryMealOrderNum(Map<String, Object> params);

    @IgnoreTenant
    String queryKeepFitOrderNum(Map<String, Object> params);

    @IgnoreTenant
    String queryKeepFitOrderPrice(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> queryMonthMealOrderNum(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> queryStoreMealOrderNum(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> queryStoreKeepFitOrderNum(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> queryNatureMealOrderNum(Map<String, Object> params);

    @IgnoreTenant
    List<Map<String, Object>> queryMonthKeepFitOrderNum(Map<String, Object> params);
}

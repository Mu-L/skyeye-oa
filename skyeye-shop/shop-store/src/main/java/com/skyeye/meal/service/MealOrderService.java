/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.meal.entity.MealOrder;

/**
 * @ClassName: MealOrderService
 * @Description: 套餐订单管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2022/2/6 19:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MealOrderService extends SkyeyeBusinessService<MealOrder> {

    void mealOrderNotify(InputObject inputObject, OutputObject outputObject);

    void updateMealOrderState(InputObject inputObject, OutputObject outputObject);

    void editOrderStateById(String id, Integer state);

    void queryMealOrderListByCodeNum(InputObject inputObject, OutputObject outputObject);
}

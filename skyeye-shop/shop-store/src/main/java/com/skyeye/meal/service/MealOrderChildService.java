/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.meal.entity.MealOrderChild;

import java.util.List;

/**
 * @ClassName: MealOrderChildService
 * @Description: 套餐订单所选套餐服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 9:51
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface MealOrderChildService extends SkyeyeBusinessService<MealOrderChild> {

    String calculationTotalPrice(String objectId, String objectKey, List<MealOrderChild> mealOrderChildList);

    void deleteByOrderId(String orderId);

    List<MealOrderChild> selectByOrderId(String orderId);

    void saveList(String orderId, List<MealOrderChild> mealOrderChildList);

    void queryMealMationByObjectId(InputObject inputObject, OutputObject outputObject);

    void updateStateISUseByOrderId(String orderId);

    void updateStateISNotUseById(String id);

    void queryMealMationByMaterial(InputObject inputObject, OutputObject outputObject);

    List<MealOrderChild> queryListByCodeNum(String codeNum);
}

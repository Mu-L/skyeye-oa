/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.keepfit.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.keepfit.entity.KeepFitOrderConsume;

import java.util.List;

/**
 * @ClassName: KeepFitOrderConsumeService
 * @Description: 保养订单关联耗材服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 20:23
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface KeepFitOrderConsumeService extends SkyeyeBusinessService<KeepFitOrderConsume> {

    String calculationTotalPrice(List<KeepFitOrderConsume> keepFitOrderConsumeList);

    void deleteByOrderId(String orderId);

    List<KeepFitOrderConsume> selectByOrderId(String orderId);

    void saveList(String orderId, List<KeepFitOrderConsume> keepFitOrderConsumeList);

    List<KeepFitOrderConsume> selectByOrderIds(List<String> keepFitOrderIdList);
}

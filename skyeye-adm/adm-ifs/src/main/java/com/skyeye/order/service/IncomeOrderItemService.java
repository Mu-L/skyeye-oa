/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service;

import com.skyeye.base.business.service.SkyeyeLinkDataService;
import com.skyeye.order.entity.IncomeOrderItem;

import java.util.List;

/**
 * @ClassName: IncomeOrderItemService
 * @Description: 明细账子单据服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 10:15
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface IncomeOrderItemService extends SkyeyeLinkDataService<IncomeOrderItem> {

    /**
     * 计算单据信息的总价
     *
     * @param orderItemList
     * @return
     */
    String calcOrderAllTotalPrice(List<IncomeOrderItem> orderItemList);

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.service.impl;

import com.skyeye.base.business.service.impl.SkyeyeLinkDataServiceImpl;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.order.dao.IncomeOrderItemDao;
import com.skyeye.order.entity.IncomeOrderItem;
import com.skyeye.order.service.IncomeOrderItemService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: IncomeOrderItemServiceImpl
 * @Description: 明细账子单据服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/3/12 10:15
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class IncomeOrderItemServiceImpl extends SkyeyeLinkDataServiceImpl<IncomeOrderItemDao, IncomeOrderItem> implements IncomeOrderItemService {

    /**
     * 计算单据信息的总价
     *
     * @param orderItemList
     * @return
     */
    @Override
    public String calcOrderAllTotalPrice(List<IncomeOrderItem> orderItemList) {
        String totalPrice = "0";
        for (IncomeOrderItem orderItem : orderItemList) {
            // 计算子单据总价：单价相加
            totalPrice = CalculationUtil.add(totalPrice, orderItem.getEachAmount());
        }
        return totalPrice;
    }
}

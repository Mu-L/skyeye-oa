/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com
 * All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.meal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopMealOrderChildState
 * @Description: 套餐子订单状态（待支付/已支付可用/已用完）
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopMealOrderChildState implements SkyeyeEnumClass {

    WAIT_PAY(0, "待支付", true, true),
    CAN_USE(1, "已支付", true, false),
    USED_UP(2, "已用完", true, false),
    REFUNDED(3, "已退款", true, false),
    EXPIRED(4, "已过期", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}


/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TenantAppBuyOrderPayState
 * @Description: 租户应用购买订单支付状态
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantAppBuyOrderPayState implements SkyeyeEnumClass {

    UNPAID(0, "待支付", "orange", true, true),
    PAID(1, "已支付", "green", true, false),
    PAY_CANCELLED(2, "取消支付", "red", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}

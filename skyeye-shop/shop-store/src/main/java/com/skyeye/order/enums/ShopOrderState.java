/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopOrderState
 * @Description: 订单状态
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:33
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopOrderState implements SkyeyeEnumClass {

    UNSUBMIT(0, "未提交", true, false),
    SUBMIT(1, "已提交", true, false),
    UNPAID(2, "待支付", true, false),
    FAIRPAID(3, "支付失败", true, false),
    CANCELED(4, "已取消", true, false),
    UNDELIVERED(5, "待发货", true, false),
    DELIVERED(6, "已发货", true, false),
    TRANSPORTING(7, "运输中", true, false),
    SIGN(8, "已签收", true, false),
    COMPLETED(9, "已完成", true, false),
    UNEVALUATE(10, "待评价", true, false),
    EVALUATED(11, "已评价", true, false),
    REFUNDING(12, "退款中", true, false),
    REFUND(13, "已退款", true, false),
    SALESRETURNING(14, "退货中", true, false),
    SALESRETURNED(15, "已退货", true, false),
    EXCHANGEING(16, "换货中", true, false),
    EXCHANGED(17, "已换货", true, false),
    PARTIALLYDONE(18,"部分完成",true,false),
    PARTIALEVALUATION(19,"部分评价",true,false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

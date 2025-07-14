package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopOrderItemDeliverState
 * @Description: 订单子单据发货状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopOrderItemOtherState implements SkyeyeEnumClass {

    WAIT_PAY(0, "待支付",true,false),
    WAIT_DELIVER(1, "待发货",true,false),
    PART_DELIVERED(2, "部分发货",true,false),
    ALL_DELIVERED(3, "全部发货",true,false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

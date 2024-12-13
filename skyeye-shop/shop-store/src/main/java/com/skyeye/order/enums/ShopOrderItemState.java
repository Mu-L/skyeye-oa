package com.skyeye.order.enums;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ShopOrderCancelType
 * @Description: 订单取消类型
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ShopOrderItemState {
    DELIVERED(1, "已签收",true,false),
    FINISHED(2, "已完成",true,false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

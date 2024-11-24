/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: OrderCommentType
 * @Description: 订单评论类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:39
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum OrderCommentType implements SkyeyeEnumClass {
    CUSTOMERFiRST(0, "客户评价", true, false),
    CUSTOMERLATER(1, "客户追评", true, false),
    MERCHANT(2, "商家回复", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

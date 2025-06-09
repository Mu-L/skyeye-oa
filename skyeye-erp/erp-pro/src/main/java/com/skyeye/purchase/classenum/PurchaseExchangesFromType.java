package com.skyeye.purchase.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PurchaseExchangesFromType
 * @Description: 换货货单来源单据类型
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/22 10:58
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PurchaseExchangesFromType implements SkyeyeEnumClass {

    PURCHASE_ORDER(1, "采购订单", true, false),
    QUALITY_INSPECTION(2, "质检单", true, false),
    WHOLE_ORDER_OUT(3, "整单委外单", true, false),;

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

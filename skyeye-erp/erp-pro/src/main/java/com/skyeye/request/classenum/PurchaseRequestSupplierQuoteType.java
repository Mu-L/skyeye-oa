package com.skyeye.request.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PurchaseRequestSupplierQuoteType
 * @Description: 采购申请供应商报价类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/29
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PurchaseRequestSupplierQuoteType implements SkyeyeEnumClass {

    ALL_SUPPLIER("all", "全部供应商", true, true),
    SPECIFIED_SUPPLIER("specified", "指定供应商", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

}
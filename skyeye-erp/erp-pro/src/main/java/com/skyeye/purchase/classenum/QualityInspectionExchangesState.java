package com.skyeye.purchase.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: QualityInspectionReturnState
 * @Description: 质检单退货状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/5/31 11:54
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum QualityInspectionExchangesState implements SkyeyeEnumClass {

    NOT_NEED_EXCHANGES(1, "无需换货", true, true),
    NEED_EXCHANGES(2, "待换货", true, false),
    PARTIAL_EXCHANGES(3, "部分换货", true, false),
    COMPLATE_EXCHANGES(4, "全部换货", true, false);


    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
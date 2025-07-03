package com.skyeye.inspection.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

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

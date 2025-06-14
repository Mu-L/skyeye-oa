package com.skyeye.seal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SalesExchangesOutState implements SkyeyeEnumClass {

    NOT_NEED_PUT(1, "无需出库", "purple", true, true),
    NEED_PUT(2, "待出库", "blue", true, false),
    PARTIAL_PUT(3, "部分出库", "orange", true, false),
    COMPLATE_PUT(4, "全部出库", "green", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;
}

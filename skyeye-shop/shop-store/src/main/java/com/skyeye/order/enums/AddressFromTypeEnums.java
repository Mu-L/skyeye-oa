package com.skyeye.order.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AddressFromTypeEnums implements SkyeyeEnumClass {

    ADDRESS_TABLE(0, "收件地址表", true, false),
    ADDRESS_HISTORY_TABLE(1, "收件地址历史表", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

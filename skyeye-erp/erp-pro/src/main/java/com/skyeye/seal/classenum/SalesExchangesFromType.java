package com.skyeye.seal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SalesExchangesFromType implements SkyeyeEnumClass {

    SEAL_ORDER(1, "销售订单", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

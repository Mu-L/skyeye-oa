package com.skyeye.product.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ProductLeadOrReturnFromType implements SkyeyeEnumClass {

    LOANOUT(1, "借出出库单", true, false),
    LOANIN(2, "归还入库单", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

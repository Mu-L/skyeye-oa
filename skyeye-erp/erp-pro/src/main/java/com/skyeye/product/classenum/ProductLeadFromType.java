package com.skyeye.product.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ProductLeadFromType implements SkyeyeEnumClass {

    LOANAPPLICATIONFORM(0, "借出申请单", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

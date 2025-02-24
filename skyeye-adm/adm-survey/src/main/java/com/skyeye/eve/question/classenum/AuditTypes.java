package com.skyeye.eve.question.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AuditTypes implements SkyeyeEnumClass {

    UNAUDITED("UNAUDITED", "未审核", 0, true, false),
    AUDITEDANDAPPROVED("AUDITEDANDAPPROVED", "审核通过", 1, true, false),
    AUDITEDANDREJECTED("AUDITEDANDREJECTED", "审核拒绝", 2, true, false),
    AUDITING("AUDITING", "审核中", 3, true, false);

    private String key;

    private String value;

    private Integer index;

    private Boolean show;

    private Boolean isDefault;

}

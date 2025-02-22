package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ExamineStateEnum implements SkyeyeEnumClass {

    NOT_EXAMINE(1, "未审核", true, true),

    EXAMINE_PASS(2, "审核通过", true, true),

    EXAMINE_NO_PASS(3, "审核不通过", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

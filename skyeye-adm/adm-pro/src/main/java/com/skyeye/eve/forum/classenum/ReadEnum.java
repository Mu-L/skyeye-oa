package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ReadEnum implements SkyeyeEnumClass {
    NO_READ(1, "未读", true, true),
    READ(2, "已读", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

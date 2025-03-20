package com.skyeye.school.chat.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CompanyChatGroupState implements SkyeyeEnumClass {

    NORMAL(1, "正常", true, true),
    CLOSED(2, "强制举报关闭", true, false),
    DISSOLVED(3, "解散", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

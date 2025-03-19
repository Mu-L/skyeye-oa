package com.skyeye.school.chat.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ChatType implements SkyeyeEnumClass {
    PERSONAL_TO_PERSONAL(1, "个人对个人", "friend", true, true),
    GROUP_CHAT(2, "群组聊天", "group", true, false);

    private Integer key;

    private String value;

    private String chType;

    private Boolean show;

    private Boolean isDefault;

    public static String getName(Integer type) {
        for (ChatType bean : ChatType.values()) {
            if (type.equals(bean.getKey())) {
                return bean.getValue();
            }
        }
        return StringUtils.EMPTY;
    }
}

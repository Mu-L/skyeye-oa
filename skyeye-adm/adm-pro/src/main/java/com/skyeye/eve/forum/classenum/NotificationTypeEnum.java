package com.skyeye.eve.forum.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum NotificationTypeEnum implements SkyeyeEnumClass {

    REPLY(1, "帖子回复通知", true, true),
    SEND_TO_FANS(2, "发帖通知粉丝", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

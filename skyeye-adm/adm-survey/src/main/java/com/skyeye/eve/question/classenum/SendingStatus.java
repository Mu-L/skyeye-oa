package com.skyeye.eve.question.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SendingStatus implements SkyeyeEnumClass {

    NOTSENT("NOTSENT", "未发送", 0, true, false),
    SENDING("SENDING", "正在发送", 1, true, false),
    SENT("SENT", "发送完成", 2, true, false),
    SENDFAILED("SENDFAILED", "发送失败", 3, true, false),
    SENDABNORMAL("SENDABNORMAL", "发送异常", 4, true, false);

    private String key;

    private String value;

    private Integer index;

    private Boolean show;

    private Boolean isDefault;
}



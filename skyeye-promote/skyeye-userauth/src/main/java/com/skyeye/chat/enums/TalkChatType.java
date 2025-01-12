/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.chat.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: TalkChatType
 * @Description: 聊天类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/1/12 14:22
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TalkChatType implements SkyeyeEnumClass {

    PERSONAL_TO_PERSONAL(1, "个人对个人", true, true),
    GROUP_CHAT(2, "群组聊天", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getName(Integer type) {
        for (TalkChatType bean : TalkChatType.values()) {
            if (type.equals(bean.getKey())) {
                return bean.getValue();
            }
        }
        return StringUtils.EMPTY;
    }
}

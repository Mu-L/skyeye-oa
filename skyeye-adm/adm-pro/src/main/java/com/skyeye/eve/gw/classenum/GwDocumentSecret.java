/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: GwDocumentSecret
 * @Description: 公文密级枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/25 22:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum GwDocumentSecret implements SkyeyeEnumClass {
    INSIDE("1", "内部", true, true),
    SECRET("2", "秘密", true, false),
    CONFIDENTIAL("3", "机密", true, false),
    TOP_SECRET("4", "绝密", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getShowName(String type) {
        for (GwDocumentSecret value : GwDocumentSecret.values()) {
            if (value.getKey().equals(type)) {
                return value.getValue();
            }
        }
        return StringUtils.EMPTY;
    }
}

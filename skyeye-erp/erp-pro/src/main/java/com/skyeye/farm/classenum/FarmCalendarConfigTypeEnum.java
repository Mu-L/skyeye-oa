/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.farm.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * 车间产能日历-配置类型枚举。
 * 支持多种场景：按日期(节假日/调休)、按星期(周期产能)等，便于后续扩展如按日期区间等。
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FarmCalendarConfigTypeEnum implements SkyeyeEnumClass {

    DATE("DATE", "按日期", true, false),
    WEEKDAY("WEEKDAY", "按星期", true, true),
    PERIOD("PERIOD", "按日期区间", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;

    /**
     * 根据key解析枚举，无效key返回null
     */
    public static FarmCalendarConfigTypeEnum parse(String key) {
        if (key == null) {
            return null;
        }
        return Arrays.stream(values())
            .filter(e -> e.getKey().equals(key))
            .findFirst()
            .orElse(null);
    }

    /**
     * 校验key是否有效
     */
    public static boolean isValid(String key) {
        return parse(key) != null;
    }
}

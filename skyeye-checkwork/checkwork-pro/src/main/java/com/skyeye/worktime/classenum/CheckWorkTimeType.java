/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.worktime.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: CheckWorkTimeType
 * @Description: 考勤班次类型
 * @author: skyeye云系列--卫志强
 * @date: 2023/5/3 15:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CheckWorkTimeType implements SkyeyeEnumClass {

    SINGLE_BREAK(1, "单休", true, true),
    WEEKEND_BREAK(2, "双休", true, false),
    SINGLE_AND_DOUBLE_REST(3, "单双休", true, false),
    CUSTOM(4, "自定义", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getShowName(Integer type) {
        for (CheckWorkTimeType value : CheckWorkTimeType.values()) {
            if (value.getKey().equals(type)) {
                return value.getValue();
            }
        }
        return StringUtils.EMPTY;
    }

}

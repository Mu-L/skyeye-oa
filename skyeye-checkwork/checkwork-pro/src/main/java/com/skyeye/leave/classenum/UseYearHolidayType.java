/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.leave.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

/**
 * @ClassName: UseYearHolidayType
 * @Description: 请假是否使用年假/补休的类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UseYearHolidayType implements SkyeyeEnumClass {

    USE_ANNUAL_LEAVE(1, "使用年假", true, false),
    USE_COMPENSATORY_LEAVE(2, "使用补休", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getShowName(Integer type) {
        for (UseYearHolidayType value : UseYearHolidayType.values()) {
            if (value.getKey().equals(type)) {
                return value.getValue();
            }
        }
        return "未使用年假/补休";
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.classenum;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ClockOutTime
 * @Description: 下班打卡状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ClockOutTime implements SkyeyeEnumClass {

    SYSTEM(0, "系统填充", true, false),
    NORMAL(1, "正常", true, false),
    EARLY(2, "早退", true, false),
    NOTCLOCK(3, "未打卡", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getClockOutState(Integer str) {
        for (ClockOutTime q : ClockOutTime.values()) {
            if (q.getKey().equals(str)) {
                return q.getValue();
            }
        }
        return StrUtil.EMPTY;
    }

}

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
 * @ClassName: ClockState
 * @Description: 考勤状态枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/25 18:44
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ClockState implements SkyeyeEnumClass {

    START(0, "早卡", true, false),
    NORMAL(1, "全勤", true, false),
    ABSENCE(2, "缺勤", true, false),
    IN_SUFFICIENT(3, "工时不足", true, false),
    NOT_START(4, "缺早卡", true, false),
    NOT_END(5, "缺晚卡", true, false),
    SYSTEM(6, "系统天空", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static String getClockState(Integer str) {
        for (ClockState q : ClockState.values()) {
            if (q.getKey().equals(str)) {
                return q.getValue();
            }
        }
        return StrUtil.EMPTY;
    }

}

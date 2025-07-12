/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.checkwork.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CheckWorkShiftType
 * @Description: 班次类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/12 15:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CheckWorkShiftType implements SkyeyeEnumClass {

    FIXED("fixed", "固定班次", true, false),
    SCHEDULE("schedule", "排班班次", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

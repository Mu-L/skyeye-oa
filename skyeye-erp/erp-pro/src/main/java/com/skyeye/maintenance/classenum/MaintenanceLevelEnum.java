/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.maintenance.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @Description: 保养等级
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum MaintenanceLevelEnum implements SkyeyeEnumClass {

    DAILY("daily", "日常保养", "green", true, true),
    LEVEL_TWO("levelTwo", "二级保养", "orange", true, false),
    LEVEL_THREE("levelThree", "三级保养", "red", true, false),
    ANNUAL("annual", "年度保养", "purple", true, false);

    private String key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}

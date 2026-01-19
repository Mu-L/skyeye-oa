/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PatrolPointType
 * @Description: 巡检点位类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PatrolPointType implements SkyeyeEnumClass {

    EQUIPMENT(1, "设备点位", true, true),
    AREA(2, "区域点位", true, false),
    SAFETY(3, "安全点位", true, false),
    ENVIRONMENT(4, "环境点位", true, false),
    FIRE(5, "消防点位", true, false),
    OTHER(99, "其他", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}


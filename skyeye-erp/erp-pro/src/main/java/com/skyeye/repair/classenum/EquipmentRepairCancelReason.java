/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentRepairCancelReason
 * @Description: 设备维修-未进行维修时的作废原因（是否进行维修=否）
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/05
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentRepairCancelReason implements SkyeyeEnumClass {

    FALSE_REPORT(1, "操作工误报", true, false),
    SELF_SOLVE(2, "操作工可自行解决", true, false),
    DUPLICATE(3, "重复报单", true, false),
    NO_PRODUCTION_IMPACT(4, "不影响生产", true, false),
    MAJOR_OVERHAUL_PENDING(5, "需要大修，待协商停机时间", true, false),
    PRODUCTION_NONSTOP(6, "生产不停机，无法进行维修", true, false),
    NO_REPAIR_VALUE(7, "设备无维修价值", true, false),
    CANNOT_REPAIR(8, "无法维修", true, false),
    NO_SPARE_PARTS(9, "没有备件", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

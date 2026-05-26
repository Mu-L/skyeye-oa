/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentRepairEquipmentStatus
 * @Description: 设备维修结果验收-设备状态
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/05
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentRepairEquipmentStatus implements SkyeyeEnumClass {

    NORMAL(1, "正常运行", true, false),
    DEGRADED(2, "带病运行", true, true),
    UNDER_REPAIR(3, "维修中", true, false),
    STANDBY(4, "备用", true, false),
    DISABLED(5, "停用", true, false),
    SCRAPPED(6, "报废", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

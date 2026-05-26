/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentRepairUrgency
 * @Description: 设备维修紧急程度（审核派工）
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/05
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentRepairUrgency implements SkyeyeEnumClass {

    EXTREME(1, "特别紧急", true, false),
    URGENT(2, "紧急", true, false),
    NORMAL(3, "一般", true, true),
    LOW(4, "不急", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

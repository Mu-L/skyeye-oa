/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentRepairAuditOpinion
 * @Description: 设备维修审核意见（审核派工：立即维修 / 驳回 / 转委外）
 * @author: skyeye云系列--卫志强
 * @date: 2026/03/31
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentRepairAuditOpinion implements SkyeyeEnumClass {

    REPAIR_NOW(1, "立即维修", true, true),
    REJECT(2, "驳回", true, false),
    OUTSOURCE(3, "转委外", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

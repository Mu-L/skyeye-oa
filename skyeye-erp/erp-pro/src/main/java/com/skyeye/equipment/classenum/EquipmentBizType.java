package com.skyeye.equipment.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentBizType
 * @Description: 设备关联业务类型
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentBizType implements SkyeyeEnumClass {

    CHECK("check", "设备点检", true, false),
    PATROL("patrol", "设备巡检", true, false),
    REPAIR("repair", "报修维修", true, false),
    MAINTAIN_PLAN("maintainPlan", "维护保养计划", true, false),
    MAINTAIN_RECORD("maintainRecord", "维护保养记录", true, false),
    SPARE_REPLACE("spareReplace", "备件更换", true, false),
    SCRAP("scrap", "设备报废", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

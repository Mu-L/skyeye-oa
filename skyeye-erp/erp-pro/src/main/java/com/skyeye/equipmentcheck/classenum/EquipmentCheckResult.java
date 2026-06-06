package com.skyeye.equipmentcheck.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentCheckResult
 * @Description: 设备点检结果枚举类
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentCheckResult implements SkyeyeEnumClass {

    NORMAL(1, "正常", true, true),
    ABNORMAL(2, "异常", true, false);

    private Integer key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}


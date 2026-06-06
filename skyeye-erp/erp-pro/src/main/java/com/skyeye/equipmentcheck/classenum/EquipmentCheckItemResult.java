package com.skyeye.equipmentcheck.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: EquipmentCheckItemResult
 * @Description: 设备点检明细结果枚举类
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentCheckItemResult implements SkyeyeEnumClass {

    NORMAL("normal", "正常", true, true),
    ABNORMAL("abnormal", "异常", true, false),
    OTHER("other", "其他", true, false);

    private String key;
    private String value;
    private Boolean show;
    private Boolean isDefault;
}


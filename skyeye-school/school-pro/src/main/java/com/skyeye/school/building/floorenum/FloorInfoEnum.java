package com.skyeye.school.building.floorenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FloorInfoEnum implements SkyeyeEnumClass {

    FLOOR_INFO_ENUM(1, "楼层", true, true),
    ClASS_INFO_ENUM(2, "教室", true, false),
    SERVICE_INFO_ENUM(3, "服务", true, false),;

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

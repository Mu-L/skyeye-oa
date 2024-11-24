package com.skyeye.school.building.floorenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: FloorInfoEnum
 * @Description: 楼层教室服务管理枚举类
 * @author: lqu
 * @date: 2023/9/5 17:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FloorInfoEnum implements SkyeyeEnumClass {

    FLOOR_INFO_ENUM(1, "楼层", true, true),
    ClASS_INFO_ENUM(2, "房间", true, false),
    SERVICE_INFO_ENUM(3, "服务", true, false),;

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

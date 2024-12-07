package com.skyeye.school.route.routeenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;


/**
 * @ClassName: RouteTypeEnum
 * @Description: 路由类型枚举
 * @author: lqu
 * @date: 2023/9/5 17:16
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum RouteTypeEnum implements SkyeyeEnumClass {

    ROUTE_TYPE_WALK(1, "步行", true, true),
    ROUTE_TYPE_CAR(2, "驾车", true, false),
    ROUTE_TYPE_EV(3, "电动车", true, false),
    ROUTE_TYPE_RIDE(4, "骑行", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}


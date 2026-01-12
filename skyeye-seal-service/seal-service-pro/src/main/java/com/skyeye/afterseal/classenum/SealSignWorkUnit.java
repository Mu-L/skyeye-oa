package com.skyeye.afterseal.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: SealSignWorkUnit
 * @Description: 签到报工工时单位枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/24
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum SealSignWorkUnit implements SkyeyeEnumClass {

    DAY("day", "天", "#1890ff", true, false),
    HOUR("hour", "小时", "#52c41a", true, true);

    private String key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}


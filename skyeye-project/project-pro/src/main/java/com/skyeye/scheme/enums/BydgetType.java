/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: BydgetType
 * @Description: 预算类型
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 10:56
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum BydgetType implements SkyeyeEnumClass {

    MATERIAL_ADD("material", "材料", true, true),
    LABOR_ADD("labor", "人工", true, false),
    EQUIPMENT_ADD("equipment", "设备", true, false),
    OUTSOURCING_ADD("outsourcing", "外包", true, false),
    MISC_ADD("misc", "其他", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

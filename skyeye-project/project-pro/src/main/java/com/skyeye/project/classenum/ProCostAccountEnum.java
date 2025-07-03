package com.skyeye.project.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ProCostAccountEnum
 * @Description: 成本类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2023/2/26 12:09
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ProCostAccountEnum implements SkyeyeEnumClass {

    HUMAN(0, "人力成本", true, true),
    MATERIAL(1, "材料成本", true, false),
    EQUIPMENT(2, "设备成本", true, false),
    OUTSOURCING(3, "外包成本", true, false),
    OTHER(4, "其他成本", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

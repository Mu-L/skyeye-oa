/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.repair.classenum;

import cn.hutool.core.map.MapUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: EquipmentFaultCategory
 * @Description: 设备维修故障类别（与前端「故障类别」选项一致；「其他」需配合 faultCategoryRemark）
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/05
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentFaultCategory implements SkyeyeEnumClass {

    ELECTRICAL(1, "电气类", true, false),
    MECHANICAL(2, "机械类", true, true),
    ENERGY(3, "能源类", true, false),
    ACCESSORIES(4, "配件类", true, false),
    OTHER(5, "其他", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentFaultCategory bean : EquipmentFaultCategory.values()) {
            if (type.equals(bean.getKey())) {
                Map<String, Object> result = new HashMap<>();
                result.put("id", bean.getKey());
                result.put("name", bean.getValue());
                return result;
            }
        }
        return MapUtil.newHashMap();
    }

}

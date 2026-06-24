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
 * @ClassName: EquipmentRepairFaultReason
 * @Description: 设备维修结果-故障原因（选「其他」须填 faultReasonRemark）
 * @author: skyeye云系列--卫志强
 * @date: 2026/04/05
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum EquipmentRepairFaultReason implements SkyeyeEnumClass {

    NATURAL_WEAR(1, "自然磨损", true, true),
    LUBRICATION(2, "润滑不良", true, false),
    ELECTRICAL_FAULT(3, "电气故障", true, false),
    MECHANICAL_STUCK(4, "机械卡阻", true, false),
    MISOPERATION(5, "操作不当", true, false),
    OTHER(6, "其他", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    public static Map<String, Object> getMation(Integer type) {
        if (type == null) {
            return MapUtil.newHashMap();
        }
        for (EquipmentRepairFaultReason bean : EquipmentRepairFaultReason.values()) {
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

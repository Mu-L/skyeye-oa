/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.dispatch.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: DispatchRuleCodeEnum
 * @Description: 系统派单规则编码枚举
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum DispatchRuleCodeEnum implements SkyeyeEnumClass {

    APPOINT_STAFF("1", "客户指定服务人员优先", "根据工单关联的客户，在客户详情中指定的服务人员列表中距离优先", true, true),
    ORDER_TYPE("2", "满足工单类型限制优先", "根据工单的工单类型，在匹配的服务人员列表中距离优先", true, true),
    FAULT_TYPE("3", "满足故障类型限制优先", "根据工单的故障类型，在匹配的服务人员列表中距离优先", true, true),
    SERVICE_REGION("4", "满足服务区域限制优先", "根据工单的地址，在匹配的服务人员列表中距离优先", true, true),
    MAIN_ACCOUNT("5", "主账号", "则指派给主账号", true, true),
    SERVED_BEFORE("6", "曾服务过的服务人员优先", "根据工单关联的客户，曾经服务过的服务人员列表中距离优先", true, true);

    private String key;
    private String value;
    private String desc;
    private Boolean show;
    private Boolean isDefault;

    public static List<Map<String, Object>> toRuleList() {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DispatchRuleCodeEnum e : values()) {
            Map<String, Object> m = new HashMap<>();
            m.put("code", e.getKey());
            m.put("name", e.getValue());
            m.put("desc", e.getDesc());
            m.put("enabled", Boolean.TRUE);
            m.put("mode", "waterfall");
            list.add(m);
        }
        return list;
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.machin.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Arrays;

/**
 * @ClassName: AutoAssignQuantityTypeEnum
 * @Description: 加工单自动安排车间任务-数量类型
 * @author: skyeye云系列--卫志强
 * @date: 2026/1/30
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum AutoAssignQuantityTypeEnum implements SkyeyeEnumClass {

    ALL("all", "全部数量", true, true),
    REMAINING("remaining", "剩余未分配数量", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    /**
     * 根据 key 解析，未匹配时返回默认值 ALL
     */
    public static AutoAssignQuantityTypeEnum parse(String key) {
        if (key == null || key.isEmpty()) {
            return ALL;
        }
        return Arrays.stream(values())
            .filter(e -> e.getKey().equalsIgnoreCase(key))
            .findFirst()
            .orElse(ALL);
    }

}

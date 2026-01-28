package com.skyeye.followup.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: FollowupPriorityEnum
 * @Description: 跟进优先级枚举
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FollowupPriorityEnum implements SkyeyeEnumClass {

    LOW("1", "低", true, false),
    NORMAL("2", "普通", true, true),
    HIGH("3", "高", true, false),
    URGENT("4", "紧急", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
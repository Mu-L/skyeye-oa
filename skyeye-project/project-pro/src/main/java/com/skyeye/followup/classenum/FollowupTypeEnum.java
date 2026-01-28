package com.skyeye.followup.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: FollowupTypeEnum
 * @Description: 跟进类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FollowupTypeEnum implements SkyeyeEnumClass {

    DEMAND_FOLLOWUP("1", "需求跟进", true, true),
    SCHEME_FOLLOWUP("2", "方案跟进", true, false),
    CONTRACT_FOLLOWUP("3", "合同跟进", true, false),
    EXECUTION_FOLLOWUP("4", "执行跟进", true, false),
    PROBLEM_FOLLOWUP("5", "问题处理跟进", true, false),
    CUSTOM_FOLLOWUP("6", "其他跟进", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
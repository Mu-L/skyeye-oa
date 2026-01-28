package com.skyeye.followup.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: FollowupStateEnum
 * @Description: 跟进状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum FollowupStateEnum implements SkyeyeEnumClass {

    PENDING("1", "待跟进", true, true),
    IN_PROGRESS("2", "跟进中", true, false),
    COMPLETED("3", "已跟进", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
package com.skyeye.construction.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: ConstructionStateEnum
 * @Description: 施工方案状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum ConstructionStateEnum implements SkyeyeEnumClass {

    DRAFT("1", "草稿", true, false),
    PENDING_AUDIT("2", "待审核", true, false),
    AUDITING("3", "审核中", true, false),
    PASSED("4", "已通过", true, false),
    REJECTED("5", "已驳回", true, false),
    REVOKED("6", "已撤销", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}
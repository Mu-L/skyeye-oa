/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TenantUserJoinType
 * @Description: 用户加入租户的加入类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/29 17:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantUserJoinType implements SkyeyeEnumClass {
    AUTO(1, "自动加入", true, false),
    MANUAL(2, "手动加入", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: TenantOrgType
 * @Description: 租户组织类型枚举
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum TenantOrgType implements SkyeyeEnumClass {

    PERSONAL(1, "个人组织", true, false),
    ENTERPRISE(2, "企业组织", true, true);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

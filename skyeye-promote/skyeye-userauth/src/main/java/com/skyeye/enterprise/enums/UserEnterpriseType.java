/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: UserEnterpriseType
 * @Description: 企业用户类型
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/15 14:33
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserEnterpriseType implements SkyeyeEnumClass {

    ENTERPRISE(1, "企业", true, false),
    INDIVIDUAL_BUSINESS_OWNER(2, "个体工商户", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

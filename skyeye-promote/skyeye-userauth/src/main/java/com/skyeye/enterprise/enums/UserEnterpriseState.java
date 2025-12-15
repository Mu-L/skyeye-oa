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
 * @Description: 企业用户状态
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/15 14:33
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum UserEnterpriseState implements SkyeyeEnumClass {

    CERTIFIEDING(1, "认证中", "black", true, false),
    CERTIFIED_SUCCESS(2, "认证成功", "green", true, false),
    CERTIFIED_FAILURE(3, "认证失败", "red", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;

}

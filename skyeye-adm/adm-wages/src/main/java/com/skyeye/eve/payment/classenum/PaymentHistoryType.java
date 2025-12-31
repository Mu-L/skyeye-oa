/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.payment.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PaymentHistoryType
 * @Description: 薪资核算类型枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/22 17:20
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PaymentHistoryType implements SkyeyeEnumClass {

    MANUAL_ACCOUNTING(1, "人工核算", "#D69E2E", true, true),
    SYSTEM_ACCOUNTING(2, "系统核算", "#38A169", true, false);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;
}

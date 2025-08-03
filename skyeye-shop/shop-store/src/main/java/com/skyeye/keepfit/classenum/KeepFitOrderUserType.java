/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.keepfit.classenum;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: KeepFitOrderUserType
 * @Description: 保养订单用户类型枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/25 19:45
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum KeepFitOrderUserType implements SkyeyeEnumClass {

    ANONYMOUS_USER(1, "匿名用户","cyan", true, false),
    MEMBER(2, "会员","purple" ,true, true);

    private Integer key;

    private String value;

    private String color;

    private Boolean show;

    private Boolean isDefault;
}

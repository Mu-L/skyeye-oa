/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CouponStoreCoverage
 * @Description: 优惠券门店使用范围
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/22 14:26
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CouponStoreCoverage implements SkyeyeEnumClass {

    ALL_STORE(1, "全部门店", true, true),
    SPECIFIED_STORE(2, "指定门店", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

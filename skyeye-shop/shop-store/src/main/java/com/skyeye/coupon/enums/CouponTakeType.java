/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.coupon.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: CouponTakeType
 * @Description: 优惠券/模版领取方式枚举类
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/19 8:56
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum CouponTakeType implements SkyeyeEnumClass {

    USER(1, "直接领取", "用户可在首页、每日领劵直接领取", true, true),
//    ADMIN(2, "指定发放", "后台指定会员赠送优惠劵", true, false),
    REGISTER(3, "新人券", "注册时自动领取", true, false);

    private Integer key;

    private String value;

    private String remark;

    private Boolean show;

    private Boolean isDefault;
}

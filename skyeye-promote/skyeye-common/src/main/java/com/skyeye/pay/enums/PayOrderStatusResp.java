/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @ClassName: PayOrderStatusResp
 * @Description: 渠道的支付状态枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:27
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PayOrderStatusResp implements SkyeyeEnumClass {

    WAITING(0, "未支付", true, false),
    SUCCESS(10, "支付成功", true, false),
    REFUND(20, "已退款", true, false),
    CLOSED(30, "支付关闭", true, false);

    private Integer key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

    /**
     * 判断是否支付成功
     *
     * @param status 状态
     * @return 是否支付成功
     */
    public static boolean isSuccess(Integer status) {
        return Objects.equals(status, SUCCESS.getKey());
    }

    /**
     * 判断是否已退款
     *
     * @param status 状态
     * @return 是否支付成功
     */
    public static boolean isRefund(Integer status) {
        return Objects.equals(status, REFUND.getKey());
    }

    /**
     * 判断是否支付关闭
     *
     * @param status 状态
     * @return 是否支付关闭
     */
    public static boolean isClosed(Integer status) {
        return Objects.equals(status, CLOSED.getKey());
    }

}

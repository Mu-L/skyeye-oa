/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.order.config;

import lombok.Data;
import org.hibernate.validator.constraints.URL;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;

/**
 * @ClassName: PayProperties
 * @Description: 支付回调配置
 * @author: skyeye云系列--卫志强
 * @date: 2024/11/21 9:12
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@ConfigurationProperties(prefix = "skyeye.pay")
@Validated
@Data
public class  PayProperties {

    private static final String ORDER_NO_PREFIX = "P";
    private static final String REFUND_NO_PREFIX = "R";

    private static final String WALLET_PAY_APP_KEY_DEFAULT = "wallet";

    /**
     * 支付回调地址
     * <p>
     * 实际上，对应的 PayNotifyController 的 notifyOrder 方法的 URL
     * <p>
     * 回调顺序：支付渠道（支付宝支付、微信支付） => pay 的 orderNotifyUrl 地址 => 业务的 PayAppDO.orderNotifyUrl 地址
     */
    @NotEmpty(message = "支付回调地址不能为空")
    @URL(message = "支付回调地址的格式必须是 URL")
    private String orderNotifyUrl;

    /**
     * 退款回调地址
     * <p>
     * 实际上，对应的 PayNotifyController 的 notifyRefund 方法的 URL
     * <p>
     * 回调顺序：支付渠道（支付宝支付、微信支付） => pay 的 refundNotifyUrl 地址 => 业务的 PayAppDO.notifyRefundUrl 地址
     */
    @NotEmpty(message = "支付回调地址不能为空")
    @URL(message = "支付回调地址的格式必须是 URL")
    private String refundNotifyUrl;

    /**
     * 支付订单 no 的前缀
     */
    @NotEmpty(message = "支付订单 no 的前缀不能为空")
    private String orderNoPrefix = ORDER_NO_PREFIX;

    /**
     * 退款订单 no 的前缀
     */
    @NotEmpty(message = "退款订单 no 的前缀不能为空")
    private String refundNoPrefix = REFUND_NO_PREFIX;

    /**
     * 钱包支付应用 AppKey
     */
    @NotEmpty(message = "钱包支付应用 AppKey 不能为空")
    private String walletPayAppKey = WALLET_PAY_APP_KEY_DEFAULT;

}

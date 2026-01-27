/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.enums;

import cn.hutool.core.util.ArrayUtil;
import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import com.skyeye.pay.core.PayClientConfig;
import com.skyeye.pay.core.service.NonePayClientConfig;
import com.skyeye.pay.core.service.impl.alipay.AlipayPayClientConfig;
import com.skyeye.pay.core.service.impl.weixin.WxPayClientConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: PayType
 * @Description: 付款类型
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/8 10:48
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PayType implements SkyeyeEnumClass {

    WX_PUB("wx_pub", "微信 JSAPI 支付", WxPayClientConfig.class, true, false), // 公众号网页
    WX_LITE("wx_lite", "微信小程序支付", WxPayClientConfig.class, true, false),
    WX_APP("wx_app", "微信 App 支付", WxPayClientConfig.class, true, false),
    WX_NATIVE("wx_native", "微信 Native 支付", WxPayClientConfig.class, true, false),
    WX_WAP("wx_wap", "微信 Wap 网站支付", WxPayClientConfig.class, true, false), // H5 网页
    WX_BAR("wx_bar", "微信付款码支付", WxPayClientConfig.class, true, false),

    ALIPAY_PC("alipay_pc", "支付宝 PC 网站支付", AlipayPayClientConfig.class, true, false),
    ALIPAY_WAP("alipay_wap", "支付宝 Wap 网站支付", AlipayPayClientConfig.class, true, false),
    ALIPAY_APP("alipay_app", "支付宝App 支付", AlipayPayClientConfig.class, true, false),
    ALIPAY_QR("alipay_qr", "支付宝扫码支付", AlipayPayClientConfig.class, true, false),
    ALIPAY_BAR("alipay_bar", "支付宝条码支付", AlipayPayClientConfig.class, true, false),
    MOCK("mock", "模拟支付", NonePayClientConfig.class, true, false),

    // 钱包支付暂时不支持，后续再添加
    WALLET("wallet", "钱包支付", NonePayClientConfig.class, false, false);

    private String key;

    private String value;

    private Class<? extends PayClientConfig> configClass;

    private Boolean show;

    private Boolean isDefault;

    public static PayType getByCode(String code) {
        return ArrayUtil.firstMatch(o -> o.getKey().equals(code), values());
    }

    public static boolean isAlipay(String channelCode) {
        return channelCode != null && channelCode.startsWith("alipay");
    }

    public static Map<String, Object> getMation(String code) {
        PayType type = getByCode(code);
        Map<String, Object> result = new HashMap<>();
        result.put("id", type.getKey());
        result.put("name", type.getValue());
        return result;
    }

}

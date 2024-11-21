/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PayChannelVersion
 * @Description: 微信支付的API版本枚举
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:50
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PayChannelVersion implements SkyeyeEnumClass {

    // <a href="https://pay.weixin.qq.com/wiki/doc/api/jsapi.php?chapter=4_1">V2 协议说明</a>
    V2("V2", "V2 协议", true, false),
    // <a href="https://pay.weixin.qq.com/wiki/doc/apiv3/wechatpay/wechatpay-1.shtml">V3 协议说明</a>
    V3("V3", "V3 协议", true, true);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

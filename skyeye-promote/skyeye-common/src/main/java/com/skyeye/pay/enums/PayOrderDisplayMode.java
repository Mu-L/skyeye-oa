/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.enums;

import com.skyeye.common.base.classenum.SkyeyeEnumClass;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * @ClassName: PayOrderDisplayMode
 * @Description: 支付 UI 展示模式
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 8:30
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public enum PayOrderDisplayMode implements SkyeyeEnumClass {

    URL("url", "Redirect 跳转链接的方式", true, false),
    IFRAME("iframe", "IFrame 内嵌链接的方式【目前暂时用不到】", true, false),
    FORM("form", "HTML 表单提交", true, false),
    QR_CODE("qr_code", "二维码的文字内容", true, false),
    QR_CODE_URL("qr_code_url", "二维码的图片链接", true, false),
    BAR_CODE("bar_code", "条形码", true, false),
    APP("app", "应用：Android、iOS、微信小程序、微信公众号等，需要做自定义处理的", true, false);

    private String key;

    private String value;

    private Boolean show;

    private Boolean isDefault;

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.pay.core.service.impl.weixin;

import cn.hutool.core.collection.CollUtil;
import com.skyeye.pay.core.PayClientConfig;
import com.skyeye.pay.enums.PayChannelVersion;
import lombok.Data;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @ClassName: WxPayClientConfig
 * @Description: 微信支付的 PayClientConfig 实现类
 * 属性主要来自 {@link com.github.binarywang.wxpay.config.WxPayConfig} 的必要属性
 * @author: skyeye云系列--卫志强
 * @date: 2024/9/10 19:16
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class WxPayClientConfig implements PayClientConfig {

    /**
     * 公众号或者小程序的 appid
     * <p>
     * 只有公众号或小程序需要该字段
     */
    @NotBlank(message = "APPID 不能为空", groups = {V2.class, V3.class})
    private String appId;
    /**
     * 商户号
     */
    @NotBlank(message = "商户号不能为空", groups = {V2.class, V3.class})
    private String mchId;
    /**
     * API 版本
     */
    @NotBlank(message = "API 版本不能为空", groups = {V2.class, V3.class})
    private String apiVersion;

    // ========== V2 版本的参数 ==========

    /**
     * 商户密钥
     */
    @NotBlank(message = "商户密钥不能为空", groups = V2.class)
    private String mchKey;
    /**
     * apiclient_cert.p12 证书文件的对应字符串【base64 格式】
     * <p>
     * 为什么采用 base64 格式？因为 p12 读取后是二进制，需要转换成 base64 格式才好传输和存储
     */
    @NotBlank(message = "apiclient_cert.p12 不能为空", groups = V2.class)
    private String keyContent;

    // ========== V3 版本的参数 ==========
    /**
     * apiclient_key.pem 证书文件的对应字符串
     */
    @NotBlank(message = "apiclient_key 不能为空", groups = V3.class)
    private String privateKeyContent;
    /**
     * apiV3 密钥值
     */
    @NotBlank(message = "apiV3 密钥值不能为空", groups = V3.class)
    private String apiV3Key;
    /**
     * 证书序列号
     */
    @NotBlank(message = "证书序列号不能为空", groups = V3.class)
    private String certSerialNo;

    @Deprecated // TODO 待移除
    private String privateCertContent;

    /**
     * 分组校验 v2版本
     */
    public interface V2 {
    }

    /**
     * 分组校验 v3版本
     */
    public interface V3 {
    }

    @Override
    public void validate(Validator validator) {
        Set<ConstraintViolation<Object>> constraintViolations = validator.validate(this, PayChannelVersion.V2_VERSION.getKey().equals(this.getApiVersion()) ? V2.class : V3.class);
        if (CollUtil.isNotEmpty(constraintViolations)) {
            throw new ConstraintViolationException(constraintViolations);
        }
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store.config;

import lombok.Data;

/**
 * @ClassName: HuaweiConfig
 * @Description: 华为应用市场配置类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class HuaweiConfig {

    /**
     * 华为开发者账号ID
     */
    private String clientId;

    /**
     * 华为开发者账号密钥
     */
    private String clientSecret;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * API基础URL
     */
    private String apiUrl = "https://connect-api.cloud.huawei.com/api";

    /**
     * 认证URL
     */
    private String authUrl = "https://oauth-login.cloud.huawei.com/oauth2/v3/token";

    /**
     * 访问令牌
     */
    private String accessToken;

    /**
     * 刷新令牌
     */
    private String refreshToken;

    /**
     * 令牌过期时间
     */
    private Long tokenExpireTime;

    /**
     * 应用包名
     */
    private String packageName;

    /**
     * 应用版本号
     */
    private String versionName;

    /**
     * 应用版本代码
     */
    private String versionCode;

    /**
     * 应用文件路径
     */
    private String filePath;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 应用截图路径列表
     */
    private String[] screenshots;

    /**
     * 应用图标路径
     */
    private String iconPath;

    /**
     * 应用分类
     */
    private String category;

    /**
     * 应用标签
     */
    private String[] tags;

    /**
     * 是否强制更新
     */
    private Boolean forceUpdate = false;

    /**
     * 更新说明
     */
    private String updateDescription;

    /**
     * 应用隐私政策URL
     */
    private String privacyPolicyUrl;

    /**
     * 应用服务条款URL
     */
    private String termsOfServiceUrl;

    /**
     * 应用年龄分级
     */
    private String ageRating;

    /**
     * 应用内容分级
     */
    private String contentRating;
}

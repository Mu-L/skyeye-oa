/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store.config;

import lombok.Data;

/**
 * @ClassName: XiaomiConfig
 * @Description: 小米应用市场配置类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class XiaomiConfig {

    /**
     * 小米开发者账号ID
     */
    private String clientId;

    /**
     * 小米开发者账号密钥
     */
    private String clientSecret;

    /**
     * 应用ID
     */
    private String appId;

    /**
     * API基础URL
     */
    private String apiUrl = "https://api.developer.xiaomi.com";

    /**
     * 认证URL
     */
    private String authUrl = "https://account.xiaomi.com/oauth2/authorize";

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
}

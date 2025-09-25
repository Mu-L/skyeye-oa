/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.store.AppStoreService;
import com.skyeye.app.store.config.VivoConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: VivoAppStoreService
 * @Description: vivo应用市场服务实现类--https://developer.vivo.com.cn/
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class VivoAppStoreService implements AppStoreService {

    private static final String STORE_KEY = "vivo";
    private static final String API_VERSION = "v1";

    @Override
    public String getStoreKey() {
        return STORE_KEY;
    }

    @Override
    public AppStoreSubmitResult submitApp(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        log.info("开始提交应用到vivo应用市场，版本ID：{}，发布记录ID：{}", appVersion.getId(), appRelease.getId());

        try {
            // 构建vivo配置
            VivoConfig config = buildVivoConfig(appVersion, appRelease, appStore);

            // 获取访问令牌
            String accessToken = getAccessToken(config);
            if (StrUtil.isEmpty(accessToken)) {
                return new AppStoreSubmitResult(false, "获取访问令牌失败");
            }

            // 提交应用
            return doSubmitApp(config, accessToken);

        } catch (Exception e) {
            log.error("提交应用到vivo应用市场失败", e);
            return new AppStoreSubmitResult(false, "提交失败：" + e.getMessage());
        }
    }

    @Override
    public AppStoreStatusResult queryAppStatus(AppRelease appRelease, AppStore appStore) {
        log.info("查询vivo应用市场发布状态，发布记录ID：{}", appRelease.getId());

        try {
            // 构建vivo配置
            VivoConfig config = buildVivoConfigFromRelease(appRelease, appStore);

            // 获取访问令牌
            String accessToken = getAccessToken(config);
            if (StrUtil.isEmpty(accessToken)) {
                return new AppStoreStatusResult(false, "获取访问令牌失败");
            }

            // 查询状态
            return doQueryAppStatus(config, accessToken);

        } catch (Exception e) {
            log.error("查询vivo应用市场发布状态失败", e);
            return new AppStoreStatusResult(false, "查询失败：" + e.getMessage());
        }
    }

    @Override
    public AppStoreCancelResult cancelApp(AppRelease appRelease, AppStore appStore) {
        log.info("取消vivo应用市场发布，发布记录ID：{}", appRelease.getId());

        try {
            // 构建vivo配置
            VivoConfig config = buildVivoConfigFromRelease(appRelease, appStore);

            // 获取访问令牌
            String accessToken = getAccessToken(config);
            if (StrUtil.isEmpty(accessToken)) {
                return new AppStoreCancelResult(false, "获取访问令牌失败");
            }

            // 取消发布
            return doCancelApp(config, accessToken);

        } catch (Exception e) {
            log.error("取消vivo应用市场发布失败", e);
            return new AppStoreCancelResult(false, "取消失败：" + e.getMessage());
        }
    }

    @Override
    public AppStoreRemoveResult removeApp(AppRelease appRelease, AppStore appStore) {
        log.info("下架vivo应用市场应用，发布记录ID：{}", appRelease.getId());

        try {
            // 构建vivo配置
            VivoConfig config = buildVivoConfigFromRelease(appRelease, appStore);

            // 获取访问令牌
            String accessToken = getAccessToken(config);
            if (StrUtil.isEmpty(accessToken)) {
                return new AppStoreRemoveResult(false, "获取访问令牌失败");
            }

            // 下架应用
            return doRemoveApp(config, accessToken);

        } catch (Exception e) {
            log.error("下架vivo应用市场应用失败", e);
            return new AppStoreRemoveResult(false, "下架失败：" + e.getMessage());
        }
    }

    /**
     * 构建vivo配置
     */
    private VivoConfig buildVivoConfig(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        VivoConfig config = new VivoConfig();

        // 从AppStore的apiConfig获取配置
        if (appStore != null && StrUtil.isNotEmpty(appStore.getApiConfig())) {
            try {
                JSONObject configJson = JSONUtil.parseObj(appStore.getApiConfig());
                config.setClientId(configJson.getStr("clientId"));
                config.setClientSecret(configJson.getStr("clientSecret"));
                config.setAppId(configJson.getStr("appId"));
                config.setApiUrl(configJson.getStr("apiUrl", "https://developer.vivo.com.cn/api"));
                config.setAuthUrl(configJson.getStr("authUrl", "https://developer.vivo.com.cn/oauth2/token"));
            } catch (Exception e) {
                log.warn("解析vivo应用商店配置失败，使用默认配置", e);
            }
        }

        // 设置应用信息
        config.setPackageName(appVersion.getName());
        config.setVersionName(appVersion.getName());
        config.setVersionCode(appVersion.getVersionCode());
        config.setFilePath(appVersion.getFilePath());
        config.setDescription(appVersion.getRemark());
        config.setForceUpdate(appVersion.getIsForceUpdate() == 1);

        return config;
    }

    /**
     * 从发布记录构建vivo配置
     */
    private VivoConfig buildVivoConfigFromRelease(AppRelease appRelease, AppStore appStore) {
        VivoConfig config = new VivoConfig();

        // 从AppStore的apiConfig获取配置
        if (appStore != null && StrUtil.isNotEmpty(appStore.getApiConfig())) {
            try {
                JSONObject configJson = JSONUtil.parseObj(appStore.getApiConfig());
                config.setClientId(configJson.getStr("clientId"));
                config.setClientSecret(configJson.getStr("clientSecret"));
                config.setAppId(configJson.getStr("appId"));
                config.setApiUrl(configJson.getStr("apiUrl", "https://developer.vivo.com.cn/api"));
                config.setAuthUrl(configJson.getStr("authUrl", "https://developer.vivo.com.cn/oauth2/token"));
            } catch (Exception e) {
                log.warn("解析vivo应用商店配置失败", e);
            }
        }

        return config;
    }

    /**
     * 获取访问令牌
     */
    private String getAccessToken(VivoConfig config) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("client_id", config.getClientId());
            params.put("client_secret", config.getClientSecret());
            params.put("grant_type", "client_credentials");

            HttpResponse response = HttpRequest.post(config.getAuthUrl())
                .form(params)
                .execute();

            if (response.getStatus() == 200) {
                JSONObject result = JSONUtil.parseObj(response.body());
                String accessToken = result.getStr("access_token");
                Long expiresIn = result.getLong("expires_in");

                // 更新配置中的令牌信息
                config.setAccessToken(accessToken);
                config.setTokenExpireTime(System.currentTimeMillis() + expiresIn * 1000);

                return accessToken;
            } else {
                log.error("获取vivo访问令牌失败，状态码：{}，响应：{}", response.getStatus(), response.body());
                return null;
            }
        } catch (Exception e) {
            log.error("获取vivo访问令牌异常", e);
            return null;
        }
    }

    /**
     * 提交应用
     */
    private AppStoreSubmitResult doSubmitApp(VivoConfig config, String accessToken) {
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("app_id", config.getAppId());
            params.put("package_name", config.getPackageName());
            params.put("version_name", config.getVersionName());
            params.put("version_code", config.getVersionCode());
            params.put("file_path", config.getFilePath());
            params.put("description", config.getDescription());
            params.put("force_update", config.getForceUpdate());

            HttpResponse response = HttpRequest.post(config.getApiUrl() + "/" + API_VERSION + "/apps/submit")
                .header("Authorization", "Bearer " + accessToken)
                .form(params)
                .execute();

            if (response.getStatus() == 200) {
                JSONObject result = JSONUtil.parseObj(response.body());
                String submissionId = result.getStr("submission_id");
                String status = result.getStr("status");

                log.info("vivo应用提交成功，提交ID：{}，状态：{}", submissionId, status);
                return new AppStoreSubmitResult(true, "提交成功", submissionId, status);
            } else {
                log.error("vivo应用提交失败，状态码：{}，响应：{}", response.getStatus(), response.body());
                return new AppStoreSubmitResult(false, "提交失败：" + response.body());
            }
        } catch (Exception e) {
            log.error("vivo应用提交异常", e);
            return new AppStoreSubmitResult(false, "提交异常：" + e.getMessage());
        }
    }

    /**
     * 查询应用状态
     */
    private AppStoreStatusResult doQueryAppStatus(VivoConfig config, String accessToken) {
        try {
            HttpResponse response = HttpRequest.get(config.getApiUrl() + "/" + API_VERSION + "/apps/" + config.getAppId() + "/status")
                .header("Authorization", "Bearer " + accessToken)
                .execute();

            if (response.getStatus() == 200) {
                JSONObject result = JSONUtil.parseObj(response.body());
                String status = result.getStr("status");
                String reviewStatus = result.getStr("review_status");
                String reviewMessage = result.getStr("review_message");

                log.info("vivo应用状态查询成功，状态：{}，审核状态：{}", status, reviewStatus);
                return new AppStoreStatusResult(true, "查询成功", status, reviewStatus, reviewMessage);
            } else {
                log.error("vivo应用状态查询失败，状态码：{}，响应：{}", response.getStatus(), response.body());
                return new AppStoreStatusResult(false, "查询失败：" + response.body());
            }
        } catch (Exception e) {
            log.error("vivo应用状态查询异常", e);
            return new AppStoreStatusResult(false, "查询异常：" + e.getMessage());
        }
    }

    /**
     * 取消应用发布
     */
    private AppStoreCancelResult doCancelApp(VivoConfig config, String accessToken) {
        try {
            HttpResponse response = HttpRequest.post(config.getApiUrl() + "/" + API_VERSION + "/apps/" + config.getAppId() + "/cancel")
                .header("Authorization", "Bearer " + accessToken)
                .execute();

            if (response.getStatus() == 200) {
                log.info("vivo应用取消发布成功");
                return new AppStoreCancelResult(true, "取消发布成功");
            } else {
                log.error("vivo应用取消发布失败，状态码：{}，响应：{}", response.getStatus(), response.body());
                return new AppStoreCancelResult(false, "取消发布失败：" + response.body());
            }
        } catch (Exception e) {
            log.error("vivo应用取消发布异常", e);
            return new AppStoreCancelResult(false, "取消发布异常：" + e.getMessage());
        }
    }

    /**
     * 下架应用
     */
    private AppStoreRemoveResult doRemoveApp(VivoConfig config, String accessToken) {
        try {
            HttpResponse response = HttpRequest.post(config.getApiUrl() + "/" + API_VERSION + "/apps/" + config.getAppId() + "/remove")
                .header("Authorization", "Bearer " + accessToken)
                .execute();

            if (response.getStatus() == 200) {
                log.info("vivo应用下架成功");
                return new AppStoreRemoveResult(true, "下架成功");
            } else {
                log.error("vivo应用下架失败，状态码：{}，响应：{}", response.getStatus(), response.body());
                return new AppStoreRemoveResult(false, "下架失败：" + response.body());
            }
        } catch (Exception e) {
            log.error("vivo应用下架异常", e);
            return new AppStoreRemoveResult(false, "下架异常：" + e.getMessage());
        }
    }
}

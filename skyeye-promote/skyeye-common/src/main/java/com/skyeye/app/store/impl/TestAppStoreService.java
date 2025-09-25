/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store.impl;

import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.store.AppStoreService;
import com.skyeye.app.store.config.TestConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @ClassName: TestAppStoreService
 * @Description: 测试应用市场服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class TestAppStoreService implements AppStoreService {

    private static final String STORE_KEY = "test";

    @Override
    public String getStoreKey() {
        return STORE_KEY;
    }

    @Override
    public AppStoreSubmitResult submitApp(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        log.info("开始测试提交应用到测试应用市场，版本ID：{}，发布记录ID：{}", appVersion.getId(), appRelease.getId());

        try {
            // 构建测试配置
            TestConfig config = buildTestConfig(appVersion, appRelease, appStore);

            // 直接返回成功结果
            String submissionId = "test_submission_" + System.currentTimeMillis();
            log.info("测试应用提交成功，提交ID：{}", submissionId);
            return new AppStoreSubmitResult(true, "测试提交成功", submissionId, "submitted");

        } catch (Exception e) {
            log.error("测试提交应用到测试应用市场失败", e);
            return new AppStoreSubmitResult(false, "测试提交失败：" + e.getMessage());
        }
    }

    @Override
    public AppStoreStatusResult queryAppStatus(AppRelease appRelease, AppStore appStore) {
        log.info("查询测试应用市场发布状态，发布记录ID：{}", appRelease.getId());

        try {
            // 直接返回成功状态
            log.info("测试应用状态查询成功");
            return new AppStoreStatusResult(true, "测试查询成功", "published", "已发布", "测试应用已成功发布");

        } catch (Exception e) {
            log.error("查询测试应用市场发布状态失败", e);
            return new AppStoreStatusResult(false, "查询失败：" + e.getMessage());
        }
    }

    @Override
    public AppStoreCancelResult cancelApp(AppRelease appRelease, AppStore appStore) {
        log.info("取消测试应用市场发布，发布记录ID：{}", appRelease.getId());

        try {
            // 直接返回成功结果
            log.info("测试应用取消发布成功");
            return new AppStoreCancelResult(true, "测试取消发布成功");

        } catch (Exception e) {
            log.error("取消测试应用市场发布失败", e);
            return new AppStoreCancelResult(false, "取消失败：" + e.getMessage());
        }
    }

    @Override
    public AppStoreRemoveResult removeApp(AppRelease appRelease, AppStore appStore) {
        log.info("下架测试应用市场应用，发布记录ID：{}", appRelease.getId());

        try {
            // 直接返回成功结果
            log.info("测试应用下架成功");
            return new AppStoreRemoveResult(true, "测试下架成功");

        } catch (Exception e) {
            log.error("下架测试应用市场应用失败", e);
            return new AppStoreRemoveResult(false, "下架失败：" + e.getMessage());
        }
    }

    /**
     * 构建测试配置
     */
    private TestConfig buildTestConfig(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        TestConfig config = new TestConfig();

        // 从AppStore的apiConfig获取配置
        if (appStore != null && appStore.getApiConfig() != null) {
            try {
                JSONObject configJson = JSONUtil.parseObj(appStore.getApiConfig());
                config.setAppId(configJson.getStr("appId", "test_app_" + System.currentTimeMillis()));
            } catch (Exception e) {
                log.warn("解析测试应用商店配置失败，使用默认配置", e);
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
}

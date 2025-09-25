/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store.example;

import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.store.AppStoreService;
import com.skyeye.app.store.AppStoreServiceManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: AppStoreUsageExample
 * @Description: 应用商店使用示例
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
public class AppStoreUsageExample {

    @Autowired
    private AppStoreServiceManager appStoreServiceManager;

    /**
     * 示例：提交应用到小米应用市场
     */
    public void submitToXiaomiExample(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        String storeKey = "xiaomi";

        // 检查是否支持该应用商店
        if (!appStoreServiceManager.isStoreSupported(storeKey)) {
            log.error("不支持的应用商店：{}", storeKey);
            return;
        }

        // 提交应用
        AppStoreService.AppStoreSubmitResult result = appStoreServiceManager.submitApp(storeKey, appVersion, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("提交成功：{}", result.getMessage());
            log.info("提交ID：{}，状态：{}", result.getSubmissionId(), result.getStatus());
        } else {
            log.error("提交失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：提交应用到华为应用市场
     */
    public void submitToHuaweiExample(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        String storeKey = "huawei";

        // 检查是否支持该应用商店
        if (!appStoreServiceManager.isStoreSupported(storeKey)) {
            log.error("不支持的应用商店：{}", storeKey);
            return;
        }

        // 提交应用
        AppStoreService.AppStoreSubmitResult result = appStoreServiceManager.submitApp(storeKey, appVersion, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("提交成功：{}", result.getMessage());
            log.info("提交ID：{}，状态：{}", result.getSubmissionId(), result.getStatus());
        } else {
            log.error("提交失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：提交应用到vivo应用市场
     */
    public void submitToVivoExample(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        String storeKey = "vivo";

        // 检查是否支持该应用商店
        if (!appStoreServiceManager.isStoreSupported(storeKey)) {
            log.error("不支持的应用商店：{}", storeKey);
            return;
        }

        // 提交应用
        AppStoreService.AppStoreSubmitResult result = appStoreServiceManager.submitApp(storeKey, appVersion, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("提交成功：{}", result.getMessage());
            log.info("提交ID：{}，状态：{}", result.getSubmissionId(), result.getStatus());
        } else {
            log.error("提交失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：提交应用到oppo应用市场
     */
    public void submitToOppoExample(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        String storeKey = "oppo";

        // 检查是否支持该应用商店
        if (!appStoreServiceManager.isStoreSupported(storeKey)) {
            log.error("不支持的应用商店：{}", storeKey);
            return;
        }

        // 提交应用
        AppStoreService.AppStoreSubmitResult result = appStoreServiceManager.submitApp(storeKey, appVersion, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("提交成功：{}", result.getMessage());
            log.info("提交ID：{}，状态：{}", result.getSubmissionId(), result.getStatus());
        } else {
            log.error("提交失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：提交应用到测试应用市场
     */
    public void submitToTestExample(AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        String storeKey = "test";

        // 检查是否支持该应用商店
        if (!appStoreServiceManager.isStoreSupported(storeKey)) {
            log.error("不支持的应用商店：{}", storeKey);
            return;
        }

        // 提交应用
        AppStoreService.AppStoreSubmitResult result = appStoreServiceManager.submitApp(storeKey, appVersion, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("测试提交成功：{}", result.getMessage());
            log.info("提交ID：{}，状态：{}", result.getSubmissionId(), result.getStatus());
        } else {
            log.error("测试提交失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：查询应用发布状态
     */
    public void queryAppStatusExample(AppRelease appRelease, AppStore appStore) {
        String storeKey = "xiaomi";

        AppStoreService.AppStoreStatusResult result = appStoreServiceManager.queryAppStatus(storeKey, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("查询成功：{}", result.getMessage());
            log.info("状态：{}，审核状态：{}", result.getStatus(), result.getReviewStatus());
            if (result.getReviewMessage() != null) {
                log.info("审核信息：{}", result.getReviewMessage());
            }
        } else {
            log.error("查询失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：取消应用发布
     */
    public void cancelAppExample(AppRelease appRelease, AppStore appStore) {
        String storeKey = "xiaomi";

        AppStoreService.AppStoreCancelResult result = appStoreServiceManager.cancelApp(storeKey, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("取消成功：{}", result.getMessage());
        } else {
            log.error("取消失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：下架应用
     */
    public void removeAppExample(AppRelease appRelease, AppStore appStore) {
        String storeKey = "xiaomi";

        AppStoreService.AppStoreRemoveResult result = appStoreServiceManager.removeApp(storeKey, appRelease, appStore);

        if (result.isSuccess()) {
            log.info("下架成功：{}", result.getMessage());
        } else {
            log.error("下架失败：{}", result.getMessage());
        }
    }

    /**
     * 示例：获取所有支持的应用商店
     */
    public void getSupportedStoresExample() {
        String[] supportedStores = appStoreServiceManager.getSupportedStores();
        log.info("支持的应用商店：{}", String.join(", ", supportedStores));
    }
}

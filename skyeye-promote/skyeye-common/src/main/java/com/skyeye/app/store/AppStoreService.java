/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store;

import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.entity.AppVersion;
import lombok.Data;

/**
 * @ClassName: AppStoreService
 * @Description: 应用商店服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface AppStoreService {

    /**
     * 获取应用商店标识
     *
     * @return 应用商店标识
     */
    String getStoreKey();

    /**
     * 提交应用到应用商店
     *
     * @param appVersion APP版本信息
     * @param appRelease 发布记录
     * @param appStore 应用商店配置
     * @return 提交结果
     */
    AppStoreSubmitResult submitApp(AppVersion appVersion, AppRelease appRelease, AppStore appStore);

    /**
     * 查询应用发布状态
     *
     * @param appRelease 发布记录
     * @param appStore 应用商店配置
     * @return 发布状态信息
     */
    AppStoreStatusResult queryAppStatus(AppRelease appRelease, AppStore appStore);

    /**
     * 取消应用发布
     *
     * @param appRelease 发布记录
     * @param appStore 应用商店配置
     * @return 取消结果
     */
    AppStoreCancelResult cancelApp(AppRelease appRelease, AppStore appStore);

    /**
     * 下架应用
     *
     * @param appRelease 发布记录
     * @param appStore 应用商店配置
     * @return 下架结果
     */
    AppStoreRemoveResult removeApp(AppRelease appRelease, AppStore appStore);

    /**
     * 应用商店提交结果
     */
    @Data
    class AppStoreSubmitResult {
        private boolean success;
        private String message;
        private String submissionId;
        private String status;

        public AppStoreSubmitResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AppStoreSubmitResult(boolean success, String message, String submissionId, String status) {
            this.success = success;
            this.message = message;
            this.submissionId = submissionId;
            this.status = status;
        }

    }

    /**
     * 应用商店状态查询结果
     */
    @Data
    class AppStoreStatusResult {
        private boolean success;
        private String message;
        private String status;
        private String reviewStatus;
        private String reviewMessage;

        public AppStoreStatusResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public AppStoreStatusResult(boolean success, String message, String status, String reviewStatus, String reviewMessage) {
            this.success = success;
            this.message = message;
            this.status = status;
            this.reviewStatus = reviewStatus;
            this.reviewMessage = reviewMessage;
        }
    }

    /**
     * 应用商店取消结果
     */
    @Data
    class AppStoreCancelResult {
        private boolean success;
        private String message;

        public AppStoreCancelResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }

    /**
     * 应用商店下架结果
     */
    @Data
    class AppStoreRemoveResult {
        private boolean success;
        private String message;

        public AppStoreRemoveResult(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

    }
}

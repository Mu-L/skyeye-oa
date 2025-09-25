/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store;

import com.skyeye.app.entity.AppRelease;
import com.skyeye.app.entity.AppStore;
import com.skyeye.app.entity.AppVersion;
import com.skyeye.app.store.factory.AppStoreFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: AppStoreServiceManager
 * @Description: 应用商店服务管理器
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class AppStoreServiceManager {

    @Autowired
    private AppStoreFactory appStoreFactory;

    /**
     * 提交应用到应用商店
     *
     * @param storeKey   应用商店标识
     * @param appVersion APP版本信息
     * @param appRelease 发布记录
     * @return 提交结果
     */
    public AppStoreService.AppStoreSubmitResult submitApp(String storeKey, AppVersion appVersion, AppRelease appRelease, AppStore appStore) {
        log.info("开始提交应用到应用商店：{}，版本ID：{}，发布记录ID：{}", storeKey, appVersion.getId(), appRelease.getId());

        AppStoreService storeService = appStoreFactory.getStoreService(storeKey);
        if (storeService == null) {
            log.error("未找到应用商店服务：{}", storeKey);
            return new AppStoreService.AppStoreSubmitResult(false, "不支持的应用商店：" + storeKey);
        }

        try {
            return storeService.submitApp(appVersion, appRelease, appStore);
        } catch (Exception e) {
            log.error("提交应用到应用商店失败：{}", storeKey, e);
            return new AppStoreService.AppStoreSubmitResult(false, "提交失败：" + e.getMessage());
        }
    }

    /**
     * 查询应用发布状态
     *
     * @param storeKey   应用商店标识
     * @param appRelease 发布记录
     * @return 发布状态信息
     */
    public AppStoreService.AppStoreStatusResult queryAppStatus(String storeKey, AppRelease appRelease, AppStore appStore) {
        log.info("查询应用商店发布状态：{}，发布记录ID：{}", storeKey, appRelease.getId());

        AppStoreService storeService = appStoreFactory.getStoreService(storeKey);
        if (storeService == null) {
            log.error("未找到应用商店服务：{}", storeKey);
            return new AppStoreService.AppStoreStatusResult(false, "不支持的应用商店：" + storeKey);
        }

        try {
            return storeService.queryAppStatus(appRelease, appStore);
        } catch (Exception e) {
            log.error("查询应用商店发布状态失败：{}", storeKey, e);
            return new AppStoreService.AppStoreStatusResult(false, "查询失败：" + e.getMessage());
        }
    }

    /**
     * 取消应用发布
     *
     * @param storeKey   应用商店标识
     * @param appRelease 发布记录
     * @return 取消结果
     */
    public AppStoreService.AppStoreCancelResult cancelApp(String storeKey, AppRelease appRelease, AppStore appStore) {
        log.info("取消应用商店发布：{}，发布记录ID：{}", storeKey, appRelease.getId());

        AppStoreService storeService = appStoreFactory.getStoreService(storeKey);
        if (storeService == null) {
            log.error("未找到应用商店服务：{}", storeKey);
            return new AppStoreService.AppStoreCancelResult(false, "不支持的应用商店：" + storeKey);
        }

        try {
            return storeService.cancelApp(appRelease, appStore);
        } catch (Exception e) {
            log.error("取消应用商店发布失败：{}", storeKey, e);
            return new AppStoreService.AppStoreCancelResult(false, "取消失败：" + e.getMessage());
        }
    }

    /**
     * 下架应用
     *
     * @param storeKey   应用商店标识
     * @param appRelease 发布记录
     * @return 下架结果
     */
    public AppStoreService.AppStoreRemoveResult removeApp(String storeKey, AppRelease appRelease, AppStore appStore) {
        log.info("下架应用商店应用：{}，发布记录ID：{}", storeKey, appRelease.getId());

        AppStoreService storeService = appStoreFactory.getStoreService(storeKey);
        if (storeService == null) {
            log.error("未找到应用商店服务：{}", storeKey);
            return new AppStoreService.AppStoreRemoveResult(false, "不支持的应用商店：" + storeKey);
        }

        try {
            return storeService.removeApp(appRelease, appStore);
        } catch (Exception e) {
            log.error("下架应用商店应用失败：{}", storeKey, e);
            return new AppStoreService.AppStoreRemoveResult(false, "下架失败：" + e.getMessage());
        }
    }

    /**
     * 检查应用商店是否支持
     *
     * @param storeKey 应用商店标识
     * @return 是否支持
     */
    public boolean isStoreSupported(String storeKey) {
        return appStoreFactory.isStoreSupported(storeKey);
    }

    /**
     * 获取所有支持的应用商店
     *
     * @return 支持的应用商店列表
     */
    public String[] getSupportedStores() {
        return appStoreFactory.getSupportedStores();
    }
}

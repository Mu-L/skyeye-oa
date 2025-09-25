/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store.factory;

import com.skyeye.app.store.AppStoreService;
import com.skyeye.app.store.impl.HuaweiAppStoreService;
import com.skyeye.app.store.impl.OppoAppStoreService;
import com.skyeye.app.store.impl.TestAppStoreService;
import com.skyeye.app.store.impl.VivoAppStoreService;
import com.skyeye.app.store.impl.XiaomiAppStoreService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName: AppStoreFactory
 * @Description: 应用商店工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Component
public class AppStoreFactory {

    private final Map<String, AppStoreService> storeServices = new HashMap<>();

    @Autowired
    private XiaomiAppStoreService xiaomiAppStoreService;

    @Autowired
    private HuaweiAppStoreService huaweiAppStoreService;

    @Autowired
    private VivoAppStoreService vivoAppStoreService;

    @Autowired
    private OppoAppStoreService oppoAppStoreService;

    @Autowired
    private TestAppStoreService testAppStoreService;

    /**
     * 初始化应用商店服务
     */
    @PostConstruct
    public void initStoreServices() {
        // 注册测试应用市场
        registerStoreService("test", testAppStoreService);
        
        // 注册小米应用市场
        registerStoreService("xiaomi", xiaomiAppStoreService);
        
        // 注册华为应用市场
        registerStoreService("huawei", huaweiAppStoreService);
        
        // 注册vivo应用市场
        registerStoreService("vivo", vivoAppStoreService);
        
        // 注册oppo应用市场
        registerStoreService("oppo", oppoAppStoreService);
        
        log.info("应用商店服务初始化完成，已注册服务：{}", storeServices.keySet());
    }

    /**
     * 注册应用商店服务
     *
     * @param storeKey 应用商店标识
     * @param service  应用商店服务
     */
    public void registerStoreService(String storeKey, AppStoreService service) {
        if (storeKey != null && service != null) {
            storeServices.put(storeKey, service);
            log.info("注册应用商店服务：{} -> {}", storeKey, service.getClass().getSimpleName());
        }
    }

    /**
     * 根据应用商店标识获取服务
     *
     * @param storeKey 应用商店标识
     * @return 应用商店服务
     */
    public AppStoreService getStoreService(String storeKey) {
        if (storeKey == null) {
            log.warn("应用商店标识为空");
            return null;
        }

        AppStoreService service = storeServices.get(storeKey);
        if (service == null) {
            log.warn("未找到应用商店服务：{}，可用服务：{}", storeKey, storeServices.keySet());
        }

        return service;
    }

    /**
     * 检查应用商店服务是否支持
     *
     * @param storeKey 应用商店标识
     * @return 是否支持
     */
    public boolean isStoreSupported(String storeKey) {
        return storeKey != null && storeServices.containsKey(storeKey);
    }

    /**
     * 获取所有支持的应用商店
     *
     * @return 支持的应用商店列表
     */
    public String[] getSupportedStores() {
        return storeServices.keySet().toArray(new String[0]);
    }

    /**
     * 获取应用商店服务数量
     *
     * @return 服务数量
     */
    public int getServiceCount() {
        return storeServices.size();
    }
}

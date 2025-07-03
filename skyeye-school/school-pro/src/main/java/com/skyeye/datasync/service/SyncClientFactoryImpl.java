/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync.service;

import com.skyeye.datasync.AbstractSyncClient;
import com.skyeye.datasync.enums.SyncFromType;
import com.skyeye.datasync.factory.SyncClientFactory;
import com.skyeye.datasync.service.impl.guangkeshi.GksClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @ClassName: SyncClientFactoryImpl
 * @Description: 同步客户端工厂实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 8:33
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
public class SyncClientFactoryImpl implements SyncClientFactory {

    private final ConcurrentMap<String, AbstractSyncClient> clientClass = new ConcurrentHashMap<>();

    public SyncClientFactoryImpl() {
        clientClass.put(SyncFromType.GKS.getKey(), new GksClient());
    }

    @Override
    public AbstractSyncClient getClient(String syncType) {
        if (!containsClient(syncType)) {
            log.error("不存在类型为 {} 的同步客户端", syncType);
            return null;
        }
        return clientClass.get(syncType);
    }

    @Override
    public void registerClient(String syncType, AbstractSyncClient client) {
        log.info("注册同步客户端，类型：{}", syncType);
        clientClass.put(syncType, client);
    }

    @Override
    public boolean containsClient(String syncType) {
        return clientClass.containsKey(syncType);
    }

}

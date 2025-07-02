/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync.factory;

import com.skyeye.datasync.AbstractSyncClient;

/**
 * @ClassName: SyncClientFactory
 * @Description: 数据同步工厂类
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 8:32
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface SyncClientFactory {

    /**
     * 获取同步客户端
     *
     * @param syncType 同步类型
     * @return 同步客户端
     */
    AbstractSyncClient getClient(String syncType);

    /**
     * 注册同步客户端
     *
     * @param syncType 同步类型
     * @param client   同步客户端
     */
    void registerClient(String syncType, AbstractSyncClient client);

    /**
     * 判断是否存在同步客户端
     *
     * @param syncType 同步类型
     * @return 是否存在
     */
    boolean containsClient(String syncType);

}

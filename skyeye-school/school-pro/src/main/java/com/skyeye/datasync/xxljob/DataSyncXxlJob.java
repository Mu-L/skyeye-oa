/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync.xxljob;

import com.skyeye.datasync.AbstractSyncClient;
import com.skyeye.datasync.enums.SyncFromType;
import com.skyeye.datasync.factory.SyncClientFactory;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @ClassName: DataSyncXxlJob
 * @Description: 数据同步定时任务
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 11:30
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class DataSyncXxlJob {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataSyncXxlJob.class);

    @Autowired
    private SyncClientFactory syncClientFactory;

    /**
     * 每天凌晨2点执行广西科技师范学院数据同步任务
     */
    @XxlJob("gksDataSyncJob")
    public void gksDataSync() {
        LOGGER.info("gksDataSync start.");
        try {
            String syncType = SyncFromType.GKS.getKey();
            AbstractSyncClient client = syncClientFactory.getClient(syncType);
            if (client == null) {
                LOGGER.error("不存在类型为 {} 的同步客户端", syncType);
                return;
            }

            // 执行全量数据同步
            boolean result = client.syncAll();
            LOGGER.info("广西科技师范学院数据同步结果：{}", result);
        } catch (Exception e) {
            LOGGER.error("广西科技师范学院数据同步失败", e);
        }
        LOGGER.info("gksDataSync end.");
    }

}
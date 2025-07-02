/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.datasync;

import com.skyeye.datasync.factory.SyncClientFactory;
import com.skyeye.datasync.service.SyncClientFactoryImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName: SkyeyeSyncAutoConfiguration
 * @Description: 数据同步自动配置类
 * @author: skyeye云系列--卫志强
 * @date: 2025/7/2 9:14
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Configuration
public class SkyeyeSyncAutoConfiguration {

    @Bean
    public SyncClientFactory syncClientFactory() {
        return new SyncClientFactoryImpl();
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.app.store.config;

import lombok.Data;

/**
 * @ClassName: TestConfig
 * @Description: 测试应用市场配置类
 * @author: skyeye云系列--卫志强
 * @date: 2025/09/20 13:11
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Data
public class TestConfig {

    /**
     * 测试应用ID
     */
    private String appId;

    /**
     * 应用包名
     */
    private String packageName;

    /**
     * 应用版本号
     */
    private String versionName;

    /**
     * 应用版本代码
     */
    private String versionCode;

    /**
     * 应用文件路径
     */
    private String filePath;

    /**
     * 应用描述
     */
    private String description;

    /**
     * 是否强制更新
     */
    private Boolean forceUpdate = false;
}

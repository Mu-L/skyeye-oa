/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.sms.core.service.impl;

import com.skyeye.sms.core.service.SmsClient;
import com.skyeye.sms.entity.SmsChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * 短信客户端的抽象类，提供模板方法，减少子类的冗余代码
 *
 * @author zzf
 * @since 2021/2/1 9:28
 */
@Slf4j
public abstract class AbstractSmsClient implements SmsClient {

    /**
     * 短信渠道配置
     */
    protected volatile SmsChannel properties;

    public AbstractSmsClient(SmsChannel properties) {
        this.properties = properties;
    }

    /**0
     * 初始化
     */
    public final void init() {
        doInit();
        log.debug("[init][配置({}) 初始化完成]", properties);
    }

    /**
     * 自定义初始化
     */
    protected abstract void doInit();

    public final void refresh(SmsChannel properties) {
        // 判断是否更新
        if (properties.equals(this.properties)) {
            return;
        }
        log.info("[refresh][配置({})发生变化，重新初始化]", properties);
        this.properties = properties;
        // 初始化
        this.init();
    }

    @Override
    public String getId() {
        return properties.getId();
    }

}

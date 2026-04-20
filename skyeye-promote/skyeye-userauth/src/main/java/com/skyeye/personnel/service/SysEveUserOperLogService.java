/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.personnel.entity.SysEveUserOperLog;

public interface SysEveUserOperLogService extends SkyeyeBusinessService<SysEveUserOperLog> {

    /**
     * 清理指定保留月数之前的操作日志（分批删除）。
     *
     * @param batchSize    单批删除数量
     * @param retainMonths 保留最近多少个月日志
     * @return 本批清理删除数量
     */
    int cleanExpiredOperationLogs(int batchSize, int retainMonths);

}

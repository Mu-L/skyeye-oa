/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.doc.member.entity.DocMemberLoginLog;

/**
 * @ClassName: DocMemberLoginLogService
 * @Description: 文档会员登录日志服务接口
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/21 8:53
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface DocMemberLoginLogService extends SkyeyeBusinessService<DocMemberLoginLog> {

    void recordLoginLogAsync(String userId, String userCode, Integer deviceType, Integer loginStatus, String loginMessage);

    /**
     * 清理指定保留月数之前的文档会员登录日志（分批删除）
     *
     * @param batchSize    单批删除数量
     * @param retainMonths 保留最近多少个月日志
     * @return 本批清理删除数量
     */
    int cleanExpiredLoginLogs(int batchSize, int retainMonths);

}

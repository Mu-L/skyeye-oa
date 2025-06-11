/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.notice.service.NoticeService;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @ClassName: QuartzNoticeMationService
 * @Description: 定时上线公告
 * @author: skyeye云系列--卫志强
 * @date: 2022/8/7 16:57
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class QuartzNoticeMationService {

    @Autowired
    private NoticeService noticeService;

    private static final Logger LOGGER = LoggerFactory.getLogger(QuartzNoticeMationService.class);

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @XxlJob("quartzNoticeMationService")
    public void call() {
        String param = XxlJobHelper.getJobParam();
        Map<String, String> paramMap = JSONUtil.toBean(param, null);
        String noticeId = paramMap.get("objectId");
        String tenantId = tenantEnable ? paramMap.get("tenantId") : StrUtil.EMPTY;
        if (tenantEnable) {
            TenantContext.setTenantId(tenantId);
        }
        LOGGER.info("start quartz notice, notice id is: {}", noticeId);
        // 上线状态
        noticeService.editNoticeStateToUp(noticeId);
        LOGGER.info("end quartz notice, notice id is: {}", noticeId);
    }

}

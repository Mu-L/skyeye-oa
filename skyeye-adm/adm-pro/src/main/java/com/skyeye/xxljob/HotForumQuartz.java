/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.forum.service.ForumHotService;
import com.skyeye.eve.service.ITenantService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: HotForumQuartz
 * @Description: 每天凌晨两点去计算每日热门贴
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 23:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class HotForumQuartz {

    @Autowired
    private ForumHotService forumHotService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Autowired
    private ITenantService iTenantService;

    /**
     * 定时器计算每日热门贴
     */
    @XxlJob("hotForumQuartz")
    public void editHotForumMation() {
        if (tenantEnable) {
            //  开启多租户
            List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
            if (CollectionUtil.isEmpty(tenantList)) {
                return;
            }
            tenantList.forEach(tenant -> {
                String tenantId = tenant.get("id").toString();
                TenantContext.setTenantId(tenantId);
                forumHotService.editHotForumMation();
            });
        } else {
            forumHotService.editHotForumMation();
        }
    }

}

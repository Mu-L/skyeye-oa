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

@Component
public class HotForumTag {

    @Autowired
    private ForumHotService forumHotService;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Autowired
    private ITenantService iTenantService;

    @XxlJob("queryHotForumTagList")
    public void queryHotForumTagList() {
        if (tenantEnable) {
            //  开启多租户
            List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
            if (CollectionUtil.isEmpty(tenantList)) {
                return;
            }
            tenantList.forEach(tenant -> {
                String tenantId = tenant.get("id").toString();
                TenantContext.setTenantId(tenantId);
                forumHotService.queryHotForumTagList();
            });
        } else {
            forumHotService.queryHotForumTagList();
        }
    }
}

package com.skyeye.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.piecework.service.PieceworkSystemService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: createPieceworkSystem
 * @Description: 新增临时员工薪资统计
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/2 9:42
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class PieceworkSystemXxlJob {

    private static Logger LOGGER = LoggerFactory.getLogger(PieceworkSystemXxlJob.class);

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Autowired
    private ITenantService iTenantService;

    @Autowired
    private PieceworkSystemService pieceworkSystemService;

    @XxlJob("createPieceworkSystem")
    public void createOrUpdatePieceworkSystem() {
        try {
            if (tenantEnable) {
                //  开启多租户
                List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
                if (CollectionUtil.isEmpty(tenantList)) {
                    return;
                }
                tenantList.forEach(tenant -> {
                    String tenantId = tenant.get("id").toString();
                    TenantContext.setTenantId(tenantId);
                    pieceworkSystemService.writePieceworkSystem();
                });
            } else {
                pieceworkSystemService.writePieceworkSystem();
            }
        } catch (Exception e) {
            LOGGER.warn("xxljob writeLoanBorrowAnalysisRecord error:{0}", e);
        }
    }
}

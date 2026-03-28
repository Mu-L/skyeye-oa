package com.skyeye.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.loan.service.LoanBorrowAnalysisService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LoanBorrowAnalysisXxlJob
 * @Description: 借款单分析记录
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/11 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class LoanBorrowAnalysisXxlJob {

    @Autowired
    private LoanBorrowAnalysisService loanBorrowAnalysisService;

    private static Logger LOGGER = LoggerFactory.getLogger(LoanBorrowAnalysisXxlJob.class);

    @Autowired
    private ITenantService iTenantService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 定时任务
     * 每月一次：还款单分析记录
     */


    @XxlJob("writeLoanBorrowAnalysisRecord")
    public void writeLoanBorrowAnalysisRecord() {
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
                    loanBorrowAnalysisService.writeLoanBorrowAnalysisRecord();
                });
            } else {
                loanBorrowAnalysisService.writeLoanBorrowAnalysisRecord();
            }
        } catch (Exception e) {
            LOGGER.warn("xxljob writeLoanBorrowAnalysisRecord error:{0}", e);
        }
    }

}

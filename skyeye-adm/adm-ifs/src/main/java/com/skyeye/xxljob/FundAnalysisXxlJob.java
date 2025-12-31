package com.skyeye.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.receivepayment.service.FundAnalysisService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: FundAnalysisXxlJob
 * @Description: 资金分析记录
 * @author: skyeye云系列--卫志强
 * @date: 2023/10/11 19:20
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class FundAnalysisXxlJob {

    @Autowired
    private  FundAnalysisService fundAnalysisService;


    private static Logger LOGGER = LoggerFactory.getLogger(FundAnalysisXxlJob.class);

    @Autowired
    private ITenantService iTenantService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * 定时任务
     * 每月一次
     * 计算每个客户/供应商的月度资金分析记录
     * */

    @XxlJob("writeFundAnalysisRecord")
    public void writeFundAnalysisRecord() {
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
                    fundAnalysisService.writeFundAnalysisRecord(tenantId);
                });
            }else {
                fundAnalysisService.writeFundAnalysisRecord(null);
            }
        }catch (Exception e){
            LOGGER.warn("xxljob writeFundAnalysisRecord error:{0}",e);
        }
    }
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com
 * All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.meal.service.MealOrderChildService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: MealOrderExpireXxlJob
 * @Description: 按年限套餐过期扫描任务（xxl-job）
 */
@Component
public class MealOrderExpireXxlJob {

    private static final Logger log = LoggerFactory.getLogger(MealOrderExpireXxlJob.class);

    @Autowired
    private MealOrderChildService mealOrderChildService;

    @Autowired
    private ITenantService iTenantService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    /**
     * xxl-job Handler：expireYearLimitMealOrders
     * 建议在调度中心配置为按日/按小时执行；参数可选传入 {"tenantId": "..."} 以支持多租户。
     */
    @XxlJob("expireYearLimitMealOrders")
    public void expireYearLimitMealOrdersJob() {
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
                    mealOrderChildService.expireYearLimitMealOrders();
                });
            } else {
                mealOrderChildService.expireYearLimitMealOrders();
            }
        } catch (Exception e) {
            log.warn("MealOrderExpireXxlJob error.", e);
        }
    }
}


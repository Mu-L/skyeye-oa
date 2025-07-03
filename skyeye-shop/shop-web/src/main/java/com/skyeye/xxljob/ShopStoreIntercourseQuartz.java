/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.xxljob;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateAfterSpacePointTime;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.service.ITenantService;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.store.service.StoreIntercourseService;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ShopStoreIntercourseQuartz
 * @Description: 门店昨日支出/收入往来计算
 * @author: skyeye云系列--卫志强
 * @date: 2022/3/10 21:13
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
public class ShopStoreIntercourseQuartz {

    private static Logger log = LoggerFactory.getLogger(ShopStoreIntercourseQuartz.class);

    @Autowired
    private StoreIntercourseService storeIntercourseService;

    private static final String LOCK_KEY = "calcShopStoreIntercourse";

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Autowired
    private ITenantService iTenantService;

    /**
     * 定时器计算门店昨日支出/收入往来信息,每天凌晨两点执行一次
     */
    @XxlJob("shopStoreIntercourseQuartz")
    public void calcShopStoreIntercourse() {
        log.info("定时器计算门店昨日支出/收入往来信息执行 start");
        if (tenantEnable) {
            List<Map<String, Object>> tenantList = iTenantService.queryAllTenantList();
            if (CollectionUtil.isEmpty(tenantList)) {
                return;
            }
            tenantList.forEach(tenant -> {
                String tenantId = tenant.get("id").toString();
                TenantContext.setTenantId(tenantId);
                calcMethod(tenantId);
            });
        } else {
            calcMethod(StrUtil.EMPTY);
        }
        log.info("定时器计算门店昨日支出/收入往来信息 end");
    }

    private void calcMethod(String tenantId) {
        String lockKey = LOCK_KEY + tenantId;
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                // 加锁失败
                return;
            }
            // 得到昨天的时间
            String yesterdayTime = DateAfterSpacePointTime.getSpecifiedTime(
                DateAfterSpacePointTime.ONE_DAY.getType(), DateUtil.getTimeAndToString(), DateUtil.YYYY_MM_DD, DateAfterSpacePointTime.AroundType.BEFORE);
            log.info("yesterdayTime is {}.", yesterdayTime);
            // 判断昨天的数据是否已经统计过并入库,如果已经统计过，则不会进行下一次的统计
            List<Map<String, Object>> yesterdayData = storeIntercourseService.queryStoreIntercourseListByDay(yesterdayTime);
            if (!CollectionUtils.isEmpty(yesterdayData)) {
                log.info("已统计过昨日数据，不再进行统计");
                return;
            }
            log.info("开始统计");
            // 获取昨天的往来数据信息
            yesterdayData = storeIntercourseService.queryStoreIntercourseByDay(yesterdayTime);
            yesterdayData.forEach(bean -> {
                if (ToolUtil.isBlank(bean.get("mealByStoreId").toString())) {
                    bean.put("state", 2);
                } else {
                    bean.put("state", 1);
                }
            });
            log.info("解析数据为 {}.", JSON.toJSONString(yesterdayData));
            if (!CollectionUtils.isEmpty(yesterdayData)) {
                storeIntercourseService.insertStoreIntercourse(yesterdayData);
            }
            log.info("保存数据完成");
        } catch (Exception e) {
            log.warn("calcShopStoreIntercourse error.", e);
        } finally {
            lock.unlock();
        }
    }

}

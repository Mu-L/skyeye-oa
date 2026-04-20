/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.personnel.dao.SysEveUserOperLogDao;
import com.skyeye.personnel.entity.SysEveUserOperLog;
import com.skyeye.personnel.service.SysEveUserOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统操作日志（租户强隔离）
 */
@Slf4j
@Service
@SkyeyeService(name = "系统操作日志", groupName = "用户管理", tenant = TenantEnum.NO_ISOLATION, allowDynamicAttrKey = false)
public class SysEveUserOperLogServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserOperLogDao, SysEveUserOperLog> implements SysEveUserOperLogService {

    private static final DateTimeFormatter OPER_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);

    @Override
    @IgnoreTenant
    public int cleanExpiredOperationLogs(int batchSize, int retainMonths) {
        int actualBatchSize = Math.max(batchSize, 100);
        int actualRetainMonths = Math.max(retainMonths, 1);
        String cutoffTime = LocalDateTime.now().minusMonths(actualRetainMonths).format(OPER_TIME_FORMATTER);
        String idColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getId);
        String operTimeColumn = MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime);
        QueryWrapper<SysEveUserOperLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(idColumn)
            .lt(operTimeColumn, cutoffTime)
            .orderByAsc(operTimeColumn, idColumn)
            .last("limit " + actualBatchSize);
        List<Object> idList = baseMapper.selectObjs(queryWrapper);
        if (idList == null || idList.isEmpty()) {
            return 0;
        }
        boolean removed = removeByIds(idList);
        if (!removed) {
            log.warn("清理过期操作日志未删除到数据，cutoffTime: {}", cutoffTime);
            return 0;
        }
        log.info("清理过期操作日志完成，保留最近{}个月，本批删除数量：{}", actualRetainMonths, idList.size());
        return idList.size();
    }

    @Override
    protected QueryWrapper<SysEveUserOperLog> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SysEveUserOperLog> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime));
        return queryWrapper;
    }
}

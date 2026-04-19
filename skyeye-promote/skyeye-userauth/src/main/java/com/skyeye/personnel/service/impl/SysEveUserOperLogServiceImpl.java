/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.personnel.dao.SysEveUserOperLogDao;
import com.skyeye.personnel.entity.SysEveUserOperLog;
import com.skyeye.personnel.service.SysEveUserOperLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 系统操作日志（租户强隔离）
 */
@Slf4j
@Service
@SkyeyeService(name = "系统操作日志", groupName = "用户管理", tenant = TenantEnum.NO_ISOLATION, allowDynamicAttrKey = false)
public class SysEveUserOperLogServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserOperLogDao, SysEveUserOperLog> implements SysEveUserOperLogService {

    @Override
    protected QueryWrapper<SysEveUserOperLog> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SysEveUserOperLog> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SysEveUserOperLog::getOperTime));
        return queryWrapper;
    }
}

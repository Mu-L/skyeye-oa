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
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.IPSeeker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.personnel.dao.SysEveUserLoginLogDao;
import com.skyeye.personnel.entity.SysEveUserLoginLog;
import com.skyeye.personnel.service.SysEveUserLoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * @ClassName: SysEveUserLoginLogServiceImpl
 * @Description: 用户登录日志服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/18 20:35
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "用户登录日志", groupName = "用户管理", tenant = TenantEnum.NO_ISOLATION)
public class SysEveUserLoginLogServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserLoginLogDao, SysEveUserLoginLog> implements SysEveUserLoginLogService {

    private static final DateTimeFormatter LOGIN_TIME_FORMATTER = DateTimeFormatter.ofPattern(DateUtil.YYYY_MM_DD_HH_MM_SS);

    @Autowired
    private Executor userLoginLogExecutor;

    @Override
    @IgnoreTenant
    public void recordLoginLogAsync(String userId, String userCode, Integer deviceType, Integer loginStatus, String loginMessage) {
        HttpServletRequest request = PutObject.getRequest();
        userLoginLogExecutor.execute(() -> {
            try {
                SysEveUserLoginLog loginLog = new SysEveUserLoginLog();

                // 设置用户信息
                loginLog.setUserId(userId);
                loginLog.setUserCode(userCode);
                // 设置登录信息
                String clientIp = ToolUtil.getIpByRequest(request);
                loginLog.setLoginIp(clientIp);
                loginLog.setLoginTime(DateUtil.getTimeAndToString());
                loginLog.setLoginStatus(loginStatus);
                loginLog.setLoginMessage(loginMessage);
                loginLog.setDeviceType(deviceType);

                // 获取IP对应的城市信息
                String address = IPSeeker.getCountry(clientIp);
                String city = IPSeeker.getCurCityByCountry(address);
                loginLog.setLoginCity(city);

                // 保存登录日志
                createEntity(loginLog, loginLog.getUserId());
                log.info("用户登录日志记录成功，用户ID：{}，登录状态：{}", loginLog.getUserId(), loginStatus);
            } catch (Exception e) {
                log.error("记录用户登录日志失败，用户ID：{}，错误信息：{}", userId, e.getMessage(), e);
            }
        });
    }

    @Override
    @IgnoreTenant
    public int cleanExpiredLoginLogs(int batchSize, int retainMonths) {
        int actualBatchSize = Math.max(batchSize, 100);
        int actualRetainMonths = Math.max(retainMonths, 1);
        String cutoffTime = LocalDateTime.now().minusMonths(actualRetainMonths).format(LOGIN_TIME_FORMATTER);
        String idColumn = MybatisPlusUtil.toColumns(SysEveUserLoginLog::getId);
        String loginTimeColumn = MybatisPlusUtil.toColumns(SysEveUserLoginLog::getLoginTime);
        QueryWrapper<SysEveUserLoginLog> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(idColumn)
            .lt(loginTimeColumn, cutoffTime)
            .orderByAsc(loginTimeColumn, idColumn)
            .last("limit " + actualBatchSize);
        List<Object> idList = baseMapper.selectObjs(queryWrapper);
        if (idList == null || idList.isEmpty()) {
            return 0;
        }
        boolean removed = removeByIds(idList);
        if (!removed) {
            log.warn("清理过期登录日志未删除到数据，cutoffTime: {}", cutoffTime);
            return 0;
        }
        log.info("清理过期登录日志完成，保留最近{}个月，本批删除数量：{}", actualRetainMonths, idList.size());
        return idList.size();
    }

    @Override
    protected QueryWrapper<SysEveUserLoginLog> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SysEveUserLoginLog> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(SysEveUserLoginLog::getLoginTime));
        return queryWrapper;
    }
}
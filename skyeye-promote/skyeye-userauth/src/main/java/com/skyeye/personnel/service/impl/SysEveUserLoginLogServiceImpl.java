/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.IPSeeker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.personnel.dao.SysEveUserLoginLogDao;
import com.skyeye.personnel.entity.SysEveUserLoginLog;
import com.skyeye.personnel.service.SysEveUserLoginLogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
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

}
/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.personnel.classenum.UserInstallMenuSize;
import com.skyeye.personnel.classenum.UserInstallTaskPosition;
import com.skyeye.personnel.dao.SysEveUserInstallDao;
import com.skyeye.personnel.entity.SysEveUserInstall;
import com.skyeye.personnel.service.SysEveUserInstallService;
import com.skyeye.personnel.service.SysEveUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

/**
 * @ClassName: SysEveUserInstallServiceImpl
 * @Description: 用户个人配置信息服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 12:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户个人配置信息", groupName = "用户个人配置信息", tenant = TenantEnum.NO_ISOLATION)
public class SysEveUserInstallServiceImpl extends SkyeyeBusinessServiceImpl<SysEveUserInstallDao, SysEveUserInstall> implements SysEveUserInstallService {

    @Autowired
    private SysEveUserService sysEveUserService;

    @Override
    public void createPrepose(SysEveUserInstall entity) {
        entity.setWinBgPicUrl("/images/upload/winbgpic/default.jpg");
        entity.setWinLockBgPicUrl("/images/upload/winlockbgpic/default.jpg");
        entity.setWinThemeColor("31");
        entity.setWinStartMenuSize(UserInstallMenuSize.MIDDLE.getKey());
        entity.setWinTaskPosition(UserInstallTaskPosition.BOTTOM.getKey());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserInstallWinBgPic(InputObject inputObject, OutputObject outputObject) {
        editUserInstallItem(inputObject, "winBgPicUrl");
    }

    private void editUserInstallItem(InputObject inputObject, String column) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        String val = map.get(column).toString();
        UpdateWrapper<SysEveUserInstall> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserInstall::getUserId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(clazz, column), val);
        update(updateWrapper);

        // 修改reids中的用户信息
        user.put(column, val);
        sysEveUserService.setUserLoginRedisMation(userId, user, true);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserInstallWinLockBgPic(InputObject inputObject, OutputObject outputObject) {
        editUserInstallItem(inputObject, "winLockBgPicUrl");
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserInstallThemeColor(InputObject inputObject, OutputObject outputObject) {
        editUserInstallItem(inputObject, "winThemeColor");
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserInstallWinStartMenuSize(InputObject inputObject, OutputObject outputObject) {
        editUserInstallItem(inputObject, "winStartMenuSize");
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserInstallWinTaskPosition(InputObject inputObject, OutputObject outputObject) {
        editUserInstallItem(inputObject, "winTaskPosition");
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserInstallVagueBgSrc(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        String winBgPicVague = map.get("winBgPicVague").toString();
        String winBgPicVagueValue = map.get("winBgPicVagueValue").toString();
        UpdateWrapper<SysEveUserInstall> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(SysEveUserInstall::getUserId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserInstall::getWinBgPicVague), winBgPicVague);
        updateWrapper.set(MybatisPlusUtil.toColumns(SysEveUserInstall::getWinBgPicVagueValue), winBgPicVagueValue);
        update(updateWrapper);

        // 修改reids中的用户信息
        user.put("winBgPicVague", winBgPicVague);
        user.put("winBgPicVagueValue", winBgPicVagueValue);
        sysEveUserService.setUserLoginRedisMation(userId, user, true);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserInstallLoadMenuIconById(InputObject inputObject, OutputObject outputObject) {
        editUserInstallItem(inputObject, "winBottomMenuIcon");
    }

}

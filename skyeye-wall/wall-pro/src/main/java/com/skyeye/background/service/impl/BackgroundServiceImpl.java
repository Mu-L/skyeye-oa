/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/
package com.skyeye.background.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.background.dao.BackgroundDao;
import com.skyeye.background.entity.Background;
import com.skyeye.background.service.BackgroundService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.WallConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.promote.user.service.ISysEveUserStaffService;
import com.skyeye.user.service.UserService;
import com.skyeye.user.userenum.LoginIdentity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: backgroundServiceImpl
 * @Description: 背景图片服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/24 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "背景图片管理", groupName = "背景图片管理")
public class BackgroundServiceImpl extends SkyeyeBusinessServiceImpl<BackgroundDao, Background> implements BackgroundService {

    @Autowired
    private UserService userService;

    @Autowired
    private ISysEveUserStaffService iSysEveUserStaffService;

    @Override
    protected QueryWrapper<Background> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Background> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Background::getCreateId), userId);
        return queryWrapper;
    }

    @Override
    public void createPostpose(Background background, String userId) {
        String userIdentity = PutObject.getRequest().getHeader(WallConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            iSysEveUserStaffService.updateTeacherWallBgImg(background.getImage());
        } else {
            userService.updateBackgroundImage(userId, background.getImage());
        }
    }

    @Override
    public void deletePreExecution(String id) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String createId = selectById(id).getCreateId();
        if (!userId.equals(createId)) {
            throw new CustomException("无权限");
        }
    }
}
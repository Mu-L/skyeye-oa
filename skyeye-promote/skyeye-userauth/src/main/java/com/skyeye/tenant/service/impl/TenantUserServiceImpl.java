/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.tenant.dao.TenantUserDao;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.service.TenantUserService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: TenantUserServiceImpl
 * @Description: 租户下的用户服务实现类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:47
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "租户下的用户管理", groupName = "租户下的用户管理")
public class TenantUserServiceImpl extends SkyeyeBusinessServiceImpl<TenantUserDao, TenantUser> implements TenantUserService {

    @Override
    public void removeTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getParams().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        remove(queryWrapper);
    }

    @Override
    public void exitTenantUser(InputObject inputObject, OutputObject outputObject) {
        String staffId = InputObject.getLogParamsStatic().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        remove(queryWrapper);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.account.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.account.dao.AccountDao;
import com.skyeye.account.entity.Account;
import com.skyeye.account.service.AccountService;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.IsDefaultEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AccountServiceImpl
 * @Description: 账户信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:42
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "账户管理", groupName = "账户管理")
public class AccountServiceImpl extends SkyeyeBusinessServiceImpl<AccountDao, Account> implements AccountService {

    @Override
    public void createPrepose(Account entity) {
        entity.setCurrentAmount(entity.getInitialAmount());
    }

    @Override
    public void updatePrepose(Account entity) {
        entity.setInitialAmount(null);
    }

    @Override
    public void writePostpose(Account entity, String userId) {
        if (entity.getIsDefault().equals(IsDefaultEnum.IS_DEFAULT.getKey())) {
            // 如果将当前数据修改为默认数据，则需要修改之前的数据为非默认
            // 1. 先查询默认的账号信息
            QueryWrapper<Account> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Account::getIsDefault), IsDefaultEnum.IS_DEFAULT.getKey());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Account::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
            Account defaultAccount = getOne(queryWrapper, false);

            // 2. 修改默认的账号信息
            UpdateWrapper<Account> updateWrapper = new UpdateWrapper<>();
            updateWrapper.ne(CommonConstants.ID, entity.getId());
            updateWrapper.eq(MybatisPlusUtil.toColumns(Account::getDeleteFlag), DeleteFlagEnum.NOT_DELETE.getKey());
            updateWrapper.set(MybatisPlusUtil.toColumns(Account::getIsDefault), IsDefaultEnum.NOT_DEFAULT.getKey());
            update(updateWrapper);

            // 3. 如果不为空，则刷新缓存
            if (defaultAccount != null) {
                refreshCache(defaultAccount.getId());
            }
        }
    }

    @Override
    public void queryAllAccountList(InputObject inputObject, OutputObject outputObject) {
        List<Map<String, Object>> beans = queryAllDataForMap();
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}

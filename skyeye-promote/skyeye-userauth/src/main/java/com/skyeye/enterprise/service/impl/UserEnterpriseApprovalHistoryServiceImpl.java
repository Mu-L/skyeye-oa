/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.enterprise.dao.UserEnterpriseApprovalHistoryDao;
import com.skyeye.enterprise.entity.UserEnterprise;
import com.skyeye.enterprise.entity.UserEnterpriseApprovalHistory;
import com.skyeye.enterprise.enums.UserEnterpriseApprovalResult;
import com.skyeye.enterprise.enums.UserEnterpriseState;
import com.skyeye.enterprise.service.UserEnterpriseApprovalHistoryService;
import com.skyeye.enterprise.service.UserEnterpriseService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: UserEnterpriseApprovalHistoryServiceImpl
 * @Description: 企业账号审批历史服务层
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/16 8:59
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "企业账号审批历史", groupName = "企业账户", tenant = TenantEnum.PLATE)
public class UserEnterpriseApprovalHistoryServiceImpl extends SkyeyeBusinessServiceImpl<UserEnterpriseApprovalHistoryDao, UserEnterpriseApprovalHistory> implements UserEnterpriseApprovalHistoryService {

    @Autowired
    private UserEnterpriseService userEnterpriseService;

    @Override
    protected void createPrepose(UserEnterpriseApprovalHistory entity) {
        UserEnterprise userEnterprise = userEnterpriseService.selectById(entity.getUserEnterpriseId());
        if (ObjectUtil.isEmpty(userEnterprise) || StrUtil.isEmpty(userEnterprise.getId())) {
            throw new CustomException("企业用户不存在！");
        }
        if (!UserEnterpriseState.CERTIFIEDING.getKey().equals(userEnterprise.getState())) {
            // 只有认证中的才能提交审批历史
            throw new CustomException("只有认证中的企业才能提交审批历史！");
        }
    }

    @Override
    protected void createPostpose(UserEnterpriseApprovalHistory entity, String userId) {
        if (entity.getApprovalResult() == UserEnterpriseApprovalResult.PASS.getKey()) {
            // 认证通过，更新用户状态为已认证
            userEnterpriseService.editUserEnterpriseState(entity.getUserEnterpriseId(), UserEnterpriseState.CERTIFIED_SUCCESS.getKey());
        } else if (entity.getApprovalResult() == UserEnterpriseApprovalResult.REFUSE.getKey()) {
            // 认证失败，更新用户状态为认证拒绝
            userEnterpriseService.editUserEnterpriseState(entity.getUserEnterpriseId(), UserEnterpriseState.CERTIFIED_FAILURE.getKey());
        }
    }

    @Override
    public void queryApprovalHistoryListByUserEnterpriseId(InputObject inputObject, OutputObject outputObject) {
        String userEnterpriseId = inputObject.getParams().get("userEnterpriseId").toString();
        QueryWrapper<UserEnterpriseApprovalHistory> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserEnterpriseApprovalHistory::getUserEnterpriseId), userEnterpriseId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(UserEnterpriseApprovalHistory::getCreateTime));
        List<UserEnterpriseApprovalHistory> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}

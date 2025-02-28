/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.CompanyTalkGroupUserDao;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: CompanyTalkGroupUserServiceImpl
 * @Description: 群组用户服务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/2/28 17:16
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "群组用户", groupName = "聊天模块")
public class CompanyTalkGroupUserServiceImpl extends SkyeyeBusinessServiceImpl<CompanyTalkGroupUserDao, CompanyTalkGroupUser> implements CompanyTalkGroupUserService {

    @Override
    public List<CompanyTalkGroupUser> selectByGroupId(String groupId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        List<CompanyTalkGroupUser> list = list(queryWrapper);
        return list;
    }

    @Override
    public long countByGroupId(String groupId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        return count(queryWrapper);
    }

    @Override
    public Map<String, String> batchCheckGroupUserIsExit(List<String> groupId, String userId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        List<CompanyTalkGroupUser> list = list(queryWrapper);
        if (list.size() > 0) {
            Map<String, String> map = new HashMap<>();
            for (CompanyTalkGroupUser companyTalkGroupUser : list) {
                map.put(companyTalkGroupUser.getGroupId(), companyTalkGroupUser.getId());
            }
            return map;
        }
        return new HashMap<>();
    }

    @Override
    public boolean checkGroupUserIsExit(String groupId, String userId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        List<CompanyTalkGroupUser> list = list(queryWrapper);
        if (list.size() > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void deleteByGroupIdAndUserId(String groupId, String userId) {
        QueryWrapper<CompanyTalkGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getGroupId), groupId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroupUser::getUserId), userId);
        remove(queryWrapper);
    }
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.ActGroupUserDao;
import com.skyeye.eve.entity.ActGroupUser;
import com.skyeye.eve.service.ActGroupUserService;
import com.skyeye.organization.service.ICompanyJobService;
import com.skyeye.organization.service.ICompanyService;
import com.skyeye.organization.service.IDepmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ActGroupUserServiceImpl
 * @Description: 用户组关联用户管理--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/12 14:14
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户组关联用户管理", groupName = "用户组关联用户管理", manageShow = false)
public class ActGroupUserServiceImpl extends SkyeyeBusinessServiceImpl<ActGroupUserDao, ActGroupUser> implements ActGroupUserService {

    @Autowired
    private ICompanyService iCompanyService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobService iCompanyJobService;

    /**
     * 给用户组新增用户
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertActGroupUser(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        List<String> userIds = Arrays.asList(map.get("userIds").toString().split(CommonCharConstants.COMMA_MARK));
        // 获取组内已有的用户
        QueryWrapper<ActGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActGroupUser::getGroupId), groupId);
        List<String> hasUserIds = list(queryWrapper).stream().map(ActGroupUser::getUserId).collect(Collectors.toList());

        List<ActGroupUser> list = new ArrayList<>();
        userIds.forEach(userId -> {
            if (hasUserIds.indexOf(userId) == -1) {
                ActGroupUser actGroupUser = new ActGroupUser();
                actGroupUser.setGroupId(groupId);
                actGroupUser.setUserId(userId);
                list.add(actGroupUser);
            }
        });
        String userId = inputObject.getLogParams().get("id").toString();
        createEntity(list, userId);
    }

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryUserInfoOnActGroup(commonPageInfo);
        if (tenantEnable) {
            // 如果开启多租户，则需要查询员工所在的租户下的员工信息
            List<String> userIds = beans.stream().map(item -> item.get("userId").toString()).distinct().collect(Collectors.toList());
            Map<String, Map<String, Object>> tenantUserMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds));
            beans.forEach(bean -> {
                String userId = bean.get("userId").toString();
                Map<String, Object> tenantUser = tenantUserMap.get(userId);
                if (CollectionUtil.isNotEmpty(tenantUser)) {
                    bean.put("companyId", tenantUser.get("companyId"));
                    bean.put("departmentId", tenantUser.get("departmentId"));
                    bean.put("jobId", tenantUser.get("jobId"));
                }
            });
        }

        iCompanyService.setNameForMap(beans, "companyId", "companyName");
        iDepmentService.setNameForMap(beans, "departmentId", "departmentName");
        iCompanyJobService.setNameForMap(beans, "jobId", "jobName");
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteAllActGroupUserByGroupId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        QueryWrapper<ActGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ActGroupUser::getGroupId), groupId);
        remove(queryWrapper);
    }

    @Override
    public List<ActGroupUser> queryAllActGroupUser() {
        List<ActGroupUser> actGroupUsers = list();
        iAuthUserService.setDataMation(actGroupUsers, ActGroupUser::getUserId);
        return actGroupUsers;
    }

    @Override
    public List<ActGroupUser> queryActGroupUser(List<String> groupIds) {
        if (CollectionUtil.isEmpty(groupIds)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<ActGroupUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ActGroupUser::getGroupId), groupIds);
        List<ActGroupUser> actGroupUsers = list(queryWrapper);
        iAuthUserService.setDataMation(actGroupUsers, ActGroupUser::getUserId);
        return actGroupUsers;
    }
}

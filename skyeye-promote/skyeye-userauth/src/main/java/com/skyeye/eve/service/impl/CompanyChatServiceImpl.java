/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.eve.dao.CompanyChatDao;
import com.skyeye.eve.service.CompanyChatService;
import com.skyeye.organization.service.ICompanyJobService;
import com.skyeye.organization.service.ICompanyService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.tenant.service.TenantUserService;
import com.skyeye.websocket.TalkWebSocket;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @ClassName: CompanyChatServiceImpl
 * @Description: 聊天管理--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/5/25 10:36
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class CompanyChatServiceImpl implements CompanyChatService {

    @Autowired
    private CompanyChatDao companyChatDao;

    @Autowired
    private ICompanyService iCompanyService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobService iCompanyJobService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Autowired
    private TenantUserService tenantUserService;

    /**
     * 获取好友列表，群聊信息，个人信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void getList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        String userId = user.get("id").toString();
        map.put("userId", userId);
        String tenantId = tenantEnable ? TenantContext.getTenantId() : StrUtil.EMPTY;
        // 获取个人信息
        Map<String, Object> mine = companyChatDao.queryUserMineByUserId(map);
        if (tenantEnable) {
            // 多租户模式下，获取当前租户下的用户信息
            tenantUserService.setThisTenantUserToDefault(mine);
            map.put("tenantId", TenantContext.getTenantId());
        }
        iCompanyService.setNameForMap(mine, "companyId", "companyName");
        iDepmentService.setNameForMap(mine, "departmentId", "departmentName");

        // 获取聊天组
        List<Map<String, Object>> group = companyChatDao.queryUserGroupByUserId(map);

        // 获取公司部门
        List<Map<String, Object>> companyDepartment = companyChatDao.queryCompanyDepartmentByUserId(map);
        if (CollectionUtil.isNotEmpty(companyDepartment)) {
            List<String> departIds = companyDepartment.stream().map(m -> m.get("id").toString()).collect(Collectors.toList());
            List<String> notInUserIds = Arrays.asList(CommonConstants.ADMIN_USER_ID, userId);
            List<Map<String, Object>> userList = companyChatDao.queryDepartmentUserByDepartId(departIds, notInUserIds, tenantId);
            if (tenantEnable) {
                // 多租户模式下，获取当前租户下的用户信息
                userList = tenantUserService.setThisTenantUserToDefault(userList, "staffId");
            }
            iCompanyService.setNameForMap(userList, "companyId", "companyName");
            iDepmentService.setNameForMap(userList, "departmentId", "departmentName");
            iCompanyJobService.setNameForMap(userList, "jobId", "jobName");
            if (CollectionUtil.isNotEmpty(userList)) {
                Set<String> uId = TalkWebSocket.getOnlineUserId();
                for (Map<String, Object> u : userList) {
                    if (uId.contains(u.get("id").toString())) {
                        u.put("status", "online");
                    } else {
                        u.put("status", "offline");
                    }
                }

                Map<String, List<Map<String, Object>>> userMap = userList.stream().collect(Collectors.groupingBy(u -> u.get("departmentId").toString()));
                // 循环获取分组的人列表
                for (Map<String, Object> depart : companyDepartment) {
                    String departId = depart.get("id").toString();
                    depart.put("list", userMap.get(departId));
                }
            }
        }


        map.clear();
        map.put("friend", companyDepartment);
        map.put("group", group);
        map.put("mine", mine);
        outputObject.setBean(map);
    }

    /**
     * 编辑签名
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editUserSignByUserId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("userId", user.get("id"));
        companyChatDao.editUserSignByUserId(map);
    }

}

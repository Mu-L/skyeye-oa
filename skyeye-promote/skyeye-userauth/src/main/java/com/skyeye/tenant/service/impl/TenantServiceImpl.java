/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.TenantTypeEnum;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.PropertiesUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.tenant.dao.TenantDao;
import com.skyeye.tenant.entity.Tenant;
import com.skyeye.tenant.entity.TenantApp;
import com.skyeye.tenant.entity.TenantAppLink;
import com.skyeye.tenant.service.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantServiceImpl
 * @Description: 租户服务实现层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/28 20:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Slf4j
@Service
@SkyeyeService(name = "租户管理", groupName = "租户管理", tenant = TenantEnum.PLATE)
public class TenantServiceImpl extends SkyeyeBusinessServiceImpl<TenantDao, Tenant> implements TenantService {

    @Autowired
    private TenantAppLinkService tenantAppLinkService;

    @Autowired
    private TenantAppService tenantAppService;

    @Autowired
    private TenantAppMenuService tenantAppMenuService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Override
    public void createPrepose(Tenant entity) {
        entity.setAccountNum(CommonNumConstants.NUM_ZERO);
    }

    @Override
    protected void createPostpose(Tenant entity, String userId) {
        // 创建消息通知，同步数据到该租户下
        Map<String, Object> jobBody = new HashMap<>();
        jobBody.put("whetherCreatTask", false);
        jobBody.put("content", JSONUtil.toJsonStr(entity));
        jobBody.put("userId", userId);
        String topic = PropertiesUtil.getPropertiesValue("${topic.synchronize-data-to-tenants}");
        jobBody.put("topic", topic);
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(jobBody));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
        log.info("租户创建成功，同步数据到该租户下");
    }

    @Override
    protected void deletePreExecution(String id) {
        if (StrUtil.equals(id, TenantTypeEnum.PLATFORM.getCode())) {
            throw new CustomException("平台租户不能删除");
        }
        super.deletePreExecution(id);
    }

    @Override
    @IgnoreTenant
    public Tenant selectById(String id) {
        Tenant tenant = super.selectById(id);
        List<TenantAppLink> tenantAppLinkList = tenantAppLinkService.selectByTenantId(id);
        if (CollectionUtil.isNotEmpty(tenantAppLinkList)) {
            List<String> appIds = tenantAppLinkList.stream().map(TenantAppLink::getAppId).collect(Collectors.toList());
            Map<String, TenantApp> tenantAppMap = tenantAppService.queryTenantAppByAppId(appIds.toArray(new String[]{}));
            tenantAppLinkList.forEach(tenantAppLink -> {
                tenantAppLink.setAppMation(tenantAppMap.get(tenantAppLink.getAppId()));
            });
        }
        tenant.setTenantAppLinkList(tenantAppLinkList);
        return tenant;
    }

    @Override
    @IgnoreTenant
    public void selectByIds(InputObject inputObject, OutputObject outputObject) {
        super.selectByIds(inputObject, outputObject);
    }

    @Override
    @IgnoreTenant
    public List<Tenant> selectByIds(String... ids) {
        return super.selectByIds(ids);
    }

    @Override
    public void editTenantAccountNumber(String tenantId, Integer accountNumber) {
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, tenantId);
        Tenant tenant = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(tenant) && StrUtil.isNotEmpty(tenant.getId())) {
            Integer accountNum = tenant.getAccountNum() + accountNumber;
            tenant.setAccountNum(accountNum);
            updateById(tenant);
            refreshCache(tenantId);
        } else {
            throw new CustomException("租户不存在");
        }
    }

    @Override
    public void queryAllTenantList(InputObject inputObject, OutputObject outputObject) {
        List<Tenant> tenants = queryAllData();
        outputObject.setBeans(tenants);
        outputObject.settotal(tenants.size());
    }

    @Override
    @IgnoreTenant
    public List<String> queryAllMenuListByTenantId(String tenantId, Integer type) {
        if (StrUtil.isEmpty(tenantId)) {
            return null;
        }
        // 查询租户下的所有应用
        List<TenantAppLink> tenantAppLinkList = tenantAppLinkService.selectByTenantId(tenantId);
        if (CollectionUtil.isEmpty(tenantAppLinkList)) {
            return null;
        }
        // 校验应用是否过期，将过期的应用提出掉
        String currentTime = DateUtil.getYmdTimeAndToString();
        List<String> appIds = tenantAppLinkList.stream().filter(tenantAppLink -> {
            if (DateUtil.getDistanceDay(tenantAppLink.getStartTime(), currentTime) >= 0 && DateUtil.getDistanceDay(currentTime, tenantAppLink.getEndTime()) >= 0) {
                // startTime <= 当前时间 <= endTime
                return true;
            }
            return false;
        }).map(TenantAppLink::getAppId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(appIds)) {
            return null;
        }
        List<String> menuIds = tenantAppMenuService.selectObjectIdsByAppId(appIds, type);
        return menuIds;
    }

    @Override
    @IgnoreTenant
    public void checkTenantAccountNum(String tenantId) {
        if (StrUtil.equals(tenantId, TenantTypeEnum.PLATFORM.getCode())) {
            // 平台租户不限制账号数量
            return;
        }
        Tenant tenant = selectById(tenantId);
        if (ObjectUtil.isEmpty(tenant) || StrUtil.isEmpty(tenant.getId())) {
            throw new CustomException("租户不存在");
        }
        // 获取租户下的所有用户数量
        long count = tenantUserService.getTenantUserCountByTenantId(tenantId);
        if (count >= tenant.getAccountNum()) {
            throw new CustomException("租户账号数量已达上限");
        }
    }

    @Override
    @IgnoreTenant
    public void queryAllTenantListByKeyword(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String keyword = params.get("keyword").toString();
        if (StrUtil.isEmpty(keyword)) {
            return;
        }
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(Tenant::getName), keyword);
        List<Tenant> tenantList = list(queryWrapper);
        outputObject.setBeans(tenantList);
        outputObject.settotal(tenantList.size());
    }
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.operate.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.attr.classenum.AttrSymbols;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.business.entity.BusinessApi;
import com.skyeye.business.service.BusinessApiService;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.dsform.entity.DsFormPage;
import com.skyeye.dsform.service.DsFormPageService;
import com.skyeye.operate.classenum.EventType;
import com.skyeye.operate.classenum.MenuPageType;
import com.skyeye.operate.dao.OperateDao;
import com.skyeye.operate.entity.Operate;
import com.skyeye.operate.entity.OperateOpenPage;
import com.skyeye.operate.service.OperateOpenPageService;
import com.skyeye.operate.service.OperateService;
import com.skyeye.rest.report.service.IReportPageService;
import com.skyeye.server.entity.ServiceBeanCustom;
import com.skyeye.server.service.ServiceBeanCustomService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: OperateServiceImpl
 * @Description: 操作管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2023/1/29 18:07
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@Slf4j
@SkyeyeService(name = "操作管理", groupName = "系统公共模块", tenant = TenantEnum.NO_ISOLATION)
public class OperateServiceImpl extends SkyeyeBusinessServiceImpl<OperateDao, Operate> implements OperateService {

    @Autowired
    private BusinessApiService businessApiService;

    @Autowired
    private OperateOpenPageService operateOpenPageService;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private DsFormPageService dsFormPageService;

    @Autowired
    private ServiceBeanCustomService serviceBeanCustomService;

    @Autowired
    private IReportPageService iReportPageService;

    @Override
    public void queryOperateList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String appId = params.get("appId").toString();
        String className = params.get("className").toString();
        List<Operate> operateList = getOperatesByClassName(appId, className);

        iAuthUserService.setName(operateList, "createId", "createName");
        iAuthUserService.setName(operateList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(operateList);
        outputObject.settotal(operateList.size());
    }

    @Override
    public List<Operate> getOperatesByClassName(String appId, String className) {
        String cacheKey = getCacheKeyByClassName(appId, className);
        List<Operate> operateList = redisCache.getList(cacheKey, key -> {
            QueryWrapper<Operate> wrapper = new QueryWrapper<>();
            wrapper.orderByAsc(MybatisPlusUtil.toColumns(Operate::getOrderBy));
            wrapper.eq(MybatisPlusUtil.toColumns(Operate::getClassName), className);
            List<Operate> operates = list(wrapper);
            List<String> ids = operates.stream().map(Operate::getId).collect(Collectors.toList());

            // 获取接口配置信息
            Map<String, BusinessApi> businessApiMap = businessApiService.selectByObjectIds(ids);
            // 获取新开页面配置信息
            Map<String, OperateOpenPage> operateOpenPageMap = operateOpenPageService.selectByOperateIds(ids);
            operates.forEach(operate -> {
                String id = operate.getId();
                operate.setBusinessApi(businessApiMap.get(id));
                operate.setOperateOpenPage(operateOpenPageMap.get(id));
            });

            return operates;
        }, RedisConstants.ALL_USE_TIME, Operate.class);

        operateList.forEach(operate -> {
            if (CollectionUtil.isNotEmpty(operate.getShowConditionList())) {
                operate.getShowConditionList().forEach(condition -> {
                    condition.setSymbolsMark(AttrSymbols.getSymbols(condition.getSymbols()));
                });
            }
        });
        return operateList;
    }

    @Override
    public void writePostpose(Operate entity, String userId) {
        super.writePostpose(entity, userId);
        if (StrUtil.equals(entity.getEventType(), EventType.AJAX.getKey())) {
            // 保存请求事件
            BusinessApi businessApi = entity.getBusinessApi();
            businessApi.setObjectId(entity.getId());
            businessApi.setObjectKey(getServiceClassName());
            businessApiService.createEntity(businessApi, userId);
        } else if (StrUtil.equals(entity.getEventType(), EventType.OPEN_PAGE.getKey())) {
            // 保存新开页面事件
            OperateOpenPage operateOpenPage = entity.getOperateOpenPage();
            operateOpenPage.setOperateId(entity.getId());
            operateOpenPageService.createEntity(operateOpenPage, userId);
        }
        String cacheKey = getCacheKeyByClassName(entity.getAppId(), entity.getClassName());
        jedisClientService.del(cacheKey);
    }

    /**
     * 根据id查询数据
     *
     * @param id
     * @return
     */
    @Override
    public Operate selectById(String id) {
        Operate operate = super.selectById(id);
        if (StrUtil.equals(operate.getEventType(), EventType.OPEN_PAGE.getKey())) {
            // 新开页面
            OperateOpenPage operateOpenPage = operate.getOperateOpenPage();
            if (operateOpenPage.getType() == MenuPageType.LAYOUT.getKey()) {
                // 表单布局
                try {
                    DsFormPage dsFormPage = dsFormPageService.getDataFromDb(operateOpenPage.getPageUrl());
                    ServiceBeanCustom serviceBeanCustom = serviceBeanCustomService.selectServiceBeanCustom(dsFormPage.getAppId(), dsFormPage.getClassName());
                    dsFormPage.setServiceBeanCustom(serviceBeanCustom);
                    operateOpenPage.setDsFormPage(dsFormPage);
                } catch (Exception ex) {
                    log.info(String.format(Locale.ROOT, "FormPage %s is not exits. 【selectById】", operateOpenPage.getPageUrl()));
                }
            } else if (operateOpenPage.getType() == MenuPageType.REPORT.getKey()) {
                // 报表页面
                operateOpenPage.setReportPage(iReportPageService.queryDataMationById(operateOpenPage.getPageUrl()));
            }
        }
        return operate;
    }

    @Override
    public Operate getDataFromDb(String id) {
        Operate operate = super.getDataFromDb(id);
        if (StrUtil.equals(operate.getEventType(), EventType.AJAX.getKey())) {
            // 查询请求事件
            BusinessApi businessApi = businessApiService.selectByObjectId(id);
            operate.setBusinessApi(businessApi);
        } else if (StrUtil.equals(operate.getEventType(), EventType.OPEN_PAGE.getKey())) {
            // 查询新开页面事件
            OperateOpenPage operateOpenPage = operateOpenPageService.selectByOperateId(id);
            operate.setOperateOpenPage(operateOpenPage);
        }
        return operate;
    }

    @Override
    public void deletePostpose(Operate entity) {
        // 删除的时候不用区分事件类型，直接每张表都删除一次
        businessApiService.deleteByObjectId(entity.getId());
        operateOpenPageService.deleteByOperateId(entity.getId());
        // 清空缓存
        String cacheKey = getCacheKeyByClassName(entity.getAppId(), entity.getClassName());
        jedisClientService.del(cacheKey);
    }

    @Override
    public void deleteOperate(String appId, String className) {
        QueryWrapper<Operate> wrapper = new QueryWrapper<>();
        wrapper.eq(MybatisPlusUtil.toColumns(Operate::getAppId), appId);
        wrapper.eq(MybatisPlusUtil.toColumns(Operate::getClassName), className);
        List<Operate> list = list(wrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            return;
        }
        remove(wrapper);
        // 清空缓存
        String cacheKey = getCacheKeyByClassName(appId, className);
        jedisClientService.del(cacheKey);
        // 删除的时候不用区分事件类型，直接每张表都删除一次
        List<String> ids = list.stream().map(Operate::getId).collect(Collectors.toList());
        businessApiService.deleteByObjectId(ids);
        operateOpenPageService.deleteByOperateId(ids);
    }

    private String getCacheKeyByClassName(String appId, String className) {
        return String.format(Locale.ROOT, "skyeye:operate:className:%s:%s", appId, className);
    }

}

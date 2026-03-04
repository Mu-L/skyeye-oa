/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.ordertype.entity.SealOrderType;
import com.skyeye.ordertype.service.SealOrderTypeService;
import com.skyeye.patrol.dao.PatrolItemDao;
import com.skyeye.patrol.entity.PatrolItem;
import com.skyeye.patrol.service.PatrolItemService;
import com.skyeye.rest.shop.service.IShopStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolItemServiceImpl
 * @Description: 巡检项目服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检项目", groupName = "巡检项目")
public class PatrolItemServiceImpl extends SkyeyeBusinessServiceImpl<PatrolItemDao, PatrolItem> implements PatrolItemService {

    @Autowired
    private SealOrderTypeService sealOrderTypeService;

    @Autowired
    private IShopStoreService iShopStoreService;

    @Override
    protected QueryWrapper<PatrolItem> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PatrolItem> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolItem::getEnabled), commonPageInfo.getEnabled());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolItem::getStoreId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置工单类型信息
        List<String> orderTypeIds = beans.stream()
            .filter(bean -> bean.get("orderTypeId") != null)
            .map(bean -> bean.get("orderTypeId").toString())
            .distinct()
            .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(orderTypeIds)) {
            List<SealOrderType> orderTypes = sealOrderTypeService.selectByIds(orderTypeIds.toArray(new String[]{}));
            Map<String, SealOrderType> orderTypeMap = orderTypes.stream()
                .collect(Collectors.toMap(SealOrderType::getId, orderType -> orderType));
            beans.forEach(bean -> {
                if (bean.get("orderTypeId") != null) {
                    bean.put("orderTypeMation", orderTypeMap.get(bean.get("orderTypeId").toString()));
                }
            });
        }
        // 设置门店信息
        iShopStoreService.setMationForMap(beans, "storeId", "storeMation");
        return beans;
    }

    @Override
    public void createPrepose(PatrolItem entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
    }

    @Override
    public PatrolItem selectById(String id) {
        PatrolItem patrolItem = super.selectById(id);
        // 设置工单类型信息
        if (StrUtil.isNotEmpty(patrolItem.getOrderTypeId())) {
            SealOrderType orderType = sealOrderTypeService.selectById(patrolItem.getOrderTypeId());
            patrolItem.setOrderTypeMation(orderType);
        }
        // 设置门店信息（门店在 shop-member 模块，暂时不处理，后续可通过 Feign 调用）
        return patrolItem;
    }

    @Override
    public void queryAllPatrolItemList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String enabled = params.get("enabled").toString();
        QueryWrapper<PatrolItem> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(enabled)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolItem::getEnabled), enabled);
        }
        List<PatrolItem> patrolItemList = list(queryWrapper);
        outputObject.setBeans(patrolItemList);
        outputObject.settotal(patrolItemList.size());
    }
}


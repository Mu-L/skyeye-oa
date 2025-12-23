/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheme.dao.ProSchemeBudgetDetailDao;
import com.skyeye.scheme.entity.ProSchemeBudgetDetail;
import com.skyeye.scheme.service.ProSchemeBudgetDetailService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ProSchemeBudgetDetailServiceImpl
 * @Description: 项目方案预算明细服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目方案预算明细", groupName = "项目方案管理")
public class ProSchemeBudgetDetailServiceImpl extends SkyeyeBusinessServiceImpl<ProSchemeBudgetDetailDao, ProSchemeBudgetDetail> implements ProSchemeBudgetDetailService {

    @Override
    public void saveList(String schemeId, List<ProSchemeBudgetDetail> beans) {
        deleteBudgetDetailBySchemeId(schemeId);
        if (CollectionUtil.isNotEmpty(beans)) {
            int orderBy = 1;
            for (ProSchemeBudgetDetail proSchemeBudgetDetail : beans) {
                proSchemeBudgetDetail.setSchemeId(schemeId);
                proSchemeBudgetDetail.setOrderBy(orderBy);
                orderBy++;
            }
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            createEntity(beans, userId);
        }
    }

    @Override
    public List<ProSchemeBudgetDetail> queryBudgetDetailBySchemeId(String schemeId) {
        if (StrUtil.isEmpty(schemeId)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<ProSchemeBudgetDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProSchemeBudgetDetail::getSchemeId), schemeId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ProSchemeBudgetDetail::getOrderBy));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<ProSchemeBudgetDetail>> queryBudgetDetailBySchemeIds(List<String> schemeIds) {
        if (CollectionUtil.isEmpty(schemeIds)) {
            return cn.hutool.core.map.MapUtil.newHashMap();
        }
        QueryWrapper<ProSchemeBudgetDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ProSchemeBudgetDetail::getSchemeId), schemeIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(ProSchemeBudgetDetail::getOrderBy));
        List<ProSchemeBudgetDetail> detailList = list(queryWrapper);
        return detailList.stream().collect(Collectors.groupingBy(ProSchemeBudgetDetail::getSchemeId));
    }

    @Override
    public void deleteBudgetDetailBySchemeId(String schemeId) {
        if (StrUtil.isEmpty(schemeId)) {
            return;
        }
        QueryWrapper<ProSchemeBudgetDetail> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProSchemeBudgetDetail::getSchemeId), schemeId);
        remove(queryWrapper);
    }

}


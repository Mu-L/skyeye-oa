/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.scheme.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.scheme.dao.ProSchemeDao;
import com.skyeye.scheme.entity.ProScheme;
import com.skyeye.scheme.entity.ProSchemeBudgetDetail;
import com.skyeye.scheme.service.ProSchemeBudgetDetailService;
import com.skyeye.scheme.service.ProSchemeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: ProSchemeServiceImpl
 * @Description: 项目方案服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "项目方案管理", groupName = "项目方案管理")
public class ProSchemeServiceImpl extends SkyeyeBusinessServiceImpl<ProSchemeDao, ProScheme> implements ProSchemeService {

    @Autowired
    private ProSchemeBudgetDetailService budgetDetailService;

    @Override
    public QueryWrapper<ProScheme> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProScheme> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 最新版本
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getObjectId), commonPageInfo.getObjectId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getFromId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getFromId), commonPageInfo.getFromId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("projectId"))) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getProjectId), commonPageInfo.getCustomParamsMapStr("projectId"));
        }
        return queryWrapper;
    }

    @Override
    public String createEntity(ProScheme entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    protected void createPrepose(ProScheme entity) {
        // 计算预算明细小计和总预算
        calculateBudget(entity);
        if (StrUtil.isNotEmpty(entity.getId())) {
            // 查询上一个版本的信息
            ProScheme preVersionProScheme = selectById(entity.getId());
            entity.setSchemeCode(preVersionProScheme.getSchemeCode());
            // 如果id不为空，说明可能是创建新版本或者编辑已有版本，要清空缓存，防止创建新版本情况下，老版本数据缓存
            clearCache(entity.getId());
        } else {
            // 生成单据编号
            Map<String, Object> business = BeanUtil.beanToMap(entity);
            String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
            entity.setSchemeCode(oddNumber);
        }
    }

    @Override
    public String updateEntity(ProScheme entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    protected void writePostpose(ProScheme entity, String userId) {
        super.writePostpose(entity, userId);

        // 保存预算明细
        budgetDetailService.saveList(entity.getId(), entity.getBudgetDetailList());
    }

    /**
     * 计算预算明细小计和总预算
     *
     * @param entity 方案实体
     */
    private void calculateBudget(ProScheme entity) {
        if (CollectionUtil.isEmpty(entity.getBudgetDetailList())) {
            entity.setBudget("0");
            return;
        }

        String totalBudget = "0";
        // 计算每个明细的小计（数量 * 单价）
        for (ProSchemeBudgetDetail detail : entity.getBudgetDetailList()) {
            String quantity = StrUtil.isEmpty(detail.getQuantity()) ? "0" : detail.getQuantity();
            String unitPrice = StrUtil.isEmpty(detail.getUnitPrice()) ? "0" : detail.getUnitPrice();
            // 计算小计：数量 * 单价
            String subtotal = CalculationUtil.multiply(quantity, unitPrice, CommonNumConstants.NUM_TWO);
            detail.setSubtotal(subtotal);
            // 累加总预算
            totalBudget = CalculationUtil.add(totalBudget, subtotal, CommonNumConstants.NUM_TWO);
        }

        // 设置总预算
        entity.setBudget(totalBudget);
    }

    @Override
    public ProScheme getDataFromDb(String id) {
        ProScheme scheme = super.getDataFromDb(id);
        // 设置预算明细信息
        scheme.setBudgetDetailList(budgetDetailService.queryBudgetDetailBySchemeId(scheme.getId()));
        return scheme;
    }

    @Override
    protected List<ProScheme> getDataFromDb(List<String> idList) {
        List<ProScheme> schemeList = super.getDataFromDb(idList);
        List<String> ids = schemeList.stream().map(ProScheme::getId).collect(Collectors.toList());
        // 设置预算明细信息
        Map<String, List<ProSchemeBudgetDetail>> budgetDetailMap = budgetDetailService.queryBudgetDetailBySchemeIds(ids);
        schemeList.forEach(scheme -> {
            String id = scheme.getId();
            scheme.setBudgetDetailList(budgetDetailMap.get(id));
        });
        return schemeList;
    }

    @Override
    public void deletePostpose(String id) {
        budgetDetailService.deleteBudgetDetailBySchemeId(id);
    }

    /**
     * 根据项目id查询方案列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySchemeListByProjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String projectId = map.get("projectId").toString();
        if (StrUtil.isEmpty(projectId)) {
            return;
        }
        QueryWrapper<ProScheme> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getProjectId), projectId);
        // 已经发布的最新版本
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getWhetherPublish), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ProScheme::getCreateTime));
        List<ProScheme> schemeList = list(queryWrapper);
        outputObject.setBeans(schemeList);
        outputObject.settotal(schemeList.size());
    }

    @Override
    public void querySchemeListBySchemeCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String schemeCode = map.get("schemeCode").toString();

        QueryWrapper<ProScheme> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProScheme::getSchemeCode), schemeCode);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ProScheme::getLargeVersion));
        List<ProScheme> schemeList = list(queryWrapper);
        outputObject.setBeans(schemeList);
        outputObject.settotal(schemeList.size());
    }

}


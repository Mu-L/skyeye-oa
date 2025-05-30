/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.holder.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.depot.service.ErpDepotService;
import com.skyeye.holder.classenum.HolderNormsChildState;
import com.skyeye.holder.dao.HolderNormsDao;
import com.skyeye.holder.entity.HolderNorms;
import com.skyeye.holder.entity.HolderNormsChild;
import com.skyeye.holder.service.HolderNormsChildService;
import com.skyeye.holder.service.HolderNormsService;
import com.skyeye.material.service.MaterialNormsService;
import com.skyeye.material.service.MaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: HolderNormsServiceImpl
 * @Description: 关联的客户/供应商/会员购买或者出售的商品信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/9/2 21:25
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "关联的客户/供应商/会员购买或者出售的商品信息管理", groupName = "关联的客户/供应商/会员购买或者出售的商品信息管理", manageShow = false)
public class HolderNormsServiceImpl extends SkyeyeBusinessServiceImpl<HolderNormsDao, HolderNorms> implements HolderNormsService {

    @Autowired
    private MaterialService materialService;

    @Autowired
    private MaterialNormsService materialNormsService;

    @Autowired
    private HolderNormsChildService holderNormsChildService;

    @Autowired
    protected ErpDepotService erpDepotService;

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public QueryWrapper<HolderNorms> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<HolderNorms> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getHolderId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getHolderKey), commonPageInfo.getHolderKey());
        }
        if (tenantEnable) {
            queryWrapper.eq(CommonConstants.TENANT_ID_FIELD, TenantContext.getTenantId());
        }
        queryWrapper.select(MybatisPlusUtil.toColumns(HolderNorms::getMaterialId),
            MybatisPlusUtil.toColumns(HolderNorms::getHolderId), MybatisPlusUtil.toColumns(HolderNorms::getHolderKey),
            MybatisPlusUtil.toColumns(HolderNorms::getNormsId), MybatisPlusUtil.toColumns(HolderNorms::getAllOperNumber));
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(HolderNorms::getNormsId));
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        materialService.setMationForMap(beans, "materialId", "materialMation");
        materialNormsService.setMationForMap(beans, "normsId", "normsMation");
        return beans;
    }

    @Override
    public void queryHolderMaterialListByHolder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String holderId = map.get("holderId").toString();
        String holderKey = map.get("holderKey").toString();
        if (StrUtil.isEmpty(holderId) || StrUtil.isEmpty(holderKey)) {
            return;
        }
        QueryWrapper<HolderNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getHolderId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getHolderKey), holderKey);
        queryWrapper.groupBy(MybatisPlusUtil.toColumns(HolderNorms::getMaterialId));
        List<HolderNorms> holderNormsList = list(queryWrapper);
        // 设置产品信息
        materialService.setDataMation(holderNormsList, HolderNorms::getMaterialId);
        holderNormsList.forEach(holderNorms -> {
            holderNorms.setId(holderNorms.getMaterialId());
            if (ObjectUtil.isNotEmpty(holderNorms.getMaterialMation())) {
                holderNorms.setName(holderNorms.getMaterialMation().getName());
            }
        });
        outputObject.setBeans(holderNormsList);
        outputObject.settotal(holderNormsList.size());
    }

    @Override
    public void writePostpose(List<HolderNorms> entity, String userId) {
        // 构造并保存购买或者出售的商品的编码信息
        List<HolderNormsChild> holderNormsChildList = new ArrayList<>();
        entity.forEach(holderNorms -> {
            if (CollectionUtil.isNotEmpty(holderNorms.getNormsCodeList())) {
                holderNorms.getNormsCodeList().forEach(normsCode -> {
                    HolderNormsChild holderNormsChild = new HolderNormsChild();
                    holderNormsChild.setNormsCodeNum(normsCode);
                    holderNormsChild.setState(HolderNormsChildState.NORMAL_TRANSACTIONS.getKey());
                    holderNormsChild.setHolderId(holderNorms.getHolderId());
                    holderNormsChild.setHolderKey(holderNorms.getHolderKey());
                    holderNormsChild.setMaterialId(holderNorms.getId());
                    holderNormsChild.setNormsId(holderNorms.getNormsId());
                    holderNormsChild.setParentId(holderNorms.getId());
                    holderNormsChildList.add(holderNormsChild);
                });
            }
        });
        if (CollectionUtil.isNotEmpty(holderNormsChildList)) {
            holderNormsChildService.createEntity(holderNormsChildList, StrUtil.EMPTY);
        }
    }

    @Override
    public List<String> queryHolderMaterialIdListByHolderId(String holderId) {
        QueryWrapper<HolderNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID);
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getHolderId), holderId);
        List<HolderNorms> holderNormsList = list(queryWrapper);
        if (CollectionUtil.isEmpty(holderNormsList)) {
            return CollectionUtil.newArrayList();
        }
        return holderNormsList.stream().map(HolderNorms::getId).collect(Collectors.toList());
    }

    @Override
    public void queryHolderMaterialNormsListByHolder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String holderId = map.get("holderId").toString();
        String holderKey = map.get("holderKey").toString();
        String materialId = map.get("materialId").toString();
        if (StrUtil.isEmpty(holderId) || StrUtil.isEmpty(holderKey) || StrUtil.isEmpty(materialId)) {
            return;
        }
        QueryWrapper<HolderNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getHolderId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getHolderKey), holderKey);
        queryWrapper.eq(MybatisPlusUtil.toColumns(HolderNorms::getMaterialId), materialId);

        queryWrapper.groupBy(MybatisPlusUtil.toColumns(HolderNorms::getNormsId));
        List<HolderNorms> holderNormsList = list(queryWrapper);
        // 设置规格信息
        materialNormsService.setDataMation(holderNormsList, HolderNorms::getNormsId);
        holderNormsList.forEach(holderNorms -> {
            holderNorms.setId(holderNorms.getNormsId());
            if (ObjectUtil.isNotEmpty(holderNorms.getNormsMation())) {
                holderNorms.setName(holderNorms.getNormsMation().getName());
            }
        });
        outputObject.setBeans(holderNormsList);
        outputObject.settotal(holderNormsList.size());
    }

    @Override
    public void queryHolderMaterialNormsCodeListByHolder(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String holderId = map.get("holderId").toString();
        String holderKey = map.get("holderKey").toString();
        String normsId = map.get("normsId").toString();
        if (StrUtil.isEmpty(holderId) || StrUtil.isEmpty(holderKey) || StrUtil.isEmpty(normsId)) {
            return;
        }
        List<Map<String, Object>> beans = holderNormsChildService.queryHolderMaterialNormsCodeListByHolder(holderId, holderKey, normsId);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}

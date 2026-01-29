/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.construction.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.construction.dao.ProConstructionDao;
import com.skyeye.construction.entity.ProConstruction;
import com.skyeye.construction.entity.ProConstructionMaterial;
import com.skyeye.construction.service.ProConstructionMaterialService;
import com.skyeye.construction.service.ProConstructionService;
import com.skyeye.construction.service.ProConstructionStepService;
import com.skyeye.erp.service.IMaterialNormsService;
import com.skyeye.erp.service.IMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: ProConstructionServiceImpl
 * @Description: 施工方案Service实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "施工方案", groupName = "项目施工管理", flowable = true)
public class ProConstructionServiceImpl extends SkyeyeBusinessServiceImpl<ProConstructionDao, ProConstruction> implements ProConstructionService {

    @Autowired
    private ProConstructionStepService proConstructionStepService;

    @Autowired
    private ProConstructionMaterialService proConstructionMaterialService;

    @Autowired
    private IMaterialService iMaterialService;

    @Autowired
    private IMaterialNormsService iMaterialNormsService;

    @Override
    protected QueryWrapper<ProConstruction> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ProConstruction> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getCustomParamsMapStr("projectId"))) {
            // 根据项目ID过滤
            queryWrapper.eq(MybatisPlusUtil.toColumns(ProConstruction::getProjectId), commonPageInfo.getCustomParamsMapStr("projectId"));
        }
        // 只查询最新版本
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProConstruction::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        return queryWrapper;
    }

    @Override
    public String createEntity(ProConstruction entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(ProConstruction entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    public void writePostpose(ProConstruction entity, String userId) {
        // 保存施工步骤
        proConstructionStepService.saveConstructionSteps(entity.getId(), entity.getConstructionStepList(), userId);
        // 保存项目材料清单
        proConstructionMaterialService.saveConstructionMaterials(entity.getId(), entity.getConstructionMaterialList(), userId);
        super.writePostpose(entity, userId);
    }

    @Override
    public ProConstruction getDataFromDb(String id) {
        ProConstruction construction = super.getDataFromDb(id);
        if (construction != null) {
            // 设置施工步骤
            construction.setConstructionStepList(proConstructionStepService.queryListByParentId(id));
            // 设置项目材料清单
            construction.setConstructionMaterialList(proConstructionMaterialService.queryListByParentId(id));
        }
        return construction;
    }

    @Override
    public ProConstruction selectById(String id) {
        ProConstruction proConstruction = super.selectById(id);
        // 设置ERP商品信息
        iMaterialService.setDataMation(proConstruction.getConstructionMaterialList(), ProConstructionMaterial::getMaterialId);

        // 设置ERP商品规格信息
        iMaterialNormsService.setDataMation(proConstruction.getConstructionMaterialList(), ProConstructionMaterial::getMaterialNormsId);
        return proConstruction;
    }

    @Override
    public void deletePostpose(String id) {
        // 删除施工步骤
        proConstructionStepService.deleteByParentId(id);
        // 删除项目材料清单
        proConstructionMaterialService.deleteByParentId(id);
    }

    @Override
    public void queryConstructionListByVersionNo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String versionNo = map.get("versionNo").toString();

        QueryWrapper<ProConstruction> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProConstruction::getVersionNo), versionNo);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ProConstruction::getLargeVersion));
        List<ProConstruction> constructionList = list(queryWrapper);
        outputObject.setBeans(constructionList);
        outputObject.settotal(constructionList.size());
    }

}
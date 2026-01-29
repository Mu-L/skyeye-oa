/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/dromara/skyeye
 ******************************************************************************/

package com.skyeye.construction.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.NumberUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.construction.dao.ProConstructionMaterialDao;
import com.skyeye.construction.entity.ProConstructionMaterial;
import com.skyeye.construction.service.ProConstructionMaterialService;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

/**
 * @ClassName: ProConstructionMaterialServiceImpl
 * @Description: 施工材料清单Service实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/23 12:09
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "施工材料清单", groupName = "项目施工管理", manageShow = false)
public class ProConstructionMaterialServiceImpl extends SkyeyeBusinessServiceImpl<ProConstructionMaterialDao, ProConstructionMaterial> implements ProConstructionMaterialService {

    /**
     * 根据施工方案ID查询材料清单列表
     */
    public List<ProConstructionMaterial> queryListByParentId(String parentId) {
        QueryWrapper<ProConstructionMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProConstructionMaterial::getConstructionId), parentId);
        List<ProConstructionMaterial> materialList = list(queryWrapper);
        return materialList;
    }

    /**
     * 根据施工方案ID删除所有关联的材料清单
     */
    public void deleteByParentId(String parentId) {
        QueryWrapper<ProConstructionMaterial> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ProConstructionMaterial::getConstructionId), parentId);
        remove(queryWrapper);
    }

    /**
     * 保存施工材料清单（先删除后新增）
     */
    @Override
    public void saveConstructionMaterials(String constructionId, List<ProConstructionMaterial> materialList, String userId) {
        // 先删除该施工方案下的所有材料，再重新插入
        deleteByParentId(constructionId);
        if (CollectionUtil.isEmpty(materialList)) {
            return;
        }

        // 保存新的材料清单
        for (ProConstructionMaterial material : materialList) {
            material.setConstructionId(constructionId);
            // 计算预估总价
            calculateTotalPrice(material);
        }
        createEntity(materialList, userId);
    }

    /**
     * 计算预估总价
     */
    private void calculateTotalPrice(ProConstructionMaterial material) {
        BigDecimal unitPrice = NumberUtil.toBigDecimal(material.getUnitPrice());
        BigDecimal quantity = NumberUtil.toBigDecimal(material.getEstimatedQuantity());
        // 总价 = 单价 × 数量
        BigDecimal totalPrice = unitPrice.multiply(quantity);
        material.setTotalPrice(totalPrice.toString());
    }

}
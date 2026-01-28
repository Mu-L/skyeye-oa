/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.enterprise.dao.EnterpriseProductNormsDao;
import com.skyeye.enterprise.entity.EnterpriseProduct;
import com.skyeye.enterprise.entity.EnterpriseProductNorms;
import com.skyeye.enterprise.service.EnterpriseProductNormsService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: EnterpriseProductNormsServiceImpl
 * @Description: 企业商品规格参数服务类--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/21
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "企业商品规格参数", groupName = "企业商品管理", tenant = TenantEnum.NO_ISOLATION)
public class EnterpriseProductNormsServiceImpl extends SkyeyeBusinessServiceImpl<EnterpriseProductNormsDao, EnterpriseProductNorms> implements EnterpriseProductNormsService {

    @Override
    public void saveEnterpriseProductNorms(String userId, EnterpriseProduct enterpriseProduct) {
        if (CollectionUtil.isEmpty(enterpriseProduct.getMaterialNorms())) {
            throw new CustomException("企业商品规格信息不能为空.");
        }

        // 保存规格信息以及初始化库存信息
        setNormsName(enterpriseProduct);
        saveNorms(userId, enterpriseProduct.getId(), enterpriseProduct.getMaterialNorms());

        // 刷新规格的缓存
        List<String> normsIds = enterpriseProduct.getMaterialNorms().stream().map(EnterpriseProductNorms::getId).collect(Collectors.toList());
        refreshCache(normsIds);
    }

    private void setNormsName(EnterpriseProduct enterpriseProduct) {
        Map<String, String> normsKeyToName = new HashMap<>();
        enterpriseProduct.getNormsSpec().forEach(normsSpec -> {
            String title = normsSpec.get("title").toString();
            List<Map<String, Object>> options = (List<Map<String, Object>>) normsSpec.get("options");
            Map<String, String> collect = options.stream().collect(Collectors.toMap(bean -> bean.get("rowNum").toString(),
                item -> String.format(Locale.ROOT, "%s：%s", title.trim(), item.get("title").toString())));
            normsKeyToName.putAll(collect);
        });
        enterpriseProduct.getMaterialNorms().forEach(materialNorms -> {
            if (enterpriseProduct.getUnit().equals(com.skyeye.material.classenum.MaterialUnit.SINGLE_SPECIFICATION.getKey())) {
                // 单规格
                materialNorms.setName(enterpriseProduct.getUnitName());
            } else {
                String[] tableNum = materialNorms.getTableNum().split(CommonCharConstants.HORIZONTAL_LINE_MARK);
                String materialNormsName = StrUtil.EMPTY;
                for (int ii = 0; ii < tableNum.length; ii++) {
                    if (ii != 0) {
                        materialNormsName += normsKeyToName.get(tableNum[ii]) + '；';
                    }
                }
                materialNorms.setName(materialNormsName);
            }
        });
    }

    private void saveNorms(String userId, String enterpriseProductId, List<EnterpriseProductNorms> enterpriseProductNormsList) {
        for (EnterpriseProductNorms materialNorms : enterpriseProductNormsList) {
            materialNorms.setEnterpriseProductId(enterpriseProductId);
        }
        List<EnterpriseProductNorms> oldEnterpriseProductNorms = queryNormsListByEnterpriseProductId(enterpriseProductId);
        List<String> oldKeys = oldEnterpriseProductNorms.stream().map(EnterpriseProductNorms::getTableNum).collect(Collectors.toList());
        List<String> newKeys = enterpriseProductNormsList.stream().map(EnterpriseProductNorms::getTableNum).collect(Collectors.toList());

        // (旧数据 - 新数据) 从数据库删除
        List<EnterpriseProductNorms> deleteBeans = oldEnterpriseProductNorms.stream()
            .filter(item -> !newKeys.contains(item.getTableNum())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(deleteBeans)) {
            List<String> ids = deleteBeans.stream().map(EnterpriseProductNorms::getId).collect(Collectors.toList());
            deleteById(ids);
        }

        // (新数据 - 旧数据) 新增到数据库
        List<EnterpriseProductNorms> addBeans = enterpriseProductNormsList.stream()
            .filter(item -> !oldKeys.contains(item.getTableNum())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(addBeans)) {
            createEntity(addBeans, userId);
        }

        // (新数据 ∩ 旧数据) 编辑数据库
        List<EnterpriseProductNorms> editBeans = enterpriseProductNormsList.stream()
            .filter(item -> oldKeys.contains(item.getTableNum())).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(editBeans)) {
            Map<String, EnterpriseProductNorms> collect = oldEnterpriseProductNorms.stream().collect(Collectors.toMap(EnterpriseProductNorms::getTableNum, item -> item));
            if (CollectionUtil.isNotEmpty(collect)) {
                editBeans.forEach(bean -> {
                    EnterpriseProductNorms enterpriseProductNorms = collect.get(bean.getTableNum());
                    bean.setId(enterpriseProductNorms.getId());
                });
                updateEntity(editBeans, userId);
            }
        }
    }

    @Override
    public void deleteEnterpriseProductNormsByEnterpriseProductId(String enterpriseProductId) {
        QueryWrapper<EnterpriseProductNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EnterpriseProductNorms::getEnterpriseProductId), enterpriseProductId);
        remove(queryWrapper);
    }

    @Override
    public List<EnterpriseProductNorms> queryNormsListByEnterpriseProductId(String enterpriseProductId) {
        QueryWrapper<EnterpriseProductNorms> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EnterpriseProductNorms::getEnterpriseProductId), enterpriseProductId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(EnterpriseProductNorms::getOrderBy));
        return list(queryWrapper);
    }

}
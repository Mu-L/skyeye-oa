/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.enterprise.dao.EnterpriseProductDao;
import com.skyeye.enterprise.entity.EnterpriseProduct;
import com.skyeye.enterprise.service.EnterpriseProductNormsService;
import com.skyeye.enterprise.service.EnterpriseProductService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: EnterpriseProductServiceImpl
 * @Description: 企业商品信息管理服务类--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/21
 * @Copyright: 2026 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "企业商品信息", groupName = "企业商品管理", tenant = TenantEnum.NO_ISOLATION)
public class EnterpriseProductServiceImpl extends SkyeyeBusinessServiceImpl<EnterpriseProductDao, EnterpriseProduct> implements EnterpriseProductService {

    @Autowired
    private EnterpriseProductNormsService enterpriseProductNormsService;

    @Override
    protected QueryWrapper<EnterpriseProduct> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EnterpriseProduct> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 企业ID过滤
        String enterpriseId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EnterpriseProduct::getEnterpriseId), enterpriseId);

        return queryWrapper;
    }

    @Override
    public void validatorEntity(EnterpriseProduct entity) {
        QueryWrapper<EnterpriseProduct> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(EnterpriseProduct::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(EnterpriseProduct::getProductCode), entity.getProductCode()));
        // 企业ID过滤
        String enterpriseId = InputObject.getLogParamsStatic().get("id").toString();
        entity.setEnterpriseId(enterpriseId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(EnterpriseProduct::getEnterpriseId), enterpriseId);
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        EnterpriseProduct checkProduct = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(checkProduct)) {
            throw new CustomException("同种商品名称/编码的商品已经存在.");
        }
    }

    @Override
    public void writePostpose(EnterpriseProduct entity, String userId) {
        super.writePostpose(entity, userId);
        // 保存企业商品规格信息
        enterpriseProductNormsService.saveEnterpriseProductNorms(userId, entity);
    }

    @Override
    public void deletePostpose(String id) {
        // 删除企业商品规格
        enterpriseProductNormsService.deleteEnterpriseProductNormsByEnterpriseProductId(id);
    }

    @Override
    public EnterpriseProduct getDataFromDb(String id) {
        EnterpriseProduct enterpriseProduct = super.getDataFromDb(id);
        if (enterpriseProduct != null) {
            // 设置规格信息
            enterpriseProduct.setMaterialNorms(enterpriseProductNormsService.queryNormsListByEnterpriseProductId(id));
        }
        return enterpriseProduct;
    }

    @Override
    public void queryAllEnterpriseProductList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        QueryWrapper<EnterpriseProduct> queryWrapper = super.getQueryWrapper(commonPageInfo);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<EnterpriseProduct> enterpriseProductList = list(queryWrapper);
        outputObject.setBeans(enterpriseProductList);
        outputObject.settotal(page.getTotal());
    }

}
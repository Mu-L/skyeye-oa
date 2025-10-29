/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.type.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.type.dao.MaterialTypeDao;
import com.skyeye.type.entity.MaterialType;
import com.skyeye.type.service.MaterialTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: MaterialTypeServiceImpl
 * @Description: 商城商品分类服务层--平台租户
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/29 9:37
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "商城商品分类", groupName = "商城商品分类", tenant = TenantEnum.PLATE)
public class MaterialTypeServiceImpl extends SkyeyeBusinessServiceImpl<MaterialTypeDao, MaterialType> implements MaterialTypeService {

    @Override
    public void queryEnabledMaterialTypeList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<MaterialType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(MaterialType::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(MaterialType::getOrderBy));
        List<MaterialType> accountSubjectList = list(queryWrapper);
        outputObject.setBeans(accountSubjectList);
        outputObject.settotal(accountSubjectList.size());
    }
}

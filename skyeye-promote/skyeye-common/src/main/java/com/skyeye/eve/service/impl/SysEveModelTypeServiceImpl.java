/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.SysEveModelTypeDao;
import com.skyeye.eve.entity.model.SysEveModelType;
import com.skyeye.eve.service.SysEveModelTypeService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: SysEveModelTypeServiceImpl
 * @Description: 系统模板分类业务实现层
 * @author: skyeye云系列
 * @date: 2021/11/13 10:15
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "素材分类管理", groupName = "素材管理", tenant = TenantEnum.PLATE)
public class SysEveModelTypeServiceImpl extends SkyeyeBusinessServiceImpl<SysEveModelTypeDao, SysEveModelType> implements SysEveModelTypeService {

    @Override
    @IgnoreTenant
    public void querySysEveModelTypeByParentId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> inputParams = inputObject.getParams();
        String parentId = inputParams.get("parentId").toString();
        QueryWrapper<SysEveModelType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModelType::getParentId), parentId);
        List<SysEveModelType> modelTypeList = list(queryWrapper);
        outputObject.setBeans(modelTypeList);
        outputObject.settotal(modelTypeList.size());
    }

    @Override
    public void deletePostpose(SysEveModelType entity) {
        super.deletePostpose(entity);
        QueryWrapper<SysEveModelType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SysEveModelType::getParentId), entity.getId());
        remove(queryWrapper);
    }

}

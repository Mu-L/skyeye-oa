/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.lifecycle.dao.LifecycleTemplateMasterDao;
import com.skyeye.lifecycle.entity.LifecycleTemplateMaster;
import com.skyeye.lifecycle.service.LifecycleTemplateMasterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: LifecycleTemplateMasterServiceImpl
 * @Description: 生命周期模板主表管理服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 11:07
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板主表管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateMasterServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateMasterDao, LifecycleTemplateMaster> implements LifecycleTemplateMasterService {

    @Override
    protected void validatorEntity(LifecycleTemplateMaster entity) {
        super.validatorEntity(entity);
        QueryWrapper<LifecycleTemplateMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateMaster::getAppId), entity.getAppId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateMaster::getClassName), entity.getClassName());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        LifecycleTemplateMaster lifecycleTemplateMaster = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(lifecycleTemplateMaster)) {
            throw new CustomException("该业务对象已经设置生命周期模板，请勿重复设置");
        }
    }

    @Override
    public void queryLifecycleTemplateMaster(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String appId = map.get("appId").toString();
        String className = map.get("className").toString();

        QueryWrapper<LifecycleTemplateMaster> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateMaster::getAppId), appId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateMaster::getClassName), className);
        LifecycleTemplateMaster lifecycleTemplateMaster = getOne(queryWrapper, false);
        outputObject.setBean(lifecycleTemplateMaster);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}

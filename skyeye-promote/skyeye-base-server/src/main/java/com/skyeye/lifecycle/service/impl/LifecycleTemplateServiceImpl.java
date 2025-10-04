/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.lifecycle.dao.LifecycleTemplateDao;
import com.skyeye.lifecycle.entity.LifecycleTemplate;
import com.skyeye.lifecycle.service.LifecycleTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: LifecycleTemplateServiceImpl
 * @Description: 生命周期模板管理业务层实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 11:27
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateDao, LifecycleTemplate> implements LifecycleTemplateService {

    @Override
    public QueryWrapper<LifecycleTemplate> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<LifecycleTemplate> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplate::getMasterId), commonPageInfo.getMasterId());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(LifecycleTemplate::getLargeVersion));
        return queryWrapper;
    }

    @Override
    public String createEntity(LifecycleTemplate entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.createEntity(entity, userId);
    }

    @Override
    public String updateEntity(LifecycleTemplate entity, String userId) {
        entity.setStartSmallVersion(false);
        return super.updateEntity(entity, userId);
    }

    @Override
    protected void writePostpose(LifecycleTemplate entity, String userId) {
        super.writePostpose(entity, userId);

    }

    @Override
    public LifecycleTemplate getDataFromDb(String id) {
        LifecycleTemplate lifecycleTemplate = super.getDataFromDb(id);
        return lifecycleTemplate;
    }

    @Override
    protected List<LifecycleTemplate> getDataFromDb(List<String> idList) {
        List<LifecycleTemplate> lifecycleTemplateList = super.getDataFromDb(idList);
        return lifecycleTemplateList;
    }

    @Override
    public LifecycleTemplate selectById(String id) {
        LifecycleTemplate lifecycleTemplate = super.selectById(id);
        return lifecycleTemplate;
    }

    @Override
    public List<LifecycleTemplate> selectByIds(String... ids) {
        List<LifecycleTemplate> lifecycleTemplates = super.selectByIds(ids);
        return lifecycleTemplates;
    }

    @Override
    protected void deletePostpose(LifecycleTemplate entity) {
        super.deletePostpose(entity);
    }

    @Override
    public void queryCurrentLifecycleTemplateByMasterId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String masterId = params.get("masterId").toString();
        // 查询当前生效的生命周期模板
        QueryWrapper<LifecycleTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplate::getWhetherLast), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplate::getWhetherPublish), WhetherEnum.ENABLE_USING.getKey());
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplate::getMasterId), masterId);
        LifecycleTemplate lifecycleTemplate = getOne(queryWrapper, false);
        if (lifecycleTemplate == null) {
            return;
        }
        lifecycleTemplate = selectById(lifecycleTemplate.getId());
        outputObject.setBean(lifecycleTemplate);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }
}

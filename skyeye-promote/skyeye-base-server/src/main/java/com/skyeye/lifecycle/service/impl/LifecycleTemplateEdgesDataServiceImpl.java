/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.lifecycle.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.lifecycle.dao.LifecycleTemplateEdgesDataDao;
import com.skyeye.lifecycle.entity.LifecycleTemplateEdgesData;
import com.skyeye.lifecycle.service.LifecycleTemplateEdgesDataService;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @ClassName: LifecycleTemplateEdgesDataServiceImpl
 * @Description: 生命周期模板边数据服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/10/4 14:55
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "生命周期模板连线数据管理", groupName = "生命周期管理", tenant = TenantEnum.PLATE)
public class LifecycleTemplateEdgesDataServiceImpl extends SkyeyeBusinessServiceImpl<LifecycleTemplateEdgesDataDao, LifecycleTemplateEdgesData> implements LifecycleTemplateEdgesDataService {

    @Override
    public void saveList(String templateId, List<LifecycleTemplateEdgesData> beans) {
        deleteByTemplateId(templateId);
        if (CollectionUtil.isNotEmpty(beans)) {
            for (LifecycleTemplateEdgesData LifecycleTemplateEdgesData : beans) {
                LifecycleTemplateEdgesData.setTemplateId(templateId);
            }
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            createEntity(beans, userId);
        }
    }

    @Override
    public void deleteByTemplateId(String templateId) {
        QueryWrapper<LifecycleTemplateEdgesData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateEdgesData::getTemplateId), templateId);
        remove(queryWrapper);
    }

    @Override
    public List<LifecycleTemplateEdgesData> selectByTemplateId(String templateId) {
        QueryWrapper<LifecycleTemplateEdgesData> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LifecycleTemplateEdgesData::getTemplateId), templateId);
        List<LifecycleTemplateEdgesData> list = list(queryWrapper);
        return list;
    }

    @Override
    public Map<String, LifecycleTemplateEdgesData> selectMapByTemplateId(String templateId) {
        List<LifecycleTemplateEdgesData> lifecycleTemplateEdgesDataList = selectByTemplateId(templateId);
        return lifecycleTemplateEdgesDataList.stream()
            .collect(Collectors.toMap(LifecycleTemplateEdgesData::getEdgesId, Function.identity(), (v1, v2) -> v1));

    }

    @Override
    public Map<String, Map<String, LifecycleTemplateEdgesData>> selectMapByTemplateId(List<String> templateId) {
        if (CollectionUtil.isEmpty(templateId)) {
            return Collections.emptyMap();
        }
        QueryWrapper<LifecycleTemplateEdgesData> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(LifecycleTemplateEdgesData::getTemplateId), templateId);
        List<LifecycleTemplateEdgesData> list = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(list)) {
            return list.stream()
                .collect(Collectors.groupingBy(LifecycleTemplateEdgesData::getTemplateId,
                    Collectors.toMap(LifecycleTemplateEdgesData::getEdgesId, Function.identity(), (v1, v2) -> v1)));
        }
        return Collections.emptyMap();
    }
}

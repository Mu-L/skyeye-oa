/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.gw.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.gw.dao.GwTemplatesDao;
import com.skyeye.eve.gw.entity.GwTemplates;
import com.skyeye.eve.gw.service.GwTemplatesService;
import com.skyeye.eve.seal.service.SealService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: GwTemplatesServiceImpl
 * @Description: 套红模板服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/25 11:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "套红模板", groupName = "套红模板")
public class GwTemplatesServiceImpl extends SkyeyeBusinessServiceImpl<GwTemplatesDao, GwTemplates> implements GwTemplatesService {

    @Autowired
    private SealService sealService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        sealService.setMationForMap(beans, "sealId", "sealMation");
        return beans;
    }

    @Override
    public GwTemplates selectById(String id) {
        GwTemplates gwTemplates = super.selectById(id);
        sealService.setDataMation(gwTemplates, GwTemplates::getSealId);
        return gwTemplates;
    }

    @Override
    public List<GwTemplates> selectByIds(String... ids) {
        List<GwTemplates> gwTemplates = super.selectByIds(ids);
        sealService.setDataMation(gwTemplates, GwTemplates::getSealId);
        return gwTemplates;
    }

    @Override
    public void queryEnabledGwTemplatesList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<GwTemplates> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GwTemplates::getEnabled), EnableEnum.ENABLE_USING.getKey());
        List<GwTemplates> gwTemplatesList = list(queryWrapper);
        outputObject.setBeans(gwTemplatesList);
        outputObject.settotal(gwTemplatesList.size());
    }

}

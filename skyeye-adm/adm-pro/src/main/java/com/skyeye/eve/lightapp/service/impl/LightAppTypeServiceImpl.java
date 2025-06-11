/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.lightapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.lightapp.dao.LightAppTypeDao;
import com.skyeye.eve.lightapp.entity.LightAppType;
import com.skyeye.eve.lightapp.service.LightAppTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: LightAppTypeServiceImpl
 * @Description: 轻应用类型管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "轻应用类型管理", groupName = "轻应用管理")
public class LightAppTypeServiceImpl extends SkyeyeBusinessServiceImpl<LightAppTypeDao, LightAppType> implements LightAppTypeService {

    /**
     * 获取启用的轻应用类型列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryLightAppTypeUpList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<LightAppType> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(LightAppType::getEnabled), EnableEnum.ENABLE_USING.getKey());
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(LightAppType::getOrderBy));
        List<LightAppType> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.patrol.dao.PatrolPointDao;
import com.skyeye.patrol.entity.PatrolPoint;
import com.skyeye.patrol.service.PatrolPointService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: PatrolPointServiceImpl
 * @Description: 巡检点位服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检点位", groupName = "巡检点位")
public class PatrolPointServiceImpl extends SkyeyeBusinessServiceImpl<PatrolPointDao, PatrolPoint> implements PatrolPointService {

    @Override
    protected void validatorEntity(PatrolPoint entity) {
        QueryWrapper<PatrolPoint> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPoint::getPointCode), entity.getPointCode());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        PatrolPoint checkPatrolPoint = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkPatrolPoint)) {
            throw new CustomException("点位编码已存在.");
        }
    }

    @Override
    protected QueryWrapper<PatrolPoint> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PatrolPoint> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (commonPageInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPoint::getEnabled), commonPageInfo.getEnabled());
        }
        return queryWrapper;
    }

    @Override
    public void queryAllPatrolPointList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String enabled = params.get("enabled").toString();
        QueryWrapper<PatrolPoint> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotBlank(enabled)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolPoint::getEnabled), enabled);
        }
        List<PatrolPoint> patrolPointList = list(queryWrapper);
        outputObject.setBeans(patrolPointList);
        outputObject.settotal(patrolPointList.size());
    }
}


/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.TableSelectInfo;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.patrol.dao.PatrolTeamDao;
import com.skyeye.patrol.entity.PatrolTeam;
import com.skyeye.patrol.service.PatrolTeamMemberService;
import com.skyeye.patrol.service.PatrolTeamService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: PatrolTeamServiceImpl
 * @Description: 巡检班组服务层
 * @author: skyeye云系列--卫志强
 * @date: 2025/01/XX XX:XX
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检班组", groupName = "巡检班组")
public class PatrolTeamServiceImpl extends SkyeyeBusinessServiceImpl<PatrolTeamDao, PatrolTeam> implements PatrolTeamService {

    @Autowired
    private PatrolTeamMemberService patrolTeamMemberService;

    @Override
    protected void validatorEntity(PatrolTeam entity) {
        QueryWrapper<PatrolTeam> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.eq(MybatisPlusUtil.toColumns(PatrolTeam::getName), entity.getName())
                .or().eq(MybatisPlusUtil.toColumns(PatrolTeam::getTeamCode), entity.getTeamCode()));
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        PatrolTeam checkWorkProcedure = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkWorkProcedure)) {
            throw new CustomException("名称/编码已存在.");
        }
    }

    @Override
    protected QueryWrapper<PatrolTeam> getQueryWrapper(TableSelectInfo tableSelectInfo) {
        QueryWrapper<PatrolTeam> queryWrapper = super.getQueryWrapper(tableSelectInfo);
        if (tableSelectInfo.getEnabled() != null) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTeam::getEnabled), tableSelectInfo.getEnabled());
        }
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(PatrolTeam::getOrderBy));
        return queryWrapper;
    }

    @Override
    protected void deletePostpose(PatrolTeam entity) {
        // 删除班组下得人员
        patrolTeamMemberService.deleteMemberListByTeamId(entity.getId());
    }
}


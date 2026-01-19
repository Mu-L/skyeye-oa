/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.patrol.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.patrol.dao.PatrolTeamMemberDao;
import com.skyeye.patrol.entity.PatrolTeamMember;
import com.skyeye.patrol.service.PatrolTeamMemberService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PatrolTeamMemberServiceImpl
 * @Description: 巡检班组人员服务层
 * @author: skyeye云系列--卫志强
 * @date: 2026/01/19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "巡检班组人员", groupName = "巡检班组人员")
public class PatrolTeamMemberServiceImpl extends SkyeyeBusinessServiceImpl<PatrolTeamMemberDao, PatrolTeamMember> implements PatrolTeamMemberService {

    @Override
    protected QueryWrapper<PatrolTeamMember> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<PatrolTeamMember> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTeamMember::getTeamId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        // 设置员工信息
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString())
            .filter(staffId -> StrUtil.isNotEmpty(staffId)).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String staffId = bean.get("staffId").toString();
            bean.put("staffMation", staffMap.get(staffId));
        });
        return beans;
    }

    @Override
    public void deleteMemberListByTeamId(String teamId) {
        if (StrUtil.isEmpty(teamId)) {
            return;
        }
        QueryWrapper<PatrolTeamMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PatrolTeamMember::getTeamId), teamId);
        remove(queryWrapper);
    }
}


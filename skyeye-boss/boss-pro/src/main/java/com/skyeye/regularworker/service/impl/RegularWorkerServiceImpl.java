/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.regularworker.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeFlowableServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.enumeration.UserStaffState;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.BossConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.ICompanyJobService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.quit.entity.Quit;
import com.skyeye.regularworker.dao.RegularWorkerDao;
import com.skyeye.regularworker.entity.RegularWorker;
import com.skyeye.regularworker.service.RegularWorkerService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: RegularWorkerServiceImpl
 * @Description: 转正申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-24 15:16:26
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "转正申请", groupName = "转正申请", flowable = true)
public class RegularWorkerServiceImpl extends SkyeyeFlowableServiceImpl<RegularWorkerDao, RegularWorker> implements RegularWorkerService {

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobService iCompanyJobService;

    @Override
    public List<Map<String, Object>> queryPageData(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryBossRegularWorkerList(pageInfo);
        iDepmentService.setMationForMap(beans, "departmentId", "departmentMation");
        iCompanyJobService.setMationForMap(beans, "jobId", "jobMation");
        return beans;
    }

    @Override
    public void validatorEntity(RegularWorker entity) {
        Map<String, Object> user = InputObject.getLogParamsStatic();
        String userId = user.get("id").toString();
        boolean canApply = isCanApply(userId, entity.getId());
        if (!canApply) {
            throw new CustomException("您已提交过转正申请，请等待审批。");
        }
        // 获取用户的信息
        String staffId = user.get("staffId").toString();
        Map<String, Map<String, Object>> staffMap = iAuthUserService
            .queryUserMationListByStaffIds(Arrays.asList(staffId));
        Map<String, Object> staff = staffMap.get(staffId);
        entity.setDepartmentId(staff.get("departmentId").toString());
        entity.setJobId(staff.get("jobId").toString());
    }

    @Override
    public RegularWorker selectById(String id) {
        RegularWorker regularWorker = super.selectById(id);
        iAuthUserService.setName(regularWorker, "createId", "createName");
        // 部门信息
        Map<String, Object> department = iDepmentService.queryDataMationById(regularWorker.getDepartmentId());
        regularWorker.setDepartmentMation(department);
        // 岗位信息
        regularWorker.setJobMation(iCompanyJobService.queryDataMationById(regularWorker.getJobId()));
        return regularWorker;
    }

    @Override
    protected void approvalEndIsSuccess(RegularWorker entity) {
        skyeyeBaseMapper.updateUserStaffState(entity.getCreateId(), UserStaffState.ON_THE_JOB.getKey(), entity.getRegularTime());
        // 删除该员工对应的缓存信息
        BossConstants.deleteCache(entity.getCreateId());
    }

    /**
     * 是否可以提交转正申请。true:可以；false:不可以
     *
     * @param userId 用户id
     * @return true:可以；false:不可以
     */
    private boolean isCanApply(String userId, String id) {
        Map<String, Object> user = iAuthUserService.queryDataMationById(userId);
        Integer state = Integer.parseInt(user.get("state").toString());
        // 是否可以提交转正申请。true:可以；false:不可以
        boolean canApply = false;
        if (state == UserStaffState.PROBATION_PERIOD.getKey()) {
            // 试用期，获取该用户是否有已经添加的转正申请(不是作废状态的)
            QueryWrapper<RegularWorker> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Quit::getCreateId), userId);
            queryWrapper.ne(MybatisPlusUtil.toColumns(Quit::getState), FlowableStateEnum.INVALID.getKey());
            if (StringUtils.isNotEmpty(id)) {
                queryWrapper.ne(CommonConstants.ID, id);
            }
            List<RegularWorker> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                // 为空，说明还没有提交转正申请
                canApply = true;
            }
        } else {
            throw new CustomException("您不是试用期员工，无法提交转正申请。");
        }
        return canApply;
    }
}

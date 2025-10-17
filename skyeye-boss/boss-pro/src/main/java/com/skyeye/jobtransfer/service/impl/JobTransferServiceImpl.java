/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.jobtransfer.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.FlowableStateEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constants.BossConstants;
import com.skyeye.exception.CustomException;
import com.skyeye.jobtransfer.dao.JobTransferDao;
import com.skyeye.jobtransfer.entity.JobTransfer;
import com.skyeye.jobtransfer.service.JobTransferService;
import com.skyeye.organization.service.ICompanyJobScoreService;
import com.skyeye.organization.service.ICompanyJobService;
import com.skyeye.organization.service.ICompanyService;
import com.skyeye.organization.service.IDepmentService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: JobTransferServiceImpl
 * @Description: 岗位调动申请服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022-04-27 15:57:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "岗位调动申请", groupName = "岗位调动申请", flowable = true)
public class JobTransferServiceImpl extends SkyeyeBusinessServiceImpl<JobTransferDao, JobTransfer> implements JobTransferService {

    @Autowired
    private ICompanyService iCompanyService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobService iCompanyJobService;

    @Autowired
    private ICompanyJobScoreService iCompanyJobScoreService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryJobTransferList(pageInfo);
        List<String> staffIds = beans.stream().map(bean -> bean.get("transferStaffId").toString()).collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String transferStaffId = bean.get("transferStaffId").toString();
            bean.put("transferStaffMation", staffMap.get(transferStaffId));
        });
        iCompanyService.setMationForMap(beans, "primaryCompanyId", "primaryCompanyMation");
        iCompanyService.setMationForMap(beans, "currentCompanyId", "currentCompanyMation");

        iDepmentService.setMationForMap(beans, "primaryDepartmentId", "primaryDepartmentMation");
        iDepmentService.setMationForMap(beans, "currentDepartmentId", "currentDepartmentMation");

        iCompanyJobService.setMationForMap(beans, "primaryJobId", "primaryJobMation");
        iCompanyJobService.setMationForMap(beans, "currentJobId", "currentJobMation");

        iCompanyJobScoreService.setMationForMap(beans, "primaryJobScoreId", "primaryJobScoreMation");
        iCompanyJobScoreService.setMationForMap(beans, "currentJobScoreId", "currentJobScoreMation");
        return beans;
    }


    @Override
    public void validatorEntity(JobTransfer entity) {
        // 校验基础信息
        QueryWrapper<JobTransfer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(JobTransfer::getTransferStaffId), entity.getTransferStaffId());
        List<String> states = Arrays.asList(FlowableStateEnum.DRAFT.getKey(),
            FlowableStateEnum.IN_EXAMINE.getKey(), FlowableStateEnum.REJECT.getKey(), FlowableStateEnum.REVOKE.getKey());
        queryWrapper.in(MybatisPlusUtil.toColumns(JobTransfer::getState), states);
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        JobTransfer checkJobTeansfer = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkJobTeansfer)) {
            throw new CustomException("该员工已存在调动申请.");
        }
        // 获取用户的信息
        Map<String, Map<String, Object>> staffMap = iAuthUserService
            .queryUserMationListByStaffIds(Arrays.asList(entity.getTransferStaffId()));
        Map<String, Object> staff = staffMap.get(entity.getTransferStaffId());
        entity.setPrimaryCompanyId(staff.get("companyId").toString());
        entity.setPrimaryDepartmentId(staff.get("departmentId").toString());
        entity.setPrimaryJobId(staff.get("jobId").toString());
        entity.setPrimaryJobScoreId(staff.get("jobScoreId").toString());
    }

    @Override
    public JobTransfer selectById(String id) {
        JobTransfer jobTransfer = super.selectById(id);
        // 获取申请人信息
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(Arrays.asList(jobTransfer.getTransferStaffId()));
        Map<String, Object> staff = staffMap.get(jobTransfer.getTransferStaffId());
        staff.put("id", staff.get("staffId"));
        jobTransfer.setTransferStaffMation(staff);

        // 原岗位信息
        jobTransfer.setPrimaryCompanyMation(iCompanyService.queryDataMationById(jobTransfer.getPrimaryCompanyId()));
        jobTransfer.setPrimaryDepartmentMation(iDepmentService.queryDataMationById(jobTransfer.getPrimaryDepartmentId()));
        jobTransfer.setPrimaryJobMation(iCompanyJobService.queryDataMationById(jobTransfer.getPrimaryJobId()));
        if (StrUtil.isNotEmpty(jobTransfer.getPrimaryJobScoreId())) {
            jobTransfer.setPrimaryJobScoreMation(iCompanyJobScoreService.queryDataMationById(jobTransfer.getPrimaryJobScoreId()));
        }

        // 现岗位信息
        jobTransfer.setCurrentCompanyMation(iCompanyService.queryDataMationById(jobTransfer.getCurrentCompanyId()));
        jobTransfer.setCurrentDepartmentMation(iDepmentService.queryDataMationById(jobTransfer.getCurrentDepartmentId()));
        jobTransfer.setCurrentJobMation(iCompanyJobService.queryDataMationById(jobTransfer.getCurrentJobId()));
        if (StrUtil.isNotEmpty(jobTransfer.getCurrentJobScoreId())) {
            jobTransfer.setCurrentJobScoreMation(iCompanyJobScoreService.queryDataMationById(jobTransfer.getCurrentJobScoreId()));
        }

        jobTransfer.setStateName(FlowableStateEnum.getStateName(jobTransfer.getState()));
        iAuthUserService.setName(jobTransfer, "createId", "createName");
        return jobTransfer;
    }

    @Override
    protected void approvalEndIsSuccess(JobTransfer entity) {
        // 修改员工岗位信息
        skyeyeBaseMapper.updateBossInterviewJobMation(entity);
        // 获取申请人信息
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(Arrays.asList(entity.getTransferStaffId()));
        String userId = staffMap.get(entity.getTransferStaffId()).get("id").toString();
        if (!ToolUtil.isBlank(userId)) {
            // 删除该员工对应的缓存信息
            BossConstants.deleteCache(userId);
        }
    }

}

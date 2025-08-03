/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.statistics.service.impl;

import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.interviewee.classenum.IntervieweeStatusEnum;
import com.skyeye.organization.service.ICompanyJobService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.personrequire.classenum.PersonRequireStateEnum;
import com.skyeye.statistics.dao.StatisticsDao;
import com.skyeye.statistics.service.StatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: StatisticsServiceImpl
 * @Description: BOSS统计模块实现类--不隔离，通过代码实现隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/3 15:18
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "BOSS统计模块", groupName = "BOSS统计模块", tenant = TenantEnum.NO_ISOLATION)
public class StatisticsServiceImpl implements StatisticsService {

    @Autowired
    private StatisticsDao statisticsDao;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobService iCompanyJobService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    private void setTenantParams(Map<String, Object> params) {
        if (tenantEnable) {
            params.put("tenantId", TenantContext.getTenantId());
        }
    }

    @Override
    public void getRecruitmentOverview(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        setTenantParams(params);
        Map<String, Object> result = statisticsDao.getRecruitmentOverview(params);
        outputObject.setBean(result);
        outputObject.settotal(1);
    }

    @Override
    public void getIntervieweeStatusDistribution(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        setTenantParams(params);
        List<Map<String, Object>> result = statisticsDao.getIntervieweeStatusDistribution(params);
        result.forEach(item -> {
            item.put("stateName", IntervieweeStatusEnum.getIntervieweeStateName(Integer.parseInt(item.get("state").toString())));
        });
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void getPersonRequireCompletion(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        setTenantParams(params);
        List<Map<String, Object>> result = statisticsDao.getPersonRequireCompletion(params);
        result.forEach(item -> {
            item.put("stateName", PersonRequireStateEnum.getStateName(item.get("state").toString()));
        });
        // 获取部门信息
        iDepmentService.setMationForMap(result, "recruitDepartmentId", "recruitDepartmentMation");
        // 获取招聘岗位
        iCompanyJobService.setMationForMap(result, "recruitJobId", "recruitJobMation");
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void getRecruitmentChannelEffect(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        setTenantParams(params);
        List<Map<String, Object>> result = statisticsDao.getRecruitmentChannelEffect(params);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void getDepartmentRecruitmentStats(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        setTenantParams(params);
        List<Map<String, Object>> result = statisticsDao.getDepartmentRecruitmentStats(params);
        // 获取部门信息
        iDepmentService.setMationForMap(result, "recruitDepartmentId", "recruitDepartmentMation");
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void getMonthlyRecruitmentTrend(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        setTenantParams(params);
        List<Map<String, Object>> result = statisticsDao.getMonthlyRecruitmentTrend(params);
        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void getRegularAndQuitStats(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        setTenantParams(params);
        Map<String, Object> result = statisticsDao.getRegularAndQuitStats(params);
        outputObject.setBean(result);
        outputObject.settotal(1);
    }

}

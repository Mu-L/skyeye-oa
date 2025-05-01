/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.CompanyDepartmentService;
import com.skyeye.organization.service.CompanyJobScoreService;
import com.skyeye.organization.service.CompanyJobService;
import com.skyeye.organization.service.CompanyMationService;
import com.skyeye.personnel.classenum.StaffWagesStateEnum;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.service.SysEveUserStaffTimeService;
import com.skyeye.tenant.dao.TenantUserDao;
import com.skyeye.tenant.entity.Tenant;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.service.TenantService;
import com.skyeye.tenant.service.TenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TenantUserServiceImpl
 * @Description: 租户下的用户服务实现类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/26 22:47
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "租户下的用户管理", groupName = "租户下的用户管理")
public class TenantUserServiceImpl extends SkyeyeBusinessServiceImpl<TenantUserDao, TenantUser> implements TenantUserService {

    @Autowired
    private CompanyMationService companyMationService;

    @Autowired
    private CompanyDepartmentService companyDepartmentService;

    @Autowired
    private CompanyJobService companyJobService;

    @Autowired
    private CompanyJobScoreService companyJobScoreService;

    @Autowired
    private TenantService tenantService;

    @Autowired
    private SysEveUserStaffTimeService sysEveUserStaffTimeService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    protected void validatorEntity(TenantUser entity) {
        if (!tenantEnable) {
            throw new CustomException("租户模式未开启.");
        }
    }

    @Override
    protected void createPrepose(TenantUser entity) {
        super.createPrepose(entity);
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        String jobNumberKey = MybatisPlusUtil.toColumns(TenantUser::getJobNumber);
        queryWrapper.select("max(0 + RIGHT(" + jobNumberKey + ", 6)) AS " + jobNumberKey);
        TenantUser tenantUser = getOne(queryWrapper, false);
        entity.setJobNumber(CalculationUtil.add(tenantUser.getJobNumber(), CommonNumConstants.NUM_ONE.toString(), CommonNumConstants.NUM_ZERO));
        entity.setActWages(CommonNumConstants.NUM_ZERO.toString());
        entity.setAnnualLeave(CommonNumConstants.NUM_ZERO.toString());
        entity.setHolidayNumber(CommonNumConstants.NUM_ZERO.toString());
        entity.setRetiredHolidayNumber(CommonNumConstants.NUM_ZERO.toString());
        entity.setDesignWages(StaffWagesStateEnum.WAIT_DESIGN_WAGES.getKey());
    }

    @Override
    public void updatePrepose(TenantUser entity) {
        TenantUser oldData = selectById(entity.getId());
        entity.setTenantId(oldData.getTenantId());
        entity.setStaffId(oldData.getStaffId());
        entity.setType(oldData.getType());
        entity.setDesignWages(oldData.getDesignWages());
        entity.setActWages(oldData.getActWages());
        entity.setAnnualLeave(oldData.getAnnualLeave());
        entity.setAnnualLeaveStatisTime(oldData.getAnnualLeaveStatisTime());
        entity.setHolidayNumber(oldData.getHolidayNumber());
        entity.setHolidayStatisTime(oldData.getHolidayStatisTime());
        entity.setRetiredHolidayNumber(oldData.getRetiredHolidayNumber());
        entity.setRetiredHolidayStatisTime(oldData.getRetiredHolidayStatisTime());
        entity.setInterviewArrangementId(oldData.getInterviewArrangementId());
    }

    @Override
    protected void updatePostpose(TenantUser entity, String userId) {
        // 单租户模式才去保存员工考勤时间段信息，多租户模式在其他地方保存
        sysEveUserStaffTimeService.saveUserStaffCheckWorkTime(entity.getTimeIdList(), entity.getId());
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<String> staffIds = beans.stream().map(bean -> bean.get("staffId").toString()).collect(Collectors.toList());
        Map<String, Map<String, Object>> staffMap = iAuthUserService.queryUserMationListByStaffIds(staffIds);
        beans.forEach(bean -> {
            String transferStaffId = bean.get("staffId").toString();
            bean.put("staffMation", staffMap.get(transferStaffId));
        });

        // 设置组织信息
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        companyDepartmentService.setNameMationForMap(beans, "departmentId", "departmentName", StrUtil.EMPTY);
        companyJobService.setNameMationForMap(beans, "jobId", "jobName", StrUtil.EMPTY);
        companyJobScoreService.setNameMationForMap(beans, "jobScoreId", "jobScoreName", StrUtil.EMPTY);
        return beans;
    }

    @Override
    public TenantUser selectById(String id) {
        TenantUser tenantUser = super.selectById(id);

        // 获取用户的信息
        Map<String, Map<String, Object>> staffMap = iAuthUserService
            .queryUserMationListByStaffIds(Arrays.asList(tenantUser.getStaffId()));
        tenantUser.setStaffMation(staffMap.get(tenantUser.getStaffId()));

        // 设置组织信息
        companyMationService.setDataMation(tenantUser, TenantUser::getCompanyId);
        companyDepartmentService.setDataMation(tenantUser, TenantUser::getDepartmentId);
        companyJobService.setDataMation(tenantUser, TenantUser::getJobId);
        companyJobScoreService.setDataMation(tenantUser, TenantUser::getJobScoreId);
        return tenantUser;
    }

    @Override
    public void removeTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = inputObject.getParams().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        remove(queryWrapper);
    }

    @Override
    public void exitTenantUser(InputObject inputObject, OutputObject outputObject) {
        String staffId = InputObject.getLogParamsStatic().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        remove(queryWrapper);
    }

    @Override
    public void queryTenantUserByStaffId(InputObject inputObject, OutputObject outputObject) {
        String staffId = InputObject.getLogParamsStatic().get("staffId").toString();
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        List<TenantUser> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return;
        }
        List<String> tenantIds = list.stream().map(bean -> bean.getTenantId()).distinct().collect(Collectors.toList());
        List<Tenant> tenantList = tenantService.selectByIds(tenantIds.toArray(new String[tenantIds.size()]));
        outputObject.setBeans(tenantList);
        outputObject.settotal(tenantList.size());
    }

    @Override
    public void editUserStaffActMoneyByStaffId(String staffId, String actMoney) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getDesignWages), StaffWagesStateEnum.TOO_DESIGN_WAGES.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getActWages), actMoney);
        update(updateWrapper);
    }

    @Override
    public void editUserStaffAnnualLeaveByStaffId(String staffId, String quarterYearHour, String annualLeaveStatisTime) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getAnnualLeave), quarterYearHour);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getAnnualLeaveStatisTime), annualLeaveStatisTime);
        update(updateWrapper);
    }

    @Override
    public void editUserStaffHolidayByStaffId(String staffId, String holidayNumber, String holidayStatisTime) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getHolidayNumber), holidayNumber);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getHolidayStatisTime), holidayStatisTime);
        update(updateWrapper);
    }

    @Override
    public void editUserStaffRetiredHolidayByStaffId(String staffId, String retiredHolidayNumber, String retiredHolidayStatisTime) {
        // 开启多租户模式时，默认加上租户id
        UpdateWrapper<TenantUser> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getRetiredHolidayNumber), retiredHolidayNumber);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUser::getRetiredHolidayStatisTime), retiredHolidayStatisTime);
        update(updateWrapper);
    }

    @Override
    public SysEveUserStaff setThisTenantUserToDefault(SysEveUserStaff sysEveUserStaff) {
        if (ObjectUtil.isEmpty(sysEveUserStaff)) {
            return null;
        }
        // 默认查询当前租户下的
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TenantUser::getStaffId), sysEveUserStaff.getId());
        TenantUser tenantUser = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(tenantUser)) {
            return null;
        }
        // 设置默认值
        setTenantUserMation(sysEveUserStaff, tenantUser);
        return sysEveUserStaff;
    }

    private void setTenantUserMation(SysEveUserStaff sysEveUserStaff, TenantUser tenantUser) {
        sysEveUserStaff.setState(tenantUser.getState());
        sysEveUserStaff.setCompanyId(tenantUser.getCompanyId());
        sysEveUserStaff.setDepartmentId(tenantUser.getDepartmentId());
        sysEveUserStaff.setJobId(tenantUser.getJobId());
        sysEveUserStaff.setJobScoreId(tenantUser.getJobScoreId());
        sysEveUserStaff.setWorkTime(tenantUser.getWorkTime());
        sysEveUserStaff.setEntryTime(tenantUser.getEntryTime());
        sysEveUserStaff.setQuitTime(tenantUser.getQuitTime());
        sysEveUserStaff.setQuitReason(tenantUser.getQuitReason());
        sysEveUserStaff.setInterviewArrangementId(tenantUser.getInterviewArrangementId());
        sysEveUserStaff.setHolidayNumber(tenantUser.getHolidayNumber());
        sysEveUserStaff.setHolidayStatisTime(tenantUser.getHolidayStatisTime());
        sysEveUserStaff.setRetiredHolidayNumber(tenantUser.getRetiredHolidayNumber());
        sysEveUserStaff.setRetiredHolidayStatisTime(tenantUser.getRetiredHolidayStatisTime());
        sysEveUserStaff.setAnnualLeave(tenantUser.getAnnualLeave());
        sysEveUserStaff.setAnnualLeaveStatisTime(tenantUser.getAnnualLeaveStatisTime());
        sysEveUserStaff.setDesignWages(tenantUser.getDesignWages());
        sysEveUserStaff.setActWages(tenantUser.getActWages());
        sysEveUserStaff.setJobNumber(tenantUser.getJobNumber());
    }

    @Override
    public List<SysEveUserStaff> setThisTenantUserToDefault(List<SysEveUserStaff> userStaffList) {
        if (CollectionUtil.isEmpty(userStaffList)) {
            return CollectionUtil.newArrayList();
        }
        List<String> staffIds = userStaffList.stream().map(SysEveUserStaff::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(staffIds)) {
            return CollectionUtil.newArrayList();
        }
        // 开启多租户模式时，默认加上租户id
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(TenantUser::getStaffId), staffIds);
        List<TenantUser> tenantUserList = list(queryWrapper);
        if (CollectionUtil.isEmpty(tenantUserList)) {
            return CollectionUtil.newArrayList();
        }
        // 过滤出当前租户下的用户
        List<String> tenantStaffIdList = tenantUserList.stream().map(TenantUser::getStaffId).distinct().collect(Collectors.toList());
        userStaffList = userStaffList.stream().filter(userStaff -> tenantStaffIdList.contains(userStaff.getId())).collect(Collectors.toList());
        Map<String, TenantUser> collect = tenantUserList.stream().collect(Collectors.toMap(TenantUser::getStaffId, tenantUser -> tenantUser, (k, v) -> v));
        userStaffList.forEach(userStaff -> {
            TenantUser tenantUser = collect.get(userStaff.getId());
            // 默认查询当前租户下的
            setTenantUserMation(userStaff, tenantUser);
        });
        return userStaffList;
    }

}

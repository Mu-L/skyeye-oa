/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.tenant.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.IsUsedEnum;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import com.skyeye.organization.service.CompanyDepartmentService;
import com.skyeye.organization.service.CompanyJobScoreService;
import com.skyeye.organization.service.CompanyJobService;
import com.skyeye.organization.service.CompanyMationService;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.service.SysEveUserStaffService;
import com.skyeye.tenant.classenum.TenantUserJoinType;
import com.skyeye.tenant.dao.TenantUserInviteDao;
import com.skyeye.tenant.entity.TenantUser;
import com.skyeye.tenant.entity.TenantUserInvite;
import com.skyeye.tenant.service.TenantService;
import com.skyeye.tenant.service.TenantUserInviteService;
import com.skyeye.tenant.service.TenantUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: TenantUserInviteServiceImpl
 * @Description: 租户下的用户邀请信息管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/27 8:30
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "租户与用户邀请关系管理", groupName = "租户管理")
public class TenantUserInviteServiceImpl extends SkyeyeBusinessServiceImpl<TenantUserInviteDao, TenantUserInvite> implements TenantUserInviteService {

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private TenantUserService tenantUserService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Value("${skyeye.tenantInvite.url}")
    private String tenantInviteUrl;

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

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);

        // 设置组织信息
        companyMationService.setNameMationForMap(beans, "companyId", "companyName", StrUtil.EMPTY);
        companyDepartmentService.setNameMationForMap(beans, "departmentId", "departmentName", StrUtil.EMPTY);
        companyJobService.setNameMationForMap(beans, "jobId", "jobName", StrUtil.EMPTY);
        companyJobScoreService.setNameMationForMap(beans, "jobScoreId", "jobScoreName", StrUtil.EMPTY);
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void inviteUsersToJoin(InputObject inputObject, OutputObject outputObject) {
        if (!tenantEnable) {
            throw new CustomException("租户功能未开启");
        }
        String tenantId = TenantContext.getTenantId();
        // 校验租户账号数量
        tenantService.checkTenantAccountNum(tenantId);

        TenantUserInvite tenantUserInvite = inputObject.getParams(TenantUserInvite.class);
        // 校验手机号
        String userStaffId = sysEveUserStaffService.queryUserStaffByPhone(tenantUserInvite.getPhone());
        String userId = inputObject.getLogParams().get("id").toString();
        if (StrUtil.isNotEmpty(userStaffId)) {
            // 手机号已存在
            // 判断该员工是否已经加入该租户
            TenantUser checkTenantUser = tenantUserService.queryTenantUserByStaffId(userStaffId, tenantId);
            if (checkTenantUser != null) {
                throw new CustomException("该员工已加入该租户");
            }
            tenantUserInvite.setIsUsed(IsUsedEnum.IN_USE.getKey());
            tenantUserInvite.setJoinType(TenantUserJoinType.AUTO.getKey());
            String id = createEntity(tenantUserInvite, userId);
            // 加入租户
            TenantUser tenantUser = new TenantUser();
            tenantUser.setStaffId(userStaffId);
            tenantUser.setCompanyId(tenantUserInvite.getCompanyId());
            tenantUser.setDepartmentId(tenantUserInvite.getDepartmentId());
            tenantUser.setJobId(tenantUserInvite.getJobId());
            tenantUser.setJobScoreId(tenantUserInvite.getJobScoreId());
            tenantUser.setState(tenantUserInvite.getState());
            tenantUser.setWorkTime(tenantUserInvite.getWorkTime());
            tenantUser.setEntryTime(tenantUserInvite.getEntryTime());
            tenantUser.setTrialTime(tenantUserInvite.getTrialTime());
            tenantUser.setInterviewArrangementId(tenantUserInvite.getInterviewArrangementId());
            tenantUser.setTenantUserInviteId(id);
            tenantUserService.createEntity(tenantUser, userId);
        } else {
            // 手机号不存在，则发起邀请
            tenantUserInvite.setIsUsed(IsUsedEnum.NOT_USED.getKey());
            tenantUserInvite.setJoinType(TenantUserJoinType.MANUAL.getKey());
            String id = createEntity(tenantUserInvite, userId);
            // 发送邮件
            sendEmail(tenantUserInvite, userId, id);
        }
    }

    private void sendEmail(TenantUserInvite tenantUserInvite, String userId, String id) {
        String tenantId = TenantContext.getTenantId();
        String content = "您好，欢迎加入，请点击下面的链接完成注册：\n" + tenantInviteUrl + "?id=" + id + "&tenantId=" + tenantId;
        Map<String, Object> emailNotice = new HashMap<>();
        emailNotice.put("title", "新用户邀请");
        emailNotice.put("content", content);
        emailNotice.put("email", tenantUserInvite.getEmail());
        emailNotice.put("type", MqConstants.JobMateMationJobType.ORDINARY_MAIL_DELIVERY.getJobType());
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(JSONUtil.toJsonStr(emailNotice));
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void cancelInviteUsersToJoin(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        // 未使用的邀请可作废
        UpdateWrapper<TenantUserInvite> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUserInvite::getIsUsed), IsUsedEnum.NOT_USED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserInvite::getIsUsed), IsUsedEnum.INVALID.getKey());
        update(updateWrapper);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void resendInviteUsersToJoin(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        TenantUserInvite tenantUserInvite = selectById(id);
        if (tenantUserInvite == null || StrUtil.isEmpty(tenantUserInvite.getId())) {
            throw new CustomException("邀请信息不存在");
        }
        if (tenantUserInvite.getIsUsed().equals(IsUsedEnum.IN_USE.getKey())) {
            throw new CustomException("该邀请已使用");
        }
        if (tenantUserInvite.getIsUsed().equals(IsUsedEnum.INVALID.getKey())) {
            throw new CustomException("该邀请已作废");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        // 发送邮件
        sendEmail(tenantUserInvite, userId, id);
    }

    @Override
    @IgnoreTenant
    public TenantUserInvite selectById(String id) {
        return super.selectById(id);
    }

    @Override
    @IgnoreTenant
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void joinTenantByInvite(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String tenantId = params.get("tenantId").toString();
        TenantUserInvite tenantUserInvite = selectById(id);
        if (tenantUserInvite == null || StrUtil.isEmpty(tenantUserInvite.getId())) {
            throw new CustomException("邀请信息不存在");
        }
        if (tenantUserInvite.getIsUsed().equals(IsUsedEnum.IN_USE.getKey())) {
            throw new CustomException("该邀请已使用");
        }
        if (tenantUserInvite.getIsUsed().equals(IsUsedEnum.INVALID.getKey())) {
            throw new CustomException("该邀请已作废");
        }
        // 校验手机号
        String staffId = sysEveUserStaffService.queryUserStaffByPhone(tenantUserInvite.getPhone());
        if (StrUtil.isEmpty(staffId)) {
            // 手机号不存在，则新增用户信息
            staffId = saveUserStaff(params, tenantUserInvite);
        }
        // 更新邀请信息
        TenantContext.setTenantId(tenantId);
        UpdateWrapper<TenantUserInvite> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.eq(MybatisPlusUtil.toColumns(TenantUserInvite::getIsUsed), IsUsedEnum.NOT_USED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserInvite::getIsUsed), IsUsedEnum.IN_USE.getKey());
        update(updateWrapper);
        // 加入租户
        TenantUser tenantUser = new TenantUser();
        tenantUser.setStaffId(staffId);
        tenantUser.setCompanyId(tenantUserInvite.getCompanyId());
        tenantUser.setDepartmentId(tenantUserInvite.getDepartmentId());
        tenantUser.setJobId(tenantUserInvite.getJobId());
        tenantUser.setJobScoreId(tenantUserInvite.getJobScoreId());
        tenantUser.setState(tenantUserInvite.getState());
        tenantUser.setWorkTime(tenantUserInvite.getWorkTime());
        tenantUser.setEntryTime(tenantUserInvite.getEntryTime());
        tenantUser.setTrialTime(tenantUserInvite.getTrialTime());
        tenantUser.setInterviewArrangementId(tenantUserInvite.getInterviewArrangementId());
        tenantUser.setTenantUserInviteId(tenantUserInvite.getId());
        tenantUser.setWorkstationType(tenantUserInvite.getWorkstationType());
        tenantUser.setHourlyPrice(tenantUserInvite.getHourlyPrice());
        tenantUserService.createEntity(tenantUser, tenantUserInvite.getCreateId());
    }

    @Override
    public void editInviteUsersToExit(String id, Integer exitType) {
        if (StrUtil.isEmpty(id)) {
            return;
        }
        UpdateWrapper<TenantUserInvite> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserInvite::getExitType), exitType);
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserInvite::getExitTime), DateUtil.getTimeAndToString());
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserInvite::getExitIp), ToolUtil.getIpByRequest(PutObject.getRequest()));
        updateWrapper.set(MybatisPlusUtil.toColumns(TenantUserInvite::getExitTerminalType), PutObject.getRequest().getHeader("requestType"));
        update(updateWrapper);
    }

    private String saveUserStaff(Map<String, Object> params, TenantUserInvite tenantUserInvite) {
        SysEveUserStaff sysEveUserStaff = new SysEveUserStaff();
        sysEveUserStaff.setUserName(params.get("userName").toString());
        sysEveUserStaff.setUserSex(Integer.parseInt(params.get("userSex").toString()));
        sysEveUserStaff.setPhone(tenantUserInvite.getPhone());
        sysEveUserStaff.setUserPhoto("/images/util/assest/common/img/anonymous.png");
        sysEveUserStaff.setUserIdCard(params.get("userIdCard").toString());
        sysEveUserStaff.setPassword(params.get("password").toString());
        // 开启自动注册账号
        sysEveUserStaff.setWhetherRegister(WhetherEnum.ENABLE_USING.getKey());
        sysEveUserStaff.setWorkstationType(tenantUserInvite.getWorkstationType());
        sysEveUserStaff.setHourlyPrice(tenantUserInvite.getHourlyPrice());
        // 保存用户信息
        return sysEveUserStaffService.createEntity(sysEveUserStaff, tenantUserInvite.getCreateId());
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.arrangement.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.arrangement.classenum.ArrangementState;
import com.skyeye.arrangement.dao.ArrangementDao;
import com.skyeye.arrangement.entity.Arrangement;
import com.skyeye.arrangement.service.ArrangementService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.centerrest.entity.staff.UserStaffRest;
import com.skyeye.centerrest.entity.tenant.TenantUserInviteRest;
import com.skyeye.centerrest.user.SysEveUserStaffService;
import com.skyeye.centerrest.user.TenantUserInviteService;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.interviewee.classenum.IntervieweeStatusEnum;
import com.skyeye.interviewee.service.IntervieweeService;
import com.skyeye.organization.service.ICompanyJobScoreService;
import com.skyeye.organization.service.IDepmentService;
import com.skyeye.personrequire.service.PersonRequireService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: ArrangementServiceImpl
 * @Description: 面试安排服务层
 * @author: skyeye云系列--卫志强
 * @date: 2022/4/14 11:46
 * @Copyright: 2022 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "面试安排", groupName = "面试安排")
public class ArrangementServiceImpl extends SkyeyeBusinessServiceImpl<ArrangementDao, Arrangement> implements ArrangementService {

    @Autowired
    private IntervieweeService intervieweeService;

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private IDepmentService iDepmentService;

    @Autowired
    private ICompanyJobScoreService iCompanyJobScoreService;

    @Autowired
    private PersonRequireService personRequireService;

    @Autowired
    private TenantUserInviteService tenantUserInviteService;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 我录入的
        commonPageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryBossInterviewArrangementList(commonPageInfo);
        iAuthUserService.setMationForMap(beans, "interviewer", "interviewerMation");
        intervieweeService.setMationForMap(beans, "interviewId", "interviewMation");
        personRequireService.setMationForMap(beans, "personRequireId", "personRequireMation");
        iCompanyJobScoreService.setMationForMap(beans, "jobScoreId", "jobScoreMation");
        return beans;
    }

    @Override
    public void createPrepose(Arrangement entity) {
        Map<String, Object> business = BeanUtil.beanToMap(entity);
        String oddNumber = iCodeRuleService.getNextCodeByClassName(this.getClass().getName(), business);
        entity.setOddNumber(oddNumber);
        entity.setState(ArrangementState.SUBMIT.getKey());
    }

    @Override
    public void validatorEntity(Arrangement entity) {
        // 校验基础信息
        QueryWrapper<Arrangement> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Arrangement::getInterviewId), entity.getInterviewId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Arrangement::getPersonRequireId), entity.getPersonRequireId());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        Arrangement checkArrangement = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(checkArrangement)) {
            throw new CustomException("该面试者已安排面试.");
        }
    }

    @Override
    public void writePostpose(Arrangement entity, String userId) {
        super.writePostpose(entity, userId);
        // 修改面试者信息为面试中
        intervieweeService.editStateById(entity.getInterviewId(), IntervieweeStatusEnum.INTERVIEW_STATUS.getKey());
    }

    @Override
    public Arrangement selectById(String id) {
        Arrangement arrangement = super.selectById(id);
        // 岗位定级信息
        if (StrUtil.isNotEmpty(arrangement.getJobScoreId())) {
            Map<String, Object> jobScore = iCompanyJobScoreService.queryDataMationById(arrangement.getJobScoreId());
            arrangement.setJobScoreMation(jobScore);
        }
        // 面试者信息
        arrangement.setInterviewMation(intervieweeService.selectById(arrangement.getInterviewId()));
        // 面试官信息
        if (StrUtil.isNotEmpty(arrangement.getInterviewer())) {
            Map<String, Object> interviewer = iAuthUserService.queryDataMationById(arrangement.getInterviewer());
            arrangement.setInterviewerMation(interviewer);
        }
        // 人员需求信息
        personRequireService.setDataMation(arrangement, Arrangement::getPersonRequireId);
        return arrangement;
    }

    private void editStateById(String id, Integer state) {
        UpdateWrapper<Arrangement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getState), state);
        update(updateWrapper);
    }

    /**
     * 作废面试安排信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void nullifyArrangement(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Arrangement arrangement = selectById(id);
        if (ObjectUtil.isEmpty(arrangement)) {
            outputObject.setreturnMessage("this data is non-exits.");
            return;
        }
        Integer state = arrangement.getState();
        if (state.equals(ArrangementState.SUBMIT.getKey())
            || state.equals(ArrangementState.TO_BE_INTERVIEWED.getKey())) {
            // 2，3状态可以作废
            editStateById(id, ArrangementState.NULLIFY.getKey());
        }
    }

    @Override
    @IgnoreTenant
    public void queryMyEntryBossPersonRequireAboutArrangementList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo pageInfo = inputObject.getParams(CommonPageInfo.class);
        pageInfo.setCreateId(inputObject.getLogParams().get("id").toString());
        pageInfo.setStateList(getArrangementState());
        Page pages = PageHelper.startPage(pageInfo.getPage(), pageInfo.getLimit());
        if (tenantEnable) {
            pageInfo.setTenantId(TenantContext.getTenantId());
        }
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryMyEntryBossPersonRequireAboutArrangementList(pageInfo);
        iAuthUserService.setMationForMap(beans, "interviewer", "interviewerMation");
        intervieweeService.setMationForMap(beans, "interviewId", "interviewMation");
        personRequireService.setMationForMap(beans, "personRequireId", "personRequireMation");
        iCompanyJobScoreService.setMationForMap(beans, "jobScoreId", "jobScoreMation");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    private List<String> getArrangementState() {
        List<String> arrangementState = new ArrayList<>();
        arrangementState.add(String.valueOf(ArrangementState.SUBMIT.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.TO_BE_INTERVIEWED.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.INTERVIEWED_PASS.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.INTERVIEWED_FAIL.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.COMPLATE.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.COMPLATE_REFUSE.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.INDUCTION_OTHER.getKey()));
        return arrangementState;
    }

    /**
     * 部门经理面试安排信息设置面试官
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setBossInterviewer(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String interviewer = map.get("interviewer").toString();
        Arrangement arrangement = selectById(id);
        if (ObjectUtil.isEmpty(arrangement)) {
            outputObject.setreturnMessage("this data is non-exits.");
            return;
        }
        Integer state = arrangement.getState();
        if (state.equals(ArrangementState.SUBMIT.getKey())
            || state.equals(ArrangementState.TO_BE_INTERVIEWED.getKey())) {
            // 2，3状态可以设置面试官
            UpdateWrapper<Arrangement> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getInterviewer), interviewer);
            updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getState), ArrangementState.TO_BE_INTERVIEWED.getKey());
            update(updateWrapper);
        }
    }

    /**
     * 获取面试官为当前登录用户的面试者信息列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryArrangementInterviewerIsMyList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo queryDo = inputObject.getParams(CommonPageInfo.class);
        queryDo.setCreateId(inputObject.getLogParams().get("id").toString());
        queryDo.setStateList(getArrangementInterviewerIsMyState());

        Page pages = PageHelper.startPage(queryDo.getPage(), queryDo.getLimit());
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryArrangementInterviewerIsMyList(queryDo);
        iAuthUserService.setMationForMap(beans, "interviewer", "interviewerMation");
        intervieweeService.setMationForMap(beans, "interviewId", "interviewMation");
        personRequireService.setMationForMap(beans, "personRequireId", "personRequireMation");
        iCompanyJobScoreService.setMationForMap(beans, "jobScoreId", "jobScoreMation");
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    private List<String> getArrangementInterviewerIsMyState() {
        List<String> arrangementState = new ArrayList<>();
        arrangementState.add(String.valueOf(ArrangementState.TO_BE_INTERVIEWED.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.INTERVIEWED_PASS.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.INTERVIEWED_FAIL.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.COMPLATE.getKey()));
        arrangementState.add(String.valueOf(ArrangementState.COMPLATE_REFUSE.getKey()));
        return arrangementState;
    }

    /**
     * 设置面试结果
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setBossInterviewResult(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Arrangement arrangement = selectById(id);
        if (ObjectUtil.isEmpty(arrangement)) {
            outputObject.setreturnMessage("this data is non-exits.");
            return;
        }
        if (arrangement.getState().equals(ArrangementState.TO_BE_INTERVIEWED.getKey())) {
            // 3状态可以设置面试结果
            UpdateWrapper<Arrangement> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, id);
            updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getEvaluation), map.get("evaluation").toString());
            updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getState), map.get("state").toString());
            update(updateWrapper);
        }
    }

    /**
     * 设置入职结果
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void setInductionResult(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Integer state = Integer.parseInt(map.get("state").toString());
        String reason = map.get("reason").toString();
        Arrangement arrangement = selectById(id);
        if (ObjectUtil.isEmpty(arrangement)) {
            outputObject.setreturnMessage("this data is non-exits.");
            return;
        }
        if (arrangement.getState().equals(ArrangementState.INTERVIEWED_PASS.getKey())) {
            // 4状态可以设置入职结果
            if (state.equals(ArrangementState.COMPLATE.getKey())) {
                // 同意入职
                // 修改该面试安排为通过
                setInductionResult(id, ArrangementState.COMPLATE.getKey(), reason);
                // 修改该面试人员的其他面试为拒绝
                setOtherInductionResult(id, arrangement.getInterviewId());
                // 修改面试者状态为面试通过
                intervieweeService.editStateById(arrangement.getInterviewId(), IntervieweeStatusEnum.INTERVIEW_PASS_STATUS.getKey());
                // 修改人员需求申请单信息
                personRequireService.updatePersonRequireNum(arrangement.getPersonRequireId(), CommonNumConstants.NUM_ONE);
                if (!tenantEnable) {
                    // 未开启租户模式，添加员工信息
                    UserStaffRest sysUserStaff = getSysUserStaffMation(arrangement, map);
                    ExecuteFeignClient.get(() -> sysEveUserStaffService.insertNewUserStaff(sysUserStaff));
                } else {
                    // 开启租户模式，邀请用户加入
                    TenantUserInviteRest tenantUserInviteRest = getTenantUserInviteRest(arrangement, map);
                    ExecuteFeignClient.get(() -> tenantUserInviteService.inviteUsersToJoin(tenantUserInviteRest));
                }
            } else {
                // 拒绝入职
                if (ToolUtil.isBlank(reason)) {
                    outputObject.setreturnMessage("请填写拒绝入职的原因.");
                    return;
                }
                setInductionResult(id, ArrangementState.COMPLATE_REFUSE.getKey(), reason);
            }
        }
    }

    private void setInductionResult(String id, Integer state, String reason) {
        UpdateWrapper<Arrangement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getState), state);
        updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getReason), reason);
        update(updateWrapper);
    }

    private void setOtherInductionResult(String id, String interviewId) {
        UpdateWrapper<Arrangement> updateWrapper = new UpdateWrapper<>();
        updateWrapper.ne(CommonConstants.ID, id);
        updateWrapper.ne(MybatisPlusUtil.toColumns(Arrangement::getState), ArrangementState.NULLIFY.getKey());
        updateWrapper.eq(MybatisPlusUtil.toColumns(Arrangement::getInterviewId), interviewId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getState), ArrangementState.INDUCTION_OTHER.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(Arrangement::getReason), "已入职其他项目组");
        update(updateWrapper);
    }

    private UserStaffRest getSysUserStaffMation(Arrangement arrangement, Map<String, Object> map) {
        UserStaffRest sysUserStaff = new UserStaffRest();
        sysUserStaff.setUserName(arrangement.getInterviewMation().getName());
        sysUserStaff.setUserSex(arrangement.getInterviewMation().getSex());
        sysUserStaff.setPhone(arrangement.getInterviewMation().getPhone());
        sysUserStaff.setUserPhoto("../../assets/images/anonymousphoto.jpg");
        sysUserStaff.setUserIdCard(map.get("userIdCard").toString());
        String departmentId = arrangement.getPersonRequireMation().getRecruitDepartmentId();
        Map<String, Object> department = iDepmentService.queryDataMationById(departmentId);
        sysUserStaff.setCompanyId(department.get("companyId").toString());
        sysUserStaff.setDepartmentId(departmentId);
        sysUserStaff.setJobId(arrangement.getPersonRequireMation().getRecruitJobId());
        sysUserStaff.setWorkTime(map.get("workTime").toString());
        sysUserStaff.setEntryTime(map.get("entryTime").toString());
        if (StrUtil.isEmpty(map.get("inductionState").toString())) {
            throw new CustomException("员工入职状态不能为空。");
        }
        sysUserStaff.setState(Integer.parseInt(map.get("inductionState").toString()));
        sysUserStaff.setTrialTime(map.get("trialTime").toString());
        sysUserStaff.setInterviewArrangementId(arrangement.getId());
        return sysUserStaff;
    }

    private TenantUserInviteRest getTenantUserInviteRest(Arrangement arrangement, Map<String, Object> map) {
        String email = map.get("email").toString();
        if (StrUtil.isEmpty(email)) {
            throw new CustomException("邮箱不能为空。");
        }
        TenantUserInviteRest tenantUserInviteRest = new TenantUserInviteRest();
        tenantUserInviteRest.setEmail(email);
        tenantUserInviteRest.setPhone(arrangement.getInterviewMation().getPhone());

        String departmentId = arrangement.getPersonRequireMation().getRecruitDepartmentId();
        Map<String, Object> department = iDepmentService.queryDataMationById(departmentId);
        tenantUserInviteRest.setCompanyId(department.get("companyId").toString());
        tenantUserInviteRest.setDepartmentId(departmentId);
        tenantUserInviteRest.setJobId(arrangement.getPersonRequireMation().getRecruitJobId());
        tenantUserInviteRest.setWorkTime(map.get("workTime").toString());
        tenantUserInviteRest.setEntryTime(map.get("entryTime").toString());
        if (StrUtil.isEmpty(map.get("inductionState").toString())) {
            throw new CustomException("员工入职状态不能为空。");
        }
        tenantUserInviteRest.setState(Integer.parseInt(map.get("inductionState").toString()));
        tenantUserInviteRest.setTrialTime(map.get("trialTime").toString());
        tenantUserInviteRest.setInterviewArrangementId(arrangement.getId());
        return tenantUserInviteRest;
    }

}

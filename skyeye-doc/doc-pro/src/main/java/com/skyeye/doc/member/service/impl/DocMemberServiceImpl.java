/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.doc.member.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.enumeration.RequestType;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.doc.member.dao.DocMemberDao;
import com.skyeye.doc.member.entity.DocMember;
import com.skyeye.doc.member.entity.DocMemberPackage;
import com.skyeye.doc.member.entity.DocMemberVersion;
import com.skyeye.doc.member.enums.DocMemberLoginLogDeviceType;
import com.skyeye.doc.member.enums.DocMemberLoginLogStatus;
import com.skyeye.doc.member.service.*;
import com.skyeye.exception.CustomException;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: DocMemberServiceImpl
 * @Description: 会员服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2025/8/19 22:23
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "会员管理", groupName = "会员管理", tenant = TenantEnum.PLATE)
public class DocMemberServiceImpl extends SkyeyeBusinessServiceImpl<DocMemberDao, DocMember> implements DocMemberService {

    @Autowired
    private DocMemberLevelService docMemberLevelService;

    @Autowired
    private DocMemberVersionService docMemberVersionService;

    @Autowired
    private DocMemberPackageService docMemberPackageService;

    @Autowired
    private DocMemberLoginLogService docMemberLoginLogService;

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        docMemberLevelService.setMationForMap(beans, "levelId", "levelMation");
        return beans;
    }

    @Override
    protected void createPrepose(DocMember entity) {
        if (StrUtil.isEmpty(entity.getPassword())) {
            throw new CustomException("密码不能为空！");
        }
        int pwdNum = (int) (Math.random() * 100);
        entity.setPwdNumEnc(pwdNum);
        entity.setPassword(getCalcPaswword(entity.getPassword(), pwdNum));
    }

    private String getCalcPaswword(String password, int pwdNum) {
        for (int i = 0; i < pwdNum; i++) {
            password = ToolUtil.MD5(password);
        }
        return password;
    }

    @Override
    protected void updatePrepose(DocMember entity) {
        DocMember oldMember = selectById(entity.getId());
        entity.setPassword(oldMember.getPassword());
        entity.setPwdNumEnc(oldMember.getPwdNumEnc());
    }

    @Override
    protected void writePostpose(DocMember entity, String userId) {
        super.writePostpose(entity, userId);
        // 保存购买的版本信息
        docMemberVersionService.saveList(entity.getId(), entity.getVersionIds());
        // 保存购买的源代码包信息
        docMemberPackageService.saveList(entity.getId(), entity.getPackageIds());
        if (EnableEnum.DISABLE_USING.getKey().equals(entity.getEnabled())) {
            // 如果禁用了，则清除登录缓存
            SysUserAuthConstants.delUserLoginRedisCache(entity.getId());
            SysUserAuthConstants.delUserLoginRedisCache(entity.getId() + SysUserAuthConstants.APP_IDENTIFYING);
            return;
        }
        // 更新PC端登录缓存
        if (SysUserAuthConstants.exitUserLoginRedisCache(entity.getId())) {
            Map<String, Object> userLoginRedisCache = SysUserAuthConstants.getUserLoginRedisCache(entity.getId());
            setLoginMation(entity, userLoginRedisCache);
            SysUserAuthConstants.setUserLoginRedisCache(entity.getId(), userLoginRedisCache);
        }
        // 更新APP端登录缓存
        if (SysUserAuthConstants.exitUserLoginRedisCache(entity.getId() + SysUserAuthConstants.APP_IDENTIFYING)) {
            Map<String, Object> userLoginRedisCache = SysUserAuthConstants.getUserLoginRedisCache(entity.getId() + SysUserAuthConstants.APP_IDENTIFYING);
            setLoginMation(entity, userLoginRedisCache);
            SysUserAuthConstants.setUserLoginRedisCache(entity.getId() + SysUserAuthConstants.APP_IDENTIFYING, userLoginRedisCache);
        }
    }

    @Override
    public DocMember getDataFromDb(String id) {
        DocMember member = super.getDataFromDb(id);
        // 版本信息
        List<DocMemberVersion> docMemberVersionList = docMemberVersionService.selectByMemberId(member.getId());
        member.setVersionList(docMemberVersionList);
        // 包信息
        List<DocMemberPackage> docMemberPackageList = docMemberPackageService.selectByMemberId(member.getId());
        member.setPackageList(docMemberPackageList);
        return member;
    }

    private static void setLoginMation(DocMember entity, Map<String, Object> userLoginRedisCache) {
        userLoginRedisCache.put("joinTime", entity.getJoinTime());
        userLoginRedisCache.put("planetNum", entity.getPlanetNum());
        userLoginRedisCache.put("planetEnter", entity.getPlanetEnter());
        userLoginRedisCache.put("levelId", entity.getLevelId());
    }

    @Override
    @IgnoreTenant
    public void loginDocMember(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String phone = map.get("phone").toString();
        DocMember member = queryMemberByPhone(phone);
        if (ObjectUtil.isEmpty(member)) {
            recordLoginFailureLog("unknown", phone, DocMemberLoginLogDeviceType.PC.getKey(), "用户不存在");
            throw new CustomException("手机号码不存在，请先注册！");
        }
        if (EnableEnum.DISABLE_USING.getKey().equals(member.getEnabled())) {
            recordLoginFailureLog("unknown", phone, DocMemberLoginLogDeviceType.PC.getKey(), "账号已被禁用，请联系客服！");
            throw new CustomException("账号已被禁用，请联系客服！");
        }
        String password = map.get("password").toString();
        for (int i = 0; i < member.getPwdNumEnc(); i++) {
            password = ToolUtil.MD5(password);
        }
        if (!StrUtil.equals(password, member.getPassword())) {
            recordLoginFailureLog(member.getId(), phone, DocMemberLoginLogDeviceType.PC.getKey(), "密码错误");
            throw new CustomException("密码错误！");
        }

        member = getMember(RequestType.PC.getKey(), member, password);
        // 异步记录登录日志
        docMemberLoginLogService.recordLoginLogAsync(member.getId(), phone,
            DocMemberLoginLogDeviceType.PC.getKey(), DocMemberLoginLogStatus.SUCCESS.getKey(), "登录成功");
        outputObject.setBean(member);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    public DocMember queryMemberByPhone(String phone) {
        QueryWrapper<DocMember> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocMember::getPhone), phone);
        DocMember member = getOne(queryWrapper, false);
        return member;
    }

    @NotNull
    private static DocMember getMember(String requestType, DocMember member, String password) {
        member.setPassword(null);
        member.setPwdNumEnc(null);
        String userToken;
        // 一个账号最多可同时登录的设备数：1
        GetUserToken.setDefaultMaxDevicesPerUser(CommonNumConstants.NUM_ONE);
        if (RequestType.APP.getKey().equals(requestType)) {
            userToken = GetUserToken.createNewToken(member.getId() + SysUserAuthConstants.APP_IDENTIFYING, password);
            SysUserAuthConstants.setUserLoginRedisCache(member.getId() + SysUserAuthConstants.APP_IDENTIFYING, BeanUtil.beanToMap(member));
        } else {
            userToken = GetUserToken.createNewToken(member.getId(), password);
            SysUserAuthConstants.setUserLoginRedisCache(member.getId(), BeanUtil.beanToMap(member));
        }
        member.setUserToken(userToken);
        return member;
    }

    /**
     * 记录登录失败日志
     *
     * @param userCode      用户账号
     * @param failureReason 失败原因
     */
    private void recordLoginFailureLog(String userId, String userCode, Integer deviceType, String failureReason) {
        docMemberLoginLogService.recordLoginLogAsync(userId, userCode, deviceType, DocMemberLoginLogStatus.FAIL.getKey(), failureReason);
    }

    @Override
    public void docMemberLoginMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> userMation = inputObject.getLogParams();
        if (userMation == null) {
            outputObject.setreturnMessage("登录超时，请重新登录。");
        } else {
            docMemberLevelService.setMationForMap(userMation, "levelId", "levelMation");
            outputObject.setBean(userMation);
        }
    }

    @Override
    public void logoutDocMember(InputObject inputObject, OutputObject outputObject) {
        String userTokenId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        SysUserAuthConstants.delUserLoginRedisCache(userTokenId);
        inputObject.removeSession();
    }

    @Override
    public void editDocMemberPassword(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String newPassword = map.get("newPassword").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        int pwdNum = (int) (Math.random() * 100);
        for (int i = 0; i < pwdNum; i++) {
            newPassword = ToolUtil.MD5(newPassword);
        }
        UpdateWrapper<DocMember> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(DocMember::getPassword), newPassword);
        updateWrapper.set(MybatisPlusUtil.toColumns(DocMember::getPwdNumEnc), pwdNum);
        update(updateWrapper);
    }

}

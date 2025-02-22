/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.personnel.service.impl;

import cn.hutool.core.util.StrUtil;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.enumeration.SmsSceneEnum;
import com.skyeye.common.object.*;
import com.skyeye.eve.authority.service.SysAuthorityService;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import com.skyeye.personnel.entity.SysEveUserStaff;
import com.skyeye.personnel.service.AppAuthService;
import com.skyeye.personnel.service.SysEveUserStaffService;
import com.skyeye.sms.entity.SmsCodeSendReq;
import com.skyeye.sms.entity.SmsCodeValidateReq;
import com.skyeye.sms.service.SmsCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: AppAuthServiceImpl
 * @Description: 登录管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/8/28 17:07
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class AppAuthServiceImpl implements AppAuthService {

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private SmsCodeService smsCodeService;

    @Autowired
    private SysAuthorityService sysAuthorityService;

    @Autowired
    protected JedisClientService jedisClientService;

    @Override
    public void sendSmsCode(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mobile = params.get("mobile").toString();
        Integer scene = Integer.parseInt(params.get("scene").toString());
        // 情况 1：如果是修改手机场景，需要校验新手机号是否已经注册，说明不能使用该手机了
        if (scene == SmsSceneEnum.UPDATE_MOBILE.getKey()) {
            if (sysEveUserStaffService.checkPhoneExists(mobile)) {
                throw new CustomException("手机号已经被使用");
            }
        }
        // 情况 2：如果是重置密码场景，需要校验手机号是存在的
        if (scene == SmsSceneEnum.RESET_PASSWORD.getKey()) {
            if (!sysEveUserStaffService.checkPhoneExists(mobile)) {
                throw new CustomException("手机号不存在");
            }
        }
        // 情况 3：如果是修改密码场景，需要查询手机号，无需前端传递
        if (scene == SmsSceneEnum.UPDATE_PASSWORD.getKey()) {
            String staffId = inputObject.getLogParams().get("staffId").toString();
            SysEveUserStaff sysEveUserStaff = sysEveUserStaffService.selectById(staffId);
            if (StrUtil.isEmpty(sysEveUserStaff.getPhone())) {
                throw new CustomException("您还没有绑定手机号，请先绑定手机号");
            }
            mobile = sysEveUserStaff.getPhone();
        }
        if (StrUtil.isEmpty(mobile)) {
            throw new CustomException("手机号不能为空");
        }
        // 发送短信验证码
        SmsCodeSendReq smsCodeSendReq = new SmsCodeSendReq().setMobile(mobile).setScene(scene);
        smsCodeService.sendSmsCodeReq(smsCodeSendReq);

    }

    @Override
    public void smsLogin(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String mobile = params.get("mobile").toString();
        String smsCode = params.get("smsCode").toString();
        // 校验验证码
        SmsCodeValidateReq smsCodeValidateReq = new SmsCodeValidateReq();
        smsCodeValidateReq.setMobile(mobile);
        smsCodeValidateReq.setSmsCode(smsCode);
        smsCodeValidateReq.setScene(SmsSceneEnum.LOGIN.getKey());
        smsCodeService.validateSmsCode(smsCodeValidateReq);
    }

    @Override
    public void queryAuthPointByUserId(InputObject inputObject, OutputObject outputObject) {
        String userIdAndType = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        // 获取角色id(逗号隔开的字符串)
        String roleIds = jedisClientService.get(ObjectConstant.getUserHasRoleIds(
            userIdAndType.replaceFirst(SysUserAuthConstants.APP_IDENTIFYING, StrUtil.EMPTY)));
        List<Map<String, Object>> authPoints = sysAuthorityService.getRoleHasMenuPointListByRoleIds(roleIds, userIdAndType);
        outputObject.setBeans(authPoints);
        outputObject.settotal(authPoints.size());
    }
}

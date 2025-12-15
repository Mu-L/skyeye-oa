/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.enterprise.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.RequestType;
import com.skyeye.common.enumeration.TenantEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.enterprise.dao.UserEnterpriseDao;
import com.skyeye.enterprise.entity.UserEnterprise;
import com.skyeye.enterprise.enums.UserEnterpriseState;
import com.skyeye.enterprise.service.UserEnterpriseService;
import com.skyeye.exception.CustomException;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @ClassName: UserEnterpriseServiceImpl
 * @Description: 企业账号服务层--不隔离
 * @author: skyeye云系列--卫志强
 * @date: 2025/12/15 14:17
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
@Service
@SkyeyeService(name = "企业账户", groupName = "企业账户", tenant = TenantEnum.NO_ISOLATION)
public class UserEnterpriseServiceImpl extends SkyeyeBusinessServiceImpl<UserEnterpriseDao, UserEnterprise> implements UserEnterpriseService {

    @Override
    protected QueryWrapper<UserEnterprise> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<UserEnterprise> queryWrapper = super.getQueryWrapper(commonPageInfo);
        // 认证状态
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserEnterprise::getState), commonPageInfo.getState());
        return queryWrapper;
    }

    @Override
    public String createEntity(UserEnterprise entity, String userId) {
        // 创建的时候对用户名(账号)进行唯一性校验
        QueryWrapper<UserEnterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserEnterprise::getUserCode), entity.getUserCode());
        UserEnterprise userEnterprise = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(userEnterprise)) {
            throw new CustomException("用户名已存在，请更换！");
        }
        return createEntity(entity, userId);
    }

    @Override
    protected void createPrepose(UserEnterprise entity) {
        entity.setPassword(ToolUtil.MD5(entity.getPassword()));
        entity.setState(UserEnterpriseState.CERTIFIEDING.getKey());
        entity.setCreateTime(DateUtil.getTimeAndToString());
    }

    @Override
    public String updateEntity(UserEnterprise entity, String userId) {
        UserEnterprise oldUserEnterprise = selectById(userId);
        if (ObjectUtil.isEmpty(oldUserEnterprise) || StrUtil.isEmpty(oldUserEnterprise.getId())) {
            throw new CustomException("用户不存在！");
        }
        if (!UserEnterpriseState.CERTIFIED_FAILURE.getKey().equals(oldUserEnterprise.getState())) {
            // 只有认证失败的才能重新认证
            throw new CustomException("认证状态不允许修改。");
        }
        entity.setId(userId);
        entity.setPassword(oldUserEnterprise.getPassword());
        entity.setState(UserEnterpriseState.CERTIFIEDING.getKey());
        return super.updateEntity(entity, userId);
    }

    @Override
    protected void validatorEntity(UserEnterprise entity) {
        // 对营业执照注册号进行唯一性校验
        QueryWrapper<UserEnterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserEnterprise::getSocialCreditCode), entity.getSocialCreditCode());
        if (StringUtils.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        UserEnterprise userEnterprise = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(userEnterprise)) {
            throw new CustomException("营业执照注册号已存在，请更换！");
        }
    }

    @Override
    protected void writePostpose(UserEnterprise entity, String userId) {
        super.writePostpose(entity, userId);

        entity.setPassword(null);
        // 更新PC端登录缓存
        if (SysUserAuthConstants.exitUserLoginRedisCache(entity.getId())) {
            SysUserAuthConstants.setUserLoginRedisCache(entity.getId(), BeanUtil.beanToMap(entity));
        }
        // 更新APP端登录缓存
        if (SysUserAuthConstants.exitUserLoginRedisCache(entity.getId() + SysUserAuthConstants.APP_IDENTIFYING)) {
            SysUserAuthConstants.setUserLoginRedisCache(entity.getId() + SysUserAuthConstants.APP_IDENTIFYING, BeanUtil.beanToMap(entity));
        }
    }

    @Override
    public void loginUserEnterprise(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String phone = map.get("phone").toString();
        UserEnterprise userEnterprise = queryUserByUserCode(phone);
        if (ObjectUtil.isEmpty(userEnterprise)) {
            throw new CustomException("用户名不存在，请先注册！");
        }
        String password = ToolUtil.MD5(map.get("password").toString());
        if (!StrUtil.equals(password, userEnterprise.getPassword())) {
            throw new CustomException("密码错误！");
        }

        String requestType = map.get("requestType").toString();
        userEnterprise = getUserEnterprise(requestType, userEnterprise, password);
        outputObject.setBean(userEnterprise);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void updateUserEnterprisePassword(InputObject inputObject, OutputObject outputObject) {
        String password = ToolUtil.MD5(inputObject.getParams().get("password").toString());
        String userId = inputObject.getLogParams().get("id").toString();
        UpdateWrapper<UserEnterprise> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(UserEnterprise::getPassword), password);
        update(updateWrapper);
        refreshCache(userId);
    }

    public UserEnterprise queryUserByUserCode(String userCode) {
        QueryWrapper<UserEnterprise> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(UserEnterprise::getUserCode), userCode);
        UserEnterprise userEnterprise = getOne(queryWrapper, false);
        return userEnterprise;
    }

    @NotNull
    private static UserEnterprise getUserEnterprise(String requestType, UserEnterprise userEnterprise, String password) {
        userEnterprise.setPassword(null);
        String userToken;
        if (RequestType.APP.getKey().equals(requestType)) {
            userToken = GetUserToken.createNewToken(userEnterprise.getId() + SysUserAuthConstants.APP_IDENTIFYING, password);
            SysUserAuthConstants.setUserLoginRedisCache(userEnterprise.getId() + SysUserAuthConstants.APP_IDENTIFYING, BeanUtil.beanToMap(userEnterprise));
        } else {
            userToken = GetUserToken.createNewToken(userEnterprise.getId(), password);
            SysUserAuthConstants.setUserLoginRedisCache(userEnterprise.getId(), BeanUtil.beanToMap(userEnterprise));
        }
        userEnterprise.setUserToken(userToken);
        return userEnterprise;
    }

    @Override
    public void queryCurrentLoginUserEnterprise(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        UserEnterprise userEnterprise = selectById(userId);
        userEnterprise.setPassword(null);
        outputObject.setBean(userEnterprise);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }


}

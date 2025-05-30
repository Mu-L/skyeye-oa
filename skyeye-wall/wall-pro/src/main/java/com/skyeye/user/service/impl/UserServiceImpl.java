/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.certification.classenum.StateEnum;
import com.skyeye.certification.entity.Certification;
import com.skyeye.certification.service.CertificationService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.RequestType;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.focus.service.FocusService;
import com.skyeye.rest.promote.tenant.service.ITenantsService;
import com.skyeye.user.dao.UserDao;
import com.skyeye.user.entity.User;
import com.skyeye.user.entity.UserView;
import com.skyeye.user.service.UserService;
import com.skyeye.user.service.UserViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: UserServiceImpl
 * @Description: 用户服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户管理", groupName = "用户管理")
public class UserServiceImpl extends SkyeyeBusinessServiceImpl<UserDao, User> implements UserService {

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private FocusService focusService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private UserViewService userViewService;

    @Autowired
    private ITenantsService iTenantService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryUserList(commonPageInfo);
        return setCertification(beans);
    }

    public List<Map<String, Object>> setCertification(List<Map<String, Object>> beans) {
        List<String> userIds = beans.stream()
            .map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        List<Certification> CertificationList = certificationService.getCertificationListByIds(userIds);
        beans.forEach(bean -> {
            String userId = bean.get("id").toString();
            for (int i = 0; i < CertificationList.size(); i++) {
                if (CertificationList.get(i).getUserId().equals(userId)) {
                    bean.put("state", CertificationList.get(i).getState());
                    break;
                }
                bean.put("state", StateEnum.UNVERIFIED.getKey());
            }
        });
        return beans;
    }

    @Override
    public void validatorEntity(User entity) {
        super.validatorEntity(entity);
        if (StrUtil.isEmpty(entity.getId())) {
            if (StrUtil.isEmpty(entity.getAccountNumber())) {
                throw new CustomException("账号不能为空。");
            }
            if (StrUtil.isEmpty(entity.getPassword())) {
                throw new CustomException("密码不能为空。");
            }
        } else {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, Object> map = iAuthUserService.queryDataMationById(userId);
            if (!userId.equals(entity.getId()) && CollectionUtil.isEmpty(map)) {
                throw new CustomException("无权限，不可修改");
            }
        }
    }

    @Override
    public void createPrepose(User entity) {
        entity.setCreateTime(DateUtil.getTimeAndToString());
    }

    @Override
    public void updatePrepose(User entity) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, entity.getId());
        User user = getOne(queryWrapper);
        entity.setPassword(user.getPassword());
        entity.setRealName(user.getRealName());
        entity.setStudentNumber(user.getStudentNumber());
    }

    @Override
    public User selectById(String id) {
        //当前学生账户Id
        User user = super.selectById(id);
        Certification certification = certificationService.selectById(id);
        if (certification == null) {
            user.setState(StateEnum.UNVERIFIED.getKey());
            return user;
        } else {
            user.setState(certification.getState());
        }
        user.setPassword(StrUtil.EMPTY);
        return user;
    }

    @Override
    public List<User> selectByIds(String... ids) {
        List<User> userList = super.selectByIds(ids);
        userList.forEach(user -> {
            user.setPassword(StrUtil.EMPTY);
        });
        return userList;
    }

    @Override
    @IgnoreTenant
    public void wallUserLogin(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String accountNumber = map.get("accountNumber").toString();
        String password = ToolUtil.MD5(map.get("password").toString());

        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(User::getAccountNumber), accountNumber);
        User user = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(user)) {
            outputObject.setreturnMessage("请确保账号输入无误！");
        } else {
            if (password.equals(user.getPassword())) {
                Map<String, Object> userMation = BeanUtil.beanToMap(user);
                String requestType = InputObject.getRequest().getHeader("requestType");
                // 学生这里以学生id作为userToken,确保不会和后台登录用户的id重复
                String userToken;
                if (RequestType.APP.getKey().equals(requestType)) {
                    SysUserAuthConstants.setUserLoginRedisCache(user.getId() + SysUserAuthConstants.APP_IDENTIFYING, userMation);
                    userToken = GetUserToken.createNewToken(user.getId() + SysUserAuthConstants.APP_IDENTIFYING, user.getPassword());
                } else {
                    SysUserAuthConstants.setUserLoginRedisCache(user.getId(), userMation);
                    userToken = GetUserToken.createNewToken(user.getId(), user.getPassword());
                }
                if (tenantEnable) {
                    // 多组户
                    userMation.put("tenantId", user.getTenantId());
                    userMation.put("tenantMation", iTenantService.queryTenantById(user.getTenantId()));
                }
                userMation.put("userToken", userToken);
                userMation.put("password", null);
                outputObject.setBean(userMation);
            } else {
                outputObject.setreturnMessage("密码输入错误！");
            }
        }
    }

    @Override
    @IgnoreTenant
    public void wallUserRegister(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String name = map.get("name").toString();
        String accountNumber = map.get("accountNumber").toString();
        String password = ToolUtil.MD5(map.get("password").toString());
        // 验证账号是否存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(User::getAccountNumber), accountNumber);
        User temp = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(temp)) {
            throw new CustomException("该账号已存在!");
        }
        User user = new User();
        if (tenantEnable) {
            // 多租户模式下
            String tenantId = (String) map.get("tenantId");
            if (StrUtil.isEmpty(tenantId)) {
                throw new CustomException("请选择租户!");
            }
            user.setTenantId(tenantId);
        }
        user.setName(name);
        user.setAccountNumber(accountNumber);
        user.setPassword(password);
        createEntity(user, StrUtil.EMPTY);
    }

    @Override
    public void wallUserExit(InputObject inputObject, OutputObject outputObject) {
        String userTokenId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        SysUserAuthConstants.delUserLoginRedisCache(userTokenId);
    }

    @Override
    public void editWallPassword(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        // 获取当前用户信息
        String stuId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, stuId);
        User user = getOne(queryWrapper);
        // 旧密码匹配
        if (user.getPassword().equals(ToolUtil.MD5(map.get("oldPassword").toString()))) {
            String newPassword = ToolUtil.MD5(map.get("newPassword").toString());
            UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq(CommonConstants.ID, stuId);
            updateWrapper.set(MybatisPlusUtil.toColumns(User::getPassword), newPassword);
            update(updateWrapper);
            if (update(updateWrapper)) {
                String userTokenId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
                SysUserAuthConstants.delUserLoginRedisCache(userTokenId);
            }
        } else {
            outputObject.setreturnMessage("旧密码输入错误.");
        }
    }

    @Override
    public void updateBackgroundImage(String id, String image) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(User::getBackgroundImage), image);
        update(updateWrapper);
        refreshCache(id);

        // 更新登录缓存信息
        Map<String, Object> user = InputObject.getLogParamsStatic();
        user.put("backgroundImage", image);
        SysUserAuthConstants.setUserLoginRedisCache(user.get("id").toString() + SysUserAuthConstants.APP_IDENTIFYING, user);
        SysUserAuthConstants.setUserLoginRedisCache(user.get("id").toString(), user);
    }

    @Override
    public void setCertification(String id, String studentNumber, String realName) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(User::getStudentNumber), studentNumber);
        updateWrapper.set(MybatisPlusUtil.toColumns(User::getRealName), realName);
        update(updateWrapper);
        refreshCache(id);

        // 更新登录缓存信息
        Map<String, Object> user = InputObject.getLogParamsStatic();
        user.put("studentNumber", studentNumber);
        user.put("realName", realName);
        SysUserAuthConstants.setUserLoginRedisCache(user.get("id").toString() + SysUserAuthConstants.APP_IDENTIFYING, user);
        SysUserAuthConstants.setUserLoginRedisCache(user.get("id").toString(), user);
    }

    @Override
    public void queryUserByRealNameOrStudentNumber(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        String keyword = commonPageInfo.getKeyword();
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.like(MybatisPlusUtil.toColumns(User::getStudentNumber), keyword)
                .or().like(MybatisPlusUtil.toColumns(User::getName), keyword);
        }
        List<User> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void queryUserById(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getParams().get("id").toString();
        boolean isCheck = focusService.checkFocus(userId);
        User user = selectById(userId);
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isNotEmpty(userToken)) {
            String visitorUserId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
            UserView userView = new UserView();
            userView.setUserId(userId);
            userView.setVisitorUserId(visitorUserId);
            userViewService.createEntity(userView, visitorUserId);
        }
        if (StrUtil.isEmpty(user.getId())) {
            Map<String, Object> teacherUser = new HashMap<>();
            teacherUser.put("createId", userId);
            teacherUser.put("createMation", StrUtil.EMPTY);
            teacherUser.put("checkFocus", isCheck);
            iAuthUserService.setMationForMap(teacherUser, "createId", "createMation");
            outputObject.setBean(teacherUser);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } else {
            user.setCheckFocus(isCheck);
            outputObject.setBean(user);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        }
    }

    /**
     * 检验createId 是老师id还是学生id
     * true -- 老师
     * false -- 学生
     */
    @Override
    public boolean checkCreateIdIsStudent(String createId) {
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, createId);
        User user = getOne(queryWrapper);
        return ObjectUtil.isEmpty(user);
    }

    /**
     * 根据学号查询用户信息
     * 用于school远程调用(章节分析)
     *
     * @param inputObject
     * @param outputObject
     */
    @Override
    public void queryListBuStudentNumberList(InputObject inputObject, OutputObject outputObject) {
        String studentNumberStr = inputObject.getParams().get("studentNumberList").toString();
        List<String> studentNumberList = Arrays.asList(studentNumberStr.split(","));
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(User::getStudentNumber), studentNumberList);
        List<User> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
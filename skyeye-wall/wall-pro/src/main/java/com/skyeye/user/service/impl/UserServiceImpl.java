/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.user.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.certification.classenum.StateEnum;
import com.skyeye.certification.entity.Certification;
import com.skyeye.certification.service.CertificationService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.SysUserAuthConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.RequestType;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.rest.school.service.ISchoolService;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.dao.UserDao;
import com.skyeye.user.entity.User;
import com.skyeye.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
            if (!userId.equals(entity.getId())) {
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

    @Autowired
    private ISchoolService iSchoolService;

    @Override
    public User selectById(String id) {
        //当前学生账户Id
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        User user = super.selectById(id);
        String studentNumber = user.getStudentNumber();
        List<Map<String, Object>> schoolStudentMation = iSchoolService.querySchoolStudentMation(studentNumber,id,userId);
        user.setSchoolStudentMation(schoolStudentMation);
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
                userMation.put("userToken", userToken);
                userMation.put("password", null);
                outputObject.setBean(userMation);
            } else {
                outputObject.setreturnMessage("密码输入错误！");
            }
        }
    }

    @Override
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
        user.setName(name);
        user.setAccountNumber(accountNumber);
        user.setPassword(password);
        createEntity(user, StrUtil.EMPTY);
    }

    @Override
    public void wallUserExit(InputObject inputObject, OutputObject outputObject) {
        String userId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
        SysUserAuthConstants.delUserLoginRedisCache(userId);
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
                String userId = GetUserToken.getUserTokenUserId(PutObject.getRequest());
                SysUserAuthConstants.delUserLoginRedisCache(userId);
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
    }

    @Override
    public void setCertification(String id, String studentNumber, String realName) {
        UpdateWrapper<User> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(User::getStudentNumber), studentNumber);
        updateWrapper.set(MybatisPlusUtil.toColumns(User::getRealName), realName);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryUserByRealNameOrStudentNumber(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String name = map.get("realName").toString();
        String studentNumber = map.get("studentNumber").toString();
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(name)){
            queryWrapper.like(MybatisPlusUtil.toColumns(User::getRealName), name);
        }
        if (StrUtil.isNotEmpty(studentNumber)){
            queryWrapper.like(MybatisPlusUtil.toColumns(User::getStudentNumber), studentNumber);
        }
        List<User> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }
}
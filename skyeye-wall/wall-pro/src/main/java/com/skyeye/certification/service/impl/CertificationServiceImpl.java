/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.certification.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.certification.classenum.StateEnum;
import com.skyeye.certification.dao.CertificationDao;
import com.skyeye.certification.entity.Certification;
import com.skyeye.certification.service.CertificationService;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.school.student.service.IStudentService;
import com.skyeye.user.entity.User;
import com.skyeye.user.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CertificationServiceImpl
 * @Description: 学生认证信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/24 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "学生认证管理", groupName = "学生认证管理")
public class CertificationServiceImpl extends SkyeyeBusinessServiceImpl<CertificationDao, Certification> implements CertificationService {

    @Autowired
    private CertificationService certificationService;

    @Autowired
    private UserService userService;

    @Autowired
    private IStudentService iStudentService;

    @Override
    protected QueryWrapper<Certification> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Certification> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            if (!StrUtil.equals(commonPageInfo.getState(), "All")) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(Certification::getState), commonPageInfo.getState());
            }
        }
        return queryWrapper;
    }


    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> maps = super.queryPageDataList(inputObject);
        List<String> studentNumber = maps.stream().map(map -> map.get("studentNumber").toString()).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(studentNumber)) {
            return new ArrayList<>();
        }
        String studentNumbers = Joiner.on(CommonCharConstants.COMMA_MARK).join(studentNumber);
        List<Map<String, Object>> maps1 = iStudentService.queryStudentByStudentNumbers(studentNumbers);
        Map<String, Map<String, Object>> studentNumberMap = maps1.stream()
            .collect(Collectors.toMap(map -> map.get("no").toString(), map -> map));
        for (Map<String, Object> map : maps) {
            String studentNumber1 = map.get("studentNumber").toString();
            if (studentNumberMap.containsKey(studentNumber1)) {
                Map<String, Object> matchedMap = studentNumberMap.get(studentNumber1);
                Map<String,Object>  schoolMation = JSONUtil.toBean(JSONUtil.toJsonStr(matchedMap.get("schoolMation")),null);
                Map<String,Object>  facultyMation = JSONUtil.toBean(JSONUtil.toJsonStr(matchedMap.get("facultyMation")),null);
                Map<String,Object>  majorMation = JSONUtil.toBean(JSONUtil.toJsonStr(matchedMap.get("majorMation")),null);
                Map<String,Object>  classMation = JSONUtil.toBean(JSONUtil.toJsonStr(matchedMap.get("classMation")),null);
                map.put("schoolMation", schoolMation);
                map.put("facultyMation", facultyMation);
                map.put("majorMation", majorMation);
                map.put("classMation", classMation);
            }
        }
        return maps;
    }

    public List<Certification> getCertificationListByIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<Certification> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Certification::getUserId), ids);
        List<Certification> certificationList = list(queryWrapper);
        return certificationList;
    }

    @Override
    public void queryByUserId(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (id.equals(userId)) {
            Certification certification = selectById(userId);
            if (StrUtil.isNotEmpty(certification.getId()) && certification.getState() == StateEnum.CERTIFIEDSUCCESS.getKey()) {
                certification.setCheckCertification(true);
            } else {
                certification.setCheckCertification(false);
            }
            outputObject.setBean(certification);
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        } else {
            throw new CustomException("无权限");
        }
    }

    @Override
    public void validatorEntity(Certification certification) {
        String id = certification.getId();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String stuNo = certification.getStudentNumber();
        QueryWrapper<Certification> queryStuNo = new QueryWrapper<>();
        queryStuNo.eq(MybatisPlusUtil.toColumns(Certification::getStudentNumber), stuNo);
        long count = count(queryStuNo);
        if (StrUtil.isEmpty(id)) {
            if (count > CommonNumConstants.NUM_ZERO) {
                throw new CustomException("提交认证信息失败，学号已存在");
            }
            QueryWrapper<Certification> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Certification::getUserId), userId);
            Certification certificationFlag = getOne(queryWrapper);
            if (ObjectUtil.isNotEmpty(certificationFlag)) {
                throw new CustomException("认证信息已提交，不可重复提交");
            }
        } else {
            Certification certificationFlag = certificationService.selectById(id);
            if (!userId.equals(certificationFlag.getUserId())) {
                throw new CustomException("无权限，不可修改!!");
            }
            if (count > CommonNumConstants.NUM_ZERO && !stuNo.equals(certificationFlag.getStudentNumber())) {
                throw new CustomException("修改认证信息失败，学号已存在");
            }
            if (certificationFlag.getState() == StateEnum.CERTIFIEDING.getKey()) {
                throw new CustomException("管理员正在审核，暂时无法修改认证信息编辑");
            }
            if (certificationFlag.getState() == StateEnum.CERTIFIEDSUCCESS.getKey()) {
                throw new CustomException("已认证，不可修改");
            }
        }
    }

    @Override
    public void createPrepose(Certification certification) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        certification.setUserId(userId);
        certification.setState(StateEnum.CERTIFIEDING.getKey());
    }

    @Override
    public void updatePrepose(Certification certification) {
        certification.setState(StateEnum.CERTIFIEDING.getKey());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void reviewInformation(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Integer state = Integer.parseInt(inputObject.getParams().get("state").toString());
        UpdateWrapper<Certification> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Certification::getState), state);
        update(updateWrapper);
        if (state == StateEnum.CERTIFIEDSUCCESS.getKey()) {
            Certification certification = certificationService.selectById(id);
            userService.setCertification(certification.getUserId(), certification.getStudentNumber(), certification.getName());
            Map<String, Object> map = JSONUtil.toBean(JSONUtil.toJsonStr(certification), null);
            map.remove("id");
            map.put("no", certification.getStudentNumber());
            map.put("state", certification.getStatus());
            map.put("schoolId", certification.getCampus());
            iStudentService.addStudent(map);
        }
        refreshCache(id);
    }

    @Override
    public void queryUserByStudentNumber(InputObject inputObject, OutputObject outputObject) {
        String studentNumber = inputObject.getParams().get("studentNumber").toString();
        List<String> studentNumberList = Arrays.asList(studentNumber.split(CommonCharConstants.COMMA_MARK));
        // 查询学生认证信息
        QueryWrapper<Certification> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Certification::getStudentNumber), studentNumberList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Certification::getState), StateEnum.CERTIFIEDSUCCESS.getKey());
        List<Certification> certificationList = list(queryWrapper);
        if (CollectionUtil.isEmpty(certificationList)) {
            return;
        }
        // 查询用户信息
        List<String> userIdList = certificationList.stream().map(Certification::getUserId).distinct().collect(Collectors.toList());
        List<User> userList = userService.selectByIds(userIdList.toArray(new String[userIdList.size()]));
        outputObject.setBeans(userList);
        outputObject.settotal(userList.size());
    }
}
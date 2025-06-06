/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.common.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Joiner;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.student.entity.Student;
import com.skyeye.school.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: SchoolCommonServiceImpl
 * @Description:
 * @author: skyeye云系列--卫志强
 * @date: 2025/3/23 22:19
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
public class SchoolCommonServiceImpl implements SchoolCommonService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    protected IAuthUserService iAuthUserService;

    @Autowired
    private StudentService studentService;

    @Override
    public UserOrStudent queryUserOrStudent(String userId) {
        UserOrStudent item = new UserOrStudent();
        // 学生信息
        Map<String, Object> studentMation = iUserService.queryDataMationById(userId);
        if (CollectionUtil.isNotEmpty(studentMation)) {
            item.setUserOrStudent(true);
            studentMation.put("userIdentity", LoginIdentity.STUDENT.getKey());
            studentMation.put("password", StrUtil.EMPTY);
            String studentNumber = studentMation.getOrDefault("studentNumber", StrUtil.EMPTY).toString();
            if (StrUtil.isNotEmpty(studentNumber)) {
                Student students = studentService.getStudents(studentNumber);
                studentMation.put("studentMation", students);
            }
            item.setDataMation(studentMation);
            return item;
        }
        // 教师信息
        Map<String, Object> teacherMation = iAuthUserService.queryDataMationById(userId);
        if (CollectionUtil.isNotEmpty(teacherMation)) {
            item.setUserOrStudent(false);
            teacherMation.put("userIdentity", LoginIdentity.TEACHER.getKey());
            item.setDataMation(teacherMation);
        }
        return item;
    }

    @Override
    public List<UserOrStudent> queryUserOrStudentList(List<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        userIds = userIds.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyList();
        }
        Map<String, Map<String, Object>> studentMap = iUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(userIds));
        List<UserOrStudent> userOrStudentList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(studentMap)) {
            // 根据学号查询学生信息
            List<String> studentNumbers = studentMap.values().stream()
                .map(studentMation -> studentMation.getOrDefault("studentNumber", StrUtil.EMPTY).toString())
                .collect(Collectors.toList());
            List<Student> students = studentService.getStudents(studentNumbers);
            Map<String, Student> studentMapByNumber = students.stream().collect(Collectors.toMap(Student::getNo, student -> student));
            // 组装数据
            studentMap.forEach((id, studentMation) -> {
                UserOrStudent item = new UserOrStudent();
                item.setUserOrStudent(true);
                studentMation.put("userIdentity", LoginIdentity.STUDENT.getKey());
                studentMation.put("password", StrUtil.EMPTY);
                String studentNumber = studentMation.getOrDefault("studentNumber", StrUtil.EMPTY).toString();
                if (StrUtil.isNotEmpty(studentNumber)) {
                    Student student = studentMapByNumber.get(studentNumber);
                    studentMation.put("studentMation", student);
                }
                item.setDataMation(studentMation);
                userOrStudentList.add(item);
            });
        }
        List<String> notStudentIds = userIds.stream().filter(id -> !studentMap.containsKey(id)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(notStudentIds)) {
            return userOrStudentList;
        }
        // 根据用户id查询教师信息
        Map<String, Map<String, Object>> teacherMap = iAuthUserService.queryDataMationForMapByIds(Joiner.on(CommonCharConstants.COMMA_MARK).join(notStudentIds));
        if (CollectionUtil.isNotEmpty(teacherMap)) {
            teacherMap.forEach((id, teacherMation) -> {
                UserOrStudent item = new UserOrStudent();
                item.setUserOrStudent(false);
                teacherMation.put("userIdentity", LoginIdentity.TEACHER.getKey());
                item.setDataMation(teacherMation);
                userOrStudentList.add(item);
            });
        }

        return userOrStudentList;
    }

    @Override
    public Map<String, UserOrStudent> queryUserOrStudentMap(List<String> userIds) {
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        userIds = userIds.stream().filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(userIds)) {
            return Collections.emptyMap();
        }
        List<UserOrStudent> userOrStudentList = queryUserOrStudentList(userIds);
        if (CollectionUtil.isEmpty(userOrStudentList)) {
            return Collections.emptyMap();
        }
        return userOrStudentList.stream().collect(Collectors.toMap(item -> item.getDataMation().get("id").toString(), item -> item));
    }

    @Override
    public void checkUserCertification(Map<String, Object> certification) {
        if (!certification.containsKey("state")) {
            throw new CustomException("请先进行学生认证");
        }
        if (!certification.get("state").equals(CommonNumConstants.NUM_FOUR)) {
            throw new CustomException("认证信息未通过审核，不允许执行该操作");
        }
    }
}

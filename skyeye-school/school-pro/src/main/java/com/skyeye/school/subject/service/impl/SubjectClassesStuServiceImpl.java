/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.score.service.ScorePartService;
import com.skyeye.school.student.entity.Student;
import com.skyeye.school.student.service.StudentService;
import com.skyeye.school.subject.dao.SubjectClassesStuDao;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectClassesTopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SubjectClassesStuServiceImpl
 * @Description: 科目表与班级表关系下的学生信息服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/12 8:19
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "科目表与班级表关系下的学生信息", groupName = "科目管理")
public class SubjectClassesStuServiceImpl extends SkyeyeBusinessServiceImpl<SubjectClassesStuDao, SubjectClassesStu> implements SubjectClassesStuService {

    @Autowired
    private ICertificationService iCertificationService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private ICertificationRest iCertificationRest;

    @Autowired
    private SubjectClassesTopService subjectClassesTopService;

    @Autowired
    private ScorePartService scorePartService;

    @Autowired
    private StudentService studentService;

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void joinSubjectClasses(InputObject inputObject, OutputObject outputObject) {
        SubjectClassesStu subjectClassesStu = inputObject.getParams(SubjectClassesStu.class);
        // 获取科目表与班级表关系信息
        SubjectClasses subjectClasses = subjectClassesService.selectById(subjectClassesStu.getSubClassLinkId());
        if (StrUtil.isEmpty(subjectClasses.getId())) {
            throw new CustomException("该课程班级信息不存在");
        }
        if (subjectClasses.getEnabled() != EnableEnum.ENABLE_USING.getKey()) {
            throw new CustomException("该课程班级信息目前不允许加入");
        }
        // 获取认证信息
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Map<String, Object> map = iAuthUserService.queryDataMationById(userId);
        if (CollectionUtil.isNotEmpty(map)) {
            String jobNumber = map.get("jobNumber").toString();
            saveToClassStu(subjectClassesStu, jobNumber, true);
            return;
        }
        if (userId.equals(subjectClasses.getCreateId())) {
            throw new CustomException("您在这个课程里面，已经是老师/助教不能重复加入");
        }
        Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
        if (!certification.containsKey("state")) {
            throw new CustomException("请先进行学生认证");
        }
        if (!certification.get("state").equals(CommonNumConstants.NUM_FOUR)) {
            throw new CustomException("认证信息未通过审核，不允许加入课程班级");
        }
        String studentNumber = certification.get("studentNumber").toString();
        String classId = studentService.getStudents(studentNumber).getClassId();
        if (!classId.equals(subjectClasses.getClassesId())) {
            throw new CustomException("您不属于这个班级，不允许加入课程班级");
        }
        if (CollectionUtil.isNotEmpty(certification) && StrUtil.isNotEmpty(studentNumber)) {
            // 认证信息不为空，并且认证的学号信息存在
            saveToClassStu(subjectClassesStu, studentNumber, true);
        } else {
            throw new CustomException("认证信息为空，或者认证的学号信息不存在");
        }
    }

    @Override
    public void saveToClassStu(SubjectClassesStu subjectClassesStu, String studentNumber, boolean throwException) {
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subjectClassesStu.getSubClassLinkId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getStuNo), studentNumber);
        long count = count(queryWrapper);
        if (count > 0) {
            if (throwException) {
                throw new CustomException("该学生/老师已经加入该课程班级");
            }
            return;
        }
        subjectClassesStu.setStuNo(studentNumber);
        subjectClassesStu.setJoinTime(DateUtil.getTimeAndToString());
        createEntity(subjectClassesStu, StrUtil.EMPTY);
        subjectClassesService.editSubjectClassesPeopleNum(subjectClassesStu.getSubClassLinkId(), true);
    }

    @Override
    public void createPostpose(SubjectClassesStu subjectClassesStu, String UserId) {
        // 同步所有的成绩数据
        SubjectClasses subjectClasses = subjectClassesService.selectById(subjectClassesStu.getSubClassLinkId());
        scorePartService.midCourse(subjectClassesStu.getStuNo(), subjectClasses.getObjectId(), subjectClasses.getClassesId());
    }

    @Override
    public void deletePreExecution(SubjectClassesStu entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        // 获取认证信息
        Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
        if (CollectionUtil.isNotEmpty(certification) && StrUtil.equals(certification.get("studentNumber").toString(), entity.getStuNo())) {
            // 认证信息不为空，并且认证的学号信息是当前登录人的学号信息
            subjectClassesService.editSubjectClassesPeopleNum(entity.getSubClassLinkId(), false);
        } else {
            throw new CustomException("认证信息为空，或者认证的学号信息不是当前登录人的学号信息");
        }
    }

    @Override
    public void deletePostpose(SubjectClassesStu subjectClassesStu) {
        subjectClassesTopService.deleteSubjectClassesTopBySubClassLinkId(subjectClassesStu.getSubClassLinkId());
    }

    @Override
    public void deletePreExecution(String id) {
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        SubjectClassesStu subjectClassesStu = getOne(queryWrapper);
        SubjectClasses subjectClasses = subjectClassesService.selectById(subjectClassesStu.getSubClassLinkId());
        if (subjectClasses.getQuit() == CommonNumConstants.NUM_TWO) {
            throw new CustomException("对不起，现在不能退课");
        }
    }

    @Override
    public Long queruClassStuNum(String... subClassLinkId) {
        List<String> idList = Arrays.asList(subClassLinkId).stream()
            .filter(id -> StrUtil.isNotEmpty(id)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idList)) {
            return 0L;
        }
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), idList);
        return count(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryClassStuIds(String... subClassLinkId) {
        List<String> idList = Arrays.asList(subClassLinkId).stream()
            .filter(id -> StrUtil.isNotEmpty(id)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idList)) {
            return new ArrayList<>();
        }
        // 获取班级学生信息
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subClassLinkId);
        List<SubjectClassesStu> subjectClassesStuList = list(queryWrapper);
        if (CollectionUtil.isEmpty(subjectClassesStuList)) {
            return CollectionUtil.newArrayList();
        }
        // 获取学生信息
        List<String> stuNoList = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(stuNoList)) {
            return CollectionUtil.newArrayList();
        }
        List<Map<String, Object>> userList = ExecuteFeignClient.get(() ->
            iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
        if (CollectionUtil.isEmpty(userList)) {
            return CollectionUtil.newArrayList();
        }
        List<String> studentNumberList = userList.stream()
            .filter(user -> StrUtil.isNotEmpty(user.getOrDefault("studentNumber", StrUtil.EMPTY).toString()))
            .map(user -> user.get("studentNumber").toString()).distinct().collect(Collectors.toList());
        List<Student> students = studentService.getStudents(studentNumberList);
        Map<String, Student> collect = students.stream().collect(Collectors.toMap(Student::getNo, bb -> bb));
        userList.forEach(user -> {
            String studentNumber = user.getOrDefault("studentNumber", StrUtil.EMPTY).toString();
            if (StrUtil.isEmpty(studentNumber)) {
                return;
            }
            Student student = collect.get(studentNumber);
            user.put("studentMation", student);
        });
        return userList;
    }

    @Override
    public List<String> querySubClassLinkIdByStuNo(String stuNo) {
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getStuNo), stuNo);
        List<SubjectClassesStu> subjectClassesStuList = list(queryWrapper);
        return subjectClassesStuList.stream().map(SubjectClassesStu::getSubClassLinkId).distinct().collect(Collectors.toList());
    }

    @Override
    public void deleteBySno(String subClassLinkId, String sno) {
        UpdateWrapper<SubjectClassesStu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subClassLinkId);
        updateWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getStuNo), sno);
        remove(updateWrapper);
    }

    @Override
    public void deleteBySubClassLinkId(List<String> subClassLinkId) {
        UpdateWrapper<SubjectClassesStu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.in(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subClassLinkId);
        remove(updateWrapper);
    }

    @Override
    public void queryAllStudentById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectId = map.get("subjectId").toString();
        String subClassLinkId = map.get("subClassLinkId").toString();
        if (StrUtil.isEmpty(subjectId) && StrUtil.isEmpty(subClassLinkId)) {
            throw new CustomException("参数不能为空，请传入科目id或科目与班级的关联id");
        }
        List<Map<String, Object>> result = new ArrayList<>();
        if (StrUtil.isNotEmpty(subjectId)) {
            // 查询这个科目下的所有学生
            List<SubjectClasses> subjectClasses = subjectClassesService.querySubjectClassesByObjectId(subjectId);
            if (CollectionUtil.isEmpty(subjectClasses)) {
                return;
            }
            List<String> subClassLinkIds = subjectClasses.stream().map(SubjectClasses::getId).distinct().collect(Collectors.toList());
            result = queryClassStuIds(subClassLinkIds.toArray(new String[]{}));
        } else {
            if (StrUtil.isNotEmpty(subClassLinkId)) {
                // 查询这个班级下的所有学生
                result = queryClassStuIds(subClassLinkId);
            }
        }

        outputObject.setBeans(result);
        outputObject.settotal(result.size());
    }

    @Override
    public void queryStudentSubjectClassesBySubClassLinkIdAndStuNo(InputObject inputObject, OutputObject outputObject) {
        String subClassLinkId = inputObject.getParams().get("subClassLinkId").toString();
        String stuNo = inputObject.getParams().get("stuNo").toString();
        List<Map<String, Object>> maps = queryClassStuIds(subClassLinkId);
        if (CollectionUtil.isEmpty(maps)) {
            return;
        }
        for (Map<String, Object> map : maps) {
            if (map.get("studentNumber").equals(stuNo)) {
                outputObject.setBean(map);
                outputObject.settotal(CommonNumConstants.NUM_ONE);
            }
        }
    }

    @Override
    public void deleteUserById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subClassLinkId = map.get("subClassLinkId").toString();
        String sno = map.get("stuNo").toString();
        deleteBySno(subClassLinkId, sno);
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subClassLinkId);
        long count = count(queryWrapper);
        subjectClassesService.updatePeopleNum(subClassLinkId, (int) count);
    }

    @Override
    public void selectRewardList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subClassLinkId = map.get("subClassLinkId").toString();
        outputObject.setBean(selectReward(subClassLinkId));
        outputObject.settotal(selectReward(subClassLinkId).size());
    }

    @Override
    public List<SubjectClassesStu> selectReward(String subClassLinkId) {
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subClassLinkId);
        List<SubjectClassesStu> subjectClassesStuList = list(queryWrapper);
        if (CollectionUtil.isEmpty(subjectClassesStuList)) {
            return subjectClassesStuList;
        }
        List<Map<String, Object>> maps = queryClassStuIds(subClassLinkId);
        subjectClassesStuList.forEach(subjectClassesStu -> {
            for (Map<String, Object> map : maps) {
                if (map.get("studentNumber").equals(subjectClassesStu.getStuNo())) {
                    subjectClassesStu.setStuInfo(map);
                }
            }
        });
        return subjectClassesStuList;

    }

    @Override
    public void queryIdBysubClassLinkIdAndstuNo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subClassLinkId = map.get("subClassLinkId").toString();
        String stuNo = map.get("stuNo").toString();
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subClassLinkId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getStuNo), stuNo);
        outputObject.setBean(getOne(queryWrapper));
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void updateReward(String subClassesStuId, String Reward) {
        UpdateWrapper<SubjectClassesStu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subClassesStuId);
        SubjectClassesStu subjectClassesStu = selectById(subClassesStuId);
        if (StrUtil.isEmpty(subjectClassesStu.getReward())) {
            updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClassesStu::getReward), Reward);
            update(updateWrapper);
        } else {
            int oldReward = Integer.parseInt(subjectClassesStu.getReward());
            int newReward = oldReward + Integer.parseInt(Reward);
            updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClassesStu::getReward), String.valueOf(newReward));
            update(updateWrapper);
        }
    }

    @Override
    public void updateRewardNumberById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String stuNo = params.get("stuNo").toString();
        String reward = params.get("reward").toString();
        String subClassLinkId = params.get("subClassLinkId").toString();
        UpdateWrapper<SubjectClassesStu> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getStuNo), stuNo)
                .eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subClassLinkId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClassesStu::getReward), reward);
        SubjectClassesStu one = getOne(updateWrapper);
        Integer rewardNumber = Integer.parseInt(one.getReward()) + Integer.parseInt(reward);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClassesStu::getReward), String.valueOf(rewardNumber));
        update(updateWrapper);
    }

    @Override
    public void selectStudentList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subClassLinkId = map.get("subClassLinkId").toString();
        Integer groupCount = Integer.parseInt(map.get("groupCount").toString());
        List<Map<String, Object>> maps = queryClassStuIds(subClassLinkId);
        // 检查学生数量是否小于分组数量
        if (maps.size() < groupCount) {
            throw new CustomException("学生数量小于分组数量，无法进行分组");
        }
        // 进行随机分组
        List<List<Map<String, Object>>> groups = new ArrayList<>();
        Random random = new Random();
        for (int i = 0; i < groupCount; i++) {
            groups.add(new ArrayList<>());
        }

        while (!maps.isEmpty()) {
            int randomIndex = random.nextInt(groups.size());
            groups.get(randomIndex).add(maps.remove(0));
        }
        outputObject.setBeans(groups);
        outputObject.settotal(groups.size());
    }

    @Override
    public List<SubjectClassesStu> queryListBySubClassLinkId(String... subClassLinkId) {
        List<String> idList = Arrays.asList(subClassLinkId).stream()
            .filter(id -> StrUtil.isNotEmpty(id)).distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(idList)) {
            return new ArrayList<>();
        }
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), idList);
        return list(queryWrapper);
    }

    @Override
    public Long queryStuStarNum(String id, String studentNumber) {
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), id);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getStuNo), studentNumber);
        SubjectClassesStu one = getOne(queryWrapper);
        return Long.valueOf(one.getReward());
    }

    @Override
    public List<SubjectClassesStu> selectNumBySubClassLinkId(String subjectClassId) {
        QueryWrapper<SubjectClassesStu> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClassesStu::getSubClassLinkId), subjectClassId);
        List<SubjectClassesStu> subjectClassesStuList = list(queryWrapper);
        return subjectClassesStuList;
    }

}
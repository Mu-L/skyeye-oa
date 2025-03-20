/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.school.assignment.classenum.AssignmentTimeState;
import com.skyeye.school.assignment.dao.AssignmentDao;
import com.skyeye.school.assignment.entity.Assignment;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.assignment.service.AssignmentSubService;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AssignmentServiceImpl
 * @Description: 作业管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 10:46
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "作业管理", groupName = "作业管理")
public class AssignmentServiceImpl extends SkyeyeBusinessServiceImpl<AssignmentDao, Assignment> implements AssignmentService {

    @Autowired
    private AssignmentSubService assignmentSubService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private ChapterService chapterService;

    @Override
    public void validatorEntity(Assignment entity) {
        if (DateUtil.getDistanceDay(entity.getStartTime(), entity.getEndTime()) < 0) {
            // endTime < startTime
            throw new CustomException("结束时间不能早于开始时间");
        }
    }

    @Override
    public Assignment selectById(String id) {
        Assignment assignment = super.selectById(id);
        chapterService.setDataMation(assignment, Assignment::getChapterId);
        if (ObjectUtil.isNotEmpty(assignment.getChapterMation())) {
            assignment.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", assignment.getChapterMation().getSection(), assignment.getChapterMation().getName()));
        }
        iAuthUserService.setDataMation(assignment, Assignment::getCreateId);
        return assignment;
    }

    @Override
    public void queryAssignmentListBySubjectClassesId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectClassesId = map.get("subjectClassesId").toString();
        QueryWrapper<Assignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Assignment::getSubjectClassesId), subjectClassesId);
        List<Assignment> assignmentList = list(queryWrapper);
        if (CollectionUtil.isEmpty(assignmentList)) {
            return;
        }
        setTimeState(assignmentList);

        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        String userId = inputObject.getLogParams().get("id").toString();
        List<String> assignmentIdList = assignmentList.stream().map(Assignment::getId).collect(Collectors.toList());
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            // 教师身份信息
            // 设置需要提交作业的人数
            Long allStuNum = subjectClassesStuService.queruClassStuNum(subjectClassesId);
            // 设置总人数/已经提交作业/未提交作业的学生人数
            Map<String, Long> userSubMap = assignmentSubService.querySubResult(assignmentIdList.toArray(new String[]{}));
            assignmentList.forEach(assignment -> {
                Long subNum = userSubMap.get(assignment.getId());
                assignment.setNeedNum(allStuNum);
                assignment.setSubNum(subNum);
                assignment.setNoSubNum(allStuNum - subNum);
            });
            // 设置已经批改/未批改的作业的人数
            Map<String, Long> correctSubMap = assignmentSubService.querySubCorrectResult(assignmentIdList.toArray(new String[]{}));
            assignmentList.forEach(assignment -> {
                Long correctNum = correctSubMap.get(assignment.getId());
                assignment.setCorrectNum(correctNum);
                assignment.setNoCorrectNum(assignment.getSubNum() - correctNum);
            });
        } else {
            // 学生身份信息
            // 设置当前学生提交作业的状态
            Map<String, String> userSubMap = assignmentSubService.querySubResult(userId, assignmentIdList.toArray(new String[]{}));
            assignmentList.forEach(assignment -> {
                assignment.setSubState(userSubMap.get(assignment.getId()));
            });
        }

        chapterService.setDataMation(assignmentList, Assignment::getChapterId);
        assignmentList.forEach(assignment -> {
            if (ObjectUtil.isNotEmpty(assignment.getChapterMation())) {
                assignment.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", assignment.getChapterMation().getSection(), assignment.getChapterMation().getName()));
            }
        });
        iAuthUserService.setDataMation(assignmentList, Assignment::getCreateId);
        iAuthUserService.setDataMation(assignmentList, Assignment::getLastUpdateId);
        outputObject.setBeans(assignmentList);
        outputObject.settotal(assignmentList.size());
    }

    public void setTimeState(List<Assignment> assignmentList) {
        for (Assignment assignment : assignmentList) {
            String currentTime = DateUtil.getYmdTimeAndToString();
            if (DateUtil.getDistanceDay(assignment.getStartTime(), currentTime) >= 0 && DateUtil.getDistanceDay(currentTime, assignment.getEndTime()) >= 0) {
                // startTime <= 当前时间 <= endTime
                assignment.setTimeState(AssignmentTimeState.IN_PROGRESS.getKey());
            } else {
                assignment.setTimeState(AssignmentTimeState.EXPIRED.getKey());
            }
        }
    }

    @Override
    public Map<String, Double> queryAssigmentByChapterId(long num, String... ids) {
        Map<String, Double> map = new HashMap<>();
        double sumSize = 0;
        double finishRate = 0;
        map.put("activeNum", sumSize);
        map.put("finishRate", finishRate);
        for (String id :ids) {
            QueryWrapper<Assignment> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Assignment::getChapterId),id);
            List<Assignment> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            sumSize += list.size();
            List<String> assignmentIdList = list.stream().map(Assignment::getId).collect(Collectors.toList());
            double rate = assignmentSubService.queryAssignmentFinshRate(assignmentIdList, num);
            finishRate = finishRate + rate;
        }
        if(finishRate == 0 && ids.length > 1){
            finishRate = finishRate / ids.length;
        }
        map.put("finishRate", finishRate);
        return map;
    }

    // 查询科目班级id作业数量
    @Override
    public Long queryClassAssignmentNum(String id) {
        QueryWrapper<Assignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Assignment::getSubjectClassesId),id);
        return count(queryWrapper);
    }

    @Override
    public List<String> queryAssignmentIdsBySubjectCLassId(String id) {
        QueryWrapper<Assignment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Assignment::getSubjectClassesId),id);
        List<Assignment> list = list(queryWrapper);
        return list.stream().map(Assignment::getId).collect(Collectors.toList());
    }

}

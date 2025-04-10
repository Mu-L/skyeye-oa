/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.assignment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.assignment.classenum.AssignmentCorrectState;
import com.skyeye.school.assignment.classenum.AssignmentSubState;
import com.skyeye.school.assignment.dao.AssignmentSubDao;
import com.skyeye.school.assignment.entity.Assignment;
import com.skyeye.school.assignment.entity.AssignmentSub;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.assignment.service.AssignmentSubService;
import com.skyeye.school.score.service.ScorePartService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: AssignmentSubServiceImpl
 * @Description: 作业提交服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 11:10
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "作业提交", groupName = "作业管理")
public class AssignmentSubServiceImpl extends SkyeyeBusinessServiceImpl<AssignmentSubDao, AssignmentSub> implements AssignmentSubService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private ScorePartService scorePartService;

    @Override
    public void validatorEntity(AssignmentSub entity) {
        super.validatorEntity(entity);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), entity.getAssignmentId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssignmentSub::getCreateId), userId);
        if (StrUtil.isNotEmpty(entity.getId())) {
            queryWrapper.ne(CommonConstants.ID, entity.getId());
        }
        long count = count(queryWrapper);
        if (count != 0) {
            throw new CustomException("请勿重复提交作业");
        }
        // 校验时间
        Assignment assignment = assignmentService.selectById(entity.getAssignmentId());
        String currentTime = DateUtil.getYmdTimeAndToString();
        if (DateUtil.getDistanceDay(assignment.getStartTime(), currentTime) < 0 || DateUtil.getDistanceDay(currentTime, assignment.getEndTime()) < 0) {
            // startTime > 当前时间 || 当前时间 > endTime
            throw new CustomException("不在作业的提交时间范围");
        }
        entity.setState(AssignmentCorrectState.BE_CORRECTED.getKey());
    }

    @Override
    public AssignmentSub selectById(String id) {
        AssignmentSub assignmentSub = super.selectById(id);
        iUserService.setDataMation(assignmentSub, AssignmentSub::getCreateId);
        return assignmentSub;
    }

    @Override
    public void queryAssignmentSubListByAssignmentId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String assignmentId = map.get("assignmentId").toString();
        if (StrUtil.isEmpty(assignmentId)) {
            return;
        }
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份不允许查看所有的作业提交信息
            return;
        }
        List<AssignmentSub> assignmentSubList = querySubList(assignmentId);
        iUserService.setDataMation(assignmentSubList, AssignmentSub::getCreateId);
        outputObject.setBeans(assignmentSubList);
        outputObject.settotal(assignmentSubList.size());
    }

    private List<AssignmentSub> querySubList(String assignmentId) {
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), assignmentId);
        List<AssignmentSub> assignmentSubList = list(queryWrapper);
        return assignmentSubList;
    }

    @Override
    public void queryAssignmentNotSubListByAssignmentId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String assignmentId = map.get("assignmentId").toString();
        if (StrUtil.isEmpty(assignmentId)) {
            return;
        }
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份不允许查看所有的作业提交信息
            return;
        }
        // 查询作业信息
        Assignment assignment = assignmentService.selectById(assignmentId);
        // 查询所有学生信息
        List<Map<String, Object>> allUserList = subjectClassesStuService.queryClassStuIds(assignment.getSubjectClassesId());
        if (CollectionUtil.isEmpty(allUserList)) {
            return;
        }
        // 查询已提交的学生信息
        List<AssignmentSub> assignmentSubList = querySubList(assignmentId);
        List<String> subIdList = assignmentSubList.stream().map(AssignmentSub::getCreateId).distinct().collect(Collectors.toList());
        // 过滤已提交的学生信息
        List<Map<String, Object>> notSubUserList = allUserList.stream().filter(user -> !subIdList.contains(user.get("id").toString()))
            .collect(Collectors.toList());
        outputObject.setBeans(notSubUserList);
        outputObject.settotal(notSubUserList.size());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void readOverAssignmentSubById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        String score = map.get("score").toString();
        String comment = map.get("comment").toString();
        UpdateWrapper<AssignmentSub> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(AssignmentSub::getScore), score);
        updateWrapper.set(MybatisPlusUtil.toColumns(AssignmentSub::getState), AssignmentCorrectState.CORRECTED.getKey());
        updateWrapper.set(MybatisPlusUtil.toColumns(AssignmentSub::getComment), comment);
        update(updateWrapper);
        AssignmentSub one = getOne(updateWrapper);
        List<Map<String, Object>> userList = iUserService.queryEntityMationByIds(one.getCreateId());
        scorePartService.updateScorePartByStuNoAndWorkId(userList.get(CommonNumConstants.NUM_ZERO).get("studentNumber").toString(), one.getAssignmentId(), score);
        refreshCache(id);
    }

    @Override
    public Map<String, Long> querySubResult(String... assignmentId) {
        List<String> assignmentIdList = Arrays.asList(assignmentId);
        if (CollectionUtil.isEmpty(assignmentIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), assignmentIdList);
        List<AssignmentSub> assignmentSubList = list(queryWrapper);

        Map<String, Long> subNumMap = assignmentSubList.stream().collect(
            Collectors.groupingBy(AssignmentSub::getAssignmentId, Collectors.counting()));
        // 和数据库的已提交的作业做对比
        Map<String, Long> result = new HashMap<>();
        assignmentIdList.forEach(assignmentTmpId -> {
            if (subNumMap.containsKey(assignmentTmpId)) {
                result.put(assignmentTmpId, subNumMap.get(assignmentTmpId));
            } else {
                result.put(assignmentTmpId, Long.valueOf(0));
            }
        });
        return result;
    }

    @Override
    public Map<String, Long> querySubCorrectResult(String... assignmentId) {
        List<String> assignmentIdList = Arrays.asList(assignmentId);
        if (CollectionUtil.isEmpty(assignmentIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), assignmentIdList);
        // 已批改
        queryWrapper.in(MybatisPlusUtil.toColumns(AssignmentSub::getState), AssignmentCorrectState.CORRECTED.getKey());
        List<AssignmentSub> assignmentSubList = list(queryWrapper);

        Map<String, Long> correctNumMap = assignmentSubList.stream().collect(
            Collectors.groupingBy(AssignmentSub::getAssignmentId, Collectors.counting()));
        // 和数据库的已批改的作业做对比
        Map<String, Long> result = new HashMap<>();
        assignmentIdList.forEach(assignmentTmpId -> {
            if (correctNumMap.containsKey(assignmentTmpId)) {
                result.put(assignmentTmpId, correctNumMap.get(assignmentTmpId));
            } else {
                result.put(assignmentTmpId, Long.valueOf(0));
            }
        });
        return result;
    }

    @Override
    public Map<String, String> querySubResultByUserId(String userId, String... assignmentId) {
        List<String> assignmentIdList = Arrays.asList(assignmentId);
        if (CollectionUtil.isEmpty(assignmentIdList)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), assignmentIdList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssignmentSub::getCreateId), userId);
        List<AssignmentSub> assignmentSubList = list(queryWrapper);
        List<String> sqlAssignmentIdList = assignmentSubList.stream().map(AssignmentSub::getAssignmentId)
            .distinct().collect(Collectors.toList());
        // 和数据库的已提交的作业做对比
        Map<String, String> result = new HashMap<>();
        assignmentIdList.forEach(assignmentTmpId -> {
            if (sqlAssignmentIdList.indexOf(assignmentTmpId) >= 0) {
                result.put(assignmentTmpId, AssignmentSubState.SUBMITTED.getKey());
            } else {
                result.put(assignmentTmpId, AssignmentSubState.NOT_SUBMITTED.getKey());
            }
        });
        return result;
    }

    @Override
    public void queryAssignmentStuSubListByAssignmentId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String assignmentId = map.get("assignmentId").toString();
        // 查询当前登录人的作业提交信息
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), assignmentId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(AssignmentSub::getCreateId), userId);
        AssignmentSub assignmentSub = getOne(queryWrapper, false);
        if (ObjectUtil.isNotEmpty(assignmentSub) && StrUtil.isNotEmpty(assignmentSub.getId())) {
            builderByHandler(assignmentSub);
        }
        outputObject.setBean(assignmentSub);
        outputObject.settotal(ObjectUtil.isEmpty(assignmentSub) ? CommonNumConstants.NUM_ZERO : CommonNumConstants.NUM_ONE);
    }

    // 获取作业参数人数
    @Override
    public Long queryClassAssignmentJoinNum(String id) {
        Long sum = 0L;
        // 获取作业id
        List<String> ids = assignmentService.queryAssignmentIdsBySubjectCLassId(id);
        if (CollectionUtil.isEmpty(ids)) {
            return sum;
        }
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), ids);
        sum = count(queryWrapper);
        return sum;
    }

    @Override
    public List<AssignmentSub> queryAssSubByAssignmentIds(List<String> assIds) {
        QueryWrapper<AssignmentSub> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(AssignmentSub::getAssignmentId), assIds);
        List<AssignmentSub> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyList();
        }
        return list;
    }

}

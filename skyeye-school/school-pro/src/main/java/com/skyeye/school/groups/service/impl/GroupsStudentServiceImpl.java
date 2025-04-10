/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.groups.dao.GroupsStudentDao;
import com.skyeye.school.groups.entity.Groups;
import com.skyeye.school.groups.entity.GroupsInformation;
import com.skyeye.school.groups.entity.GroupsStudent;
import com.skyeye.school.groups.service.GroupsInformationService;
import com.skyeye.school.groups.service.GroupsService;
import com.skyeye.school.groups.service.GroupsStudentService;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: GroupsStudentServiceImpl
 * @Description: 学生与分组的关系管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/10 9:45
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "学生与分组的关系管理", groupName = "分组管理")
public class GroupsStudentServiceImpl extends SkyeyeBusinessServiceImpl<GroupsStudentDao, GroupsStudent> implements GroupsStudentService {

    @Autowired
    private GroupsInformationService groupsInformationService;

    @Autowired
    private GroupsService groupsService;

    @Autowired
    private ICertificationService iCertificationService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private ICertificationRest iCertificationRest;

    @Autowired
    private SchoolCommonService schoolCommonService;

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void joinGroups(InputObject inputObject, OutputObject outputObject) {
        GroupsStudent groupsStudent = inputObject.getParams(GroupsStudent.class);
        String groupId = groupsStudent.getGroupId();
        GroupsInformation groupsInformation = getGroupsInformation(groupId);
        if (ObjectUtil.isEmpty(groupsInformation)) {
            throw new CustomException("分组信息不存在");
        }
        String studentNumber1 = groupsStudent.getStudentNumber();
        // 判断学生是否在该班级范围内
        List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.selectNumBySubClassLinkId(groupsInformation.getSubjectClassId());
        List<String> collect = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
        boolean isExist = collect.contains(studentNumber1);
        if (isExist) {
            if (groupsInformation.getStatus().equals(CommonNumConstants.NUM_ONE)) {
                if (getExitCount(groupId) >= groupsInformation.getGroupsNum()) {
                    // 按照人数分组时，判断分组人数是否已满，已满则不能再加入
                    throw new CustomException("分组人数已满");
                }
            }
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
            String studentNumber = certification.get("studentNumber").toString();
            saveToGroupsStudent(groupsStudent, studentNumber, true);
        } else {
            throw new CustomException("学生不在该班级范围内");
        }
    }

    /**
     * 根据分组id获取该分组的学生数量
     *
     * @param groupId
     * @return
     */
    private long getExitCount(String groupId) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupId);
        return count(queryWrapper);
    }

    @NotNull
    public GroupsInformation getGroupsInformation(String groupId) {
        Groups groups1 = groupsService.selectById(groupId);
        String groupsInformationId = groups1.getGroupsInformationId();
        GroupsInformation groupsInformation = groupsInformationService.selectById(groupsInformationId);
        return groupsInformation;
    }

    public void saveToGroupsStudent(GroupsStudent groupsStudent, String studentNumber, boolean throwException) {
        Groups groups = groupsService.selectById(groupsStudent.getGroupId());
        if (groups.getState().equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该分组已解散");
        }

        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), studentNumber);
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupsStudent.getGroupId());
        long count = count(queryWrapper);
        if (count > 0) {
            if (throwException) {
                throw new CustomException("学生已加入该分组");
            }
            return;
        }

        groupsStudent.setCreateTime(DateUtil.getTimeAndToString());
        createEntity(groupsStudent, StrUtil.EMPTY);
        GroupsInformation groupsInformation = getGroupsInformation(groupsStudent.getGroupId());
        groupsInformationService.editGroupsInformationStuNum(groupsInformation.getId(), true);
    }

    @Override
    protected void deletePreExecution(GroupsStudent entity) {
        GroupsInformation groupsInformation = getGroupsInformation(entity.getGroupId());
        groupsInformationService.editGroupsInformationStuNum(groupsInformation.getId(), false);
    }

    @Override
    public Map<String, Boolean> checkStudentIsJoined(List<String> groupsIds, String studentNumber) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), studentNumber);
        queryWrapper.in(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupsIds);
        List<GroupsStudent> groupsStudentList = list(queryWrapper);
        List<String> collect = groupsStudentList.stream().map(GroupsStudent::getGroupId).collect(Collectors.toList());
        Map<String, Boolean> map = new HashMap<>();
        if (collect.size() > 0) {
            for (String groupId : groupsIds) {
                map.put(groupId, collect.contains(groupId));
            }
            return map;
        }
        groupsIds.forEach(groupId -> {
            map.put(groupId, false);
        });
        return map;
    }

    @Override
    public void deleteByGroupsIds(List<String> groupsIds) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupsIds);
        remove(queryWrapper);
    }

    @Override
    public List<Map<String, Object>> queryGroupsStudentsByGroupId(String groupId) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupId);
        List<GroupsStudent> groupsStudentList = list(queryWrapper);
        List<String> studentNumberList = groupsStudentList.stream().map(GroupsStudent::getStudentNumber).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(studentNumberList)) {
            return new ArrayList<>();
        }
        List<Map<String, Object>> userList = ExecuteFeignClient.get(() ->
            iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(studentNumberList))).getRows();
        return userList;
    }

    @Override
    public Map<String, Integer> getStudentCountByGroupId(List<String> groupsIds) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupsIds);
        List<GroupsStudent> groupsStudentList = list(queryWrapper);
        Map<String, Integer> map = new HashMap<>();
        for (String groupId : groupsIds) {
            int count = (int) groupsStudentList.stream().filter(groupsStudent -> groupsStudent.getGroupId().equals(groupId)).count();
            map.put(groupId, count);
        }
        return map;
    }

    @Override
    public void exitGroups(InputObject inputObject, OutputObject outputObject) {
        String groupsId = inputObject.getParams().get("groupsId").toString();

        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
        schoolCommonService.checkUserCertification(certification);
        String studentNumber = certification.get("studentNumber").toString();

        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), studentNumber);
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupsId);
        GroupsStudent groupsStudent = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(groupsStudent)) {
            throw new CustomException("学生不在该分组中");
        }
        deleteById(groupsStudent.getId());
    }

}

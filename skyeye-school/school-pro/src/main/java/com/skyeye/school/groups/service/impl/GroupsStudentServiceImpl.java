/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.service.ICertificationService;
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
        List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.selectNumBySubClassLinkId(groupsInformation.getSubjectClassId());
        List<String> collect = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
        boolean isExist = collect.contains(studentNumber1);
        if (isExist) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
            String studentNumber = certification.get("studentNumber").toString();
            saveToGroupsStudent(groupsStudent, studentNumber, true);
        } else {
            throw new CustomException("学生不在该分组范围内");
        }
    }

    @NotNull
    public GroupsInformation getGroupsInformation(String groupId) {
        Groups groups1 = groupsService.selectById(groupId);
        String groupsInformationId = groups1.getGroupsInformationId();
        GroupsInformation groupsInformation = groupsInformationService.selectById(groupsInformationId);
        return groupsInformation;
    }

    @Override
    public void selectGroupsByStuNumber(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String stuNumber = map.get("studentNumber").toString();
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), stuNumber);
        GroupsStudent groupsStudent = this.getOne(queryWrapper);
        Groups groups = groupsService.selectById(groupsStudent.getGroupId());
        groupsStudent.setGroupsMation(groups);
        outputObject.setBean(groupsStudent);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
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

}
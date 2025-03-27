package com.skyeye.school.groups.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
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
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    private SubjectClassesService subjectClassesService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Override
    public void joinGroups(InputObject inputObject, OutputObject outputObject) {
        GroupsStudent groupsStudent = inputObject.getParams(GroupsStudent.class);
        String groupId = groupsStudent.getGroupId();
        if (StrUtil.isEmpty(groupId)) {
            throw new CustomException("分组不存在");
        }
        String studentNumber1 = groupsStudent.getStudentNumber();
        GroupsInformation groupsInformation = getGroupsInformation(groupId);
        String subjectId = groupsInformation.getSubjectId();
        String classId = groupsInformation.getClassId();
        boolean isExist = false;
        if (StrUtil.isNotEmpty(subjectId)) {
            List<SubjectClasses> subjectClassesList = subjectClassesService.selectIdBySubJectId(subjectId);
            isExist = isaBoolean(subjectClassesList, isExist, studentNumber1);
        }
        if (StrUtil.isNotEmpty(classId)) {
            List<SubjectClasses> subjectClassesList = subjectClassesService.selectIdByClassId(groupsInformation.getSubjectId());
            isExist = isaBoolean(subjectClassesList, isExist, studentNumber1);
        }
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

    private boolean isaBoolean(List<SubjectClasses> subjectClassesList, boolean isExist, String studentNumber1) {
        List<String> collect = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        List<SubjectClassesStu> allStudents = collect.stream()
            .map(id1 -> subjectClassesStuService.queryListBySubClassLinkId(id1))
            .flatMap(List::stream).collect(Collectors.toList());
        List<String> collect1 = allStudents.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
        isExist = collect1.contains(studentNumber1);
        return isExist;
    }

    @Override
    public void selectGroupsByStuNumber(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String stuNumber = map.get("studentNumber").toString();
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), stuNumber);
        GroupsStudent groupsStudent = getOne(queryWrapper);
        Groups groups = groupsService.selectById(groupsStudent.getGroupId());
        groupsStudent.setGroupsMation(groups);
        outputObject.setBean(groupsStudent);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public List<Map<String, Object>> selectAllStudent() {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        List<GroupsStudent> groupsStudentList = list(queryWrapper);
        List<Map<String, Object>> list = JSONUtil.toList(JSONUtil.toJsonStr(groupsStudentList), null);
        return list;
    }

    public void saveToGroupsStudent(GroupsStudent groupsStudent, String studentNumber, boolean throwException) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), studentNumber);
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupsStudent.getGroupId());
        Groups groups = groupsService.selectById(groupsStudent.getGroupId());
        if (groups.getState().equals(CommonNumConstants.NUM_ONE)) {
            throw new CustomException("该分组已解散");
        }
        long count = count(queryWrapper);
        if (count > 0) {
            if (throwException) {
                throw new CustomException("学生已加入该分组");
            }
            return;
        }
        groupsStudent.setStudentNumber(studentNumber);
        groupsStudent.setCreateTime(DateUtil.getTimeAndToString());
        groupsStudent.setGroupId(groupsStudent.getGroupId());
        createEntity(groupsStudent, StrUtil.EMPTY);
        GroupsInformation groupsInformation = getGroupsInformation(groupsStudent.getGroupId());
        groupsInformationService.editGroupsInformationStuNum(groupsInformation.getId(), true);
    }

    @Override
    protected void deletePreExecution(GroupsStudent entity) {
        GroupsInformation groupsInformation = getGroupsInformation(entity.getGroupId());
        groupsInformationService.editGroupsInformationStuNum(groupsInformation.getId(), false);
    }

}

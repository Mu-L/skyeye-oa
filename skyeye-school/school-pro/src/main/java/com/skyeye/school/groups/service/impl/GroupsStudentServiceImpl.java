package com.skyeye.school.groups.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.service.ICertificationService;
import com.skyeye.school.groups.dao.GroupsStudentDao;
import com.skyeye.school.groups.entity.Groups;
import com.skyeye.school.groups.entity.GroupsStudent;
import com.skyeye.school.groups.service.GroupsInformationService;
import com.skyeye.school.groups.service.GroupsService;
import com.skyeye.school.groups.service.GroupsStudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "学生与分组的关系管理", groupName = "分组管理")
public class GroupsStudentServiceImpl extends SkyeyeBusinessServiceImpl<GroupsStudentDao, GroupsStudent> implements GroupsStudentService {

    @Autowired
    private GroupsInformationService groupsInformationService;

    @Autowired
    private GroupsService groupsService;

    @Autowired
    private ICertificationService iCertificationService;

    @Override
//    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void joinGroups(InputObject inputObject, OutputObject outputObject) {
        GroupsStudent groupsStudent = inputObject.getParams(GroupsStudent.class);
        Groups groups = groupsService.selectById(groupsStudent.getGroupId());
        if (StrUtil.isEmpty(groups.getId())) {
            throw new CustomException("分组不存在");
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Map<String, Object> certification = iCertificationService.queryCertificationById(userId);
        String studentNumber = certification.get("studentNumber").toString();
        saveToGroupsStudent(groupsStudent, studentNumber, true);
    }

    @Override
    public void selectGroupsByStuNumber(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String stuNumber = map.get("studentNumber").toString();
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), stuNumber);
        List<GroupsStudent> list = list(queryWrapper);
        outputObject.setBean(list);
        outputObject.settotal(list.size());
    }

    public void saveToGroupsStudent(GroupsStudent groupsStudent, String studentNumber, boolean throwException) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getStudentNumber), studentNumber);
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), groupsStudent.getGroupId());
        Groups groups = groupsService.selectById(groupsStudent.getGroupId());
        if (groups.getState().equals("已解散")) {
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
    }

    @Override
    public void deleteGroupsStudent(String GroupsId) {
        QueryWrapper<GroupsStudent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsStudent::getGroupId), GroupsId);
        remove(queryWrapper);
    }


}

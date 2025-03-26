package com.skyeye.school.groups.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.groups.dao.GroupsInformationDao;
import com.skyeye.school.groups.entity.Groups;
import com.skyeye.school.groups.entity.GroupsInformation;
import com.skyeye.school.groups.service.GroupsInformationService;
import com.skyeye.school.groups.service.GroupsService;
import com.skyeye.school.groups.service.GroupsStudentService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.impl.SubjectClassesServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "学生分组信息管理", groupName = "分组管理")
public class GroupsInformationServiceImpl extends SkyeyeBusinessServiceImpl<GroupsInformationDao, GroupsInformation> implements GroupsInformationService {

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private GroupsService groupsService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private ClassesService classesService;

    private static Logger LOGGER = LoggerFactory.getLogger(SubjectClassesServiceImpl.class);

    @Override
    public QueryWrapper<GroupsInformation> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<GroupsInformation> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsInformation::getSubjectId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsInformation::getClassId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Autowired
    private GroupsStudentService groupsStudentService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        List<Map<String, Object>> groupsStudents = groupsStudentService.selectAllStudent();
        Map<String, List<Map<String, Object>>> studentMap = groupsStudents.stream()
                .collect(Collectors.groupingBy(student -> student.get("groupId").toString()));
        for (Map<String, Object> bean : beans) {
            String id = bean.get("id").toString();
            List<Groups> groupsList = groupsService.selectByGroupsInformationId(id);
            bean.put("groupsList", groupsList);
            List<String> groupIds = groupsList.stream().map(Groups::getId).collect(Collectors.toList());
            List<Map<String, Object>> studentMation = groupIds.stream()
                    .filter(studentMap::containsKey)
                    .flatMap(groupId -> studentMap.get(groupId).stream())
                    .collect(Collectors.toList());
            bean.put("studentMation", studentMation);
        }
        iAuthUserService.setMationForMap(beans, "createId", "createMation");
        classesService.setMationForMap(beans, "classId", "classMation");
        return beans;
    }

    @Override
    protected void createPostpose(GroupsInformation groupsInformation, String userId) {
        List<SubjectClassesStu> subjectClassesStuList = new ArrayList<>();
        String classId = groupsInformation.getClassId();
        if (StrUtil.isNotEmpty(classId)) {
            List<SubjectClasses> subjectClassesList1 = subjectClassesService.selectIdByClassId(classId);
            List<String> collect = subjectClassesList1.stream().map(SubjectClasses::getId).collect(Collectors.toList());
            List<SubjectClassesStu> collect1 = collect.stream()
                    .map(id2 -> subjectClassesStuService.queryListBySubClassLinkId(id2))
                    .flatMap(List::stream).collect(Collectors.toList());//获取所有班级下的学生
            subjectClassesStuList.addAll(collect1);
        }
        String subjectId = groupsInformation.getSubjectId();
        if (StrUtil.isNotEmpty(subjectId)) {
            List<SubjectClasses> subjectClassesList = subjectClassesService.selectIdBySubJectId(groupsInformation.getSubjectId());
            List<String> collect = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
            List<SubjectClassesStu> allStudents = collect.stream()
                    .map(id1 -> subjectClassesStuService.queryListBySubClassLinkId(id1))
                    .flatMap(List::stream).collect(Collectors.toList());//获取所有科目下的学生
            subjectClassesStuList.addAll(allStudents);
        }
        Integer status = groupsInformation.getStatus();
        Integer groNumber = groupsInformation.getGroNumber();
        if (status.equals(CommonNumConstants.NUM_ZERO)) {
            if (subjectClassesStuList.size() < groNumber) {
                throw new CustomException("学生人数不足,无法创建分组");
            }
            groupsService.insertList(groupsInformation, subjectClassesStuList);
        }
        if (status.equals(CommonNumConstants.NUM_ONE)) {
            int size = subjectClassesStuList.size();
            Integer groupsnun = groupsInformation.getGroupsNum();
            if (groupsnun == null) {
                throw new CustomException("分组数量未设置");
            }
            int num;
            int numGroups;
            if (size > groupsnun) {
                num = size % groupsnun;
                if (num != 0) {
                    numGroups = size / groupsnun + 1;
                } else {
                    numGroups = size / groupsnun;
                }
                UpdateWrapper<GroupsInformation> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, groupsInformation.getId());
                updateWrapper.set(MybatisPlusUtil.toColumns(GroupsInformation::getGroupsNumber), numGroups);
                update(updateWrapper);
            } else {
                throw new CustomException("学生人数不足,无法创建分组");
            }
            GroupsInformation groupsInformation1 = selectById(groupsInformation.getId());
            groupsService.insertList(groupsInformation1, subjectClassesStuList);
        }
    }

    @Override
    public void deletePreExecution(String id) {
        groupsService.deleteGroups(id);
    }

    @Override
    public void editGroupsInformationStuNum(String id, Boolean isAdd) {
        String lockKey = String.format("editGroupsInformationStuNum_/%s", id);
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                throw new CustomException("操作频繁，请稍后再试");
            }
            LOGGER.info("get lock success, lockKey is {}.", lockKey);
            GroupsInformation groupsInformation = selectById(id);
            if (isAdd) {
                //新增
                UpdateWrapper<GroupsInformation> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(GroupsInformation::getJoinGroupsStu), groupsInformation.getJoinGroupsStu() + CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            } else {
                //删除
                UpdateWrapper<GroupsInformation> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(GroupsInformation::getJoinGroupsStu), groupsInformation.getJoinGroupsStu() - CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            }
            refreshCache(id);
            LOGGER.info("editGroupsInformationStuNum is success.");
        } catch (Exception ee) {
            LOGGER.warn("editGroupsInformationStuNum error, because {}", ee);
            if (ee instanceof CustomException) {
                throw new CustomException(ee.getMessage());
            }
            throw new RuntimeException(ee.getMessage());
        } finally {
            lock.unlock();
        }

    }

}

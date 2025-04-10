/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.groups.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.school.groups.dao.GroupsInformationDao;
import com.skyeye.school.groups.entity.GroupsInformation;
import com.skyeye.school.groups.service.GroupsInformationService;
import com.skyeye.school.groups.service.GroupsService;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.impl.SubjectClassesServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @ClassName: GroupsInformationServiceImpl
 * @Description: 学生分组信息管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2025/4/10 9:07
 * @Copyright: 2025 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "学生分组信息管理", groupName = "分组管理")
public class GroupsInformationServiceImpl extends SkyeyeBusinessServiceImpl<GroupsInformationDao, GroupsInformation> implements GroupsInformationService {

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private GroupsService groupsService;

    private static Logger LOGGER = LoggerFactory.getLogger(GroupsInformationServiceImpl.class);

    @Override
    public QueryWrapper<GroupsInformation> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<GroupsInformation> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.isEmpty(commonPageInfo.getHolderId())) {
            throw new CustomException("请先选择科目");
        }
        if (StrUtil.equals(userIdentity, LoginIdentity.TEACHER.getKey())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsInformation::getSubjectId), commonPageInfo.getHolderId());
        }
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsInformation::getSubjectClassId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    protected void createPrepose(GroupsInformation groupsInformation) {
        String subjectClassId = groupsInformation.getSubjectClassId();
        if (StrUtil.isEmpty(subjectClassId)) {
            throw new CustomException("请先选择班级");
        }
        List<SubjectClassesStu> allStuNum = subjectClassesStuService.selectNumBySubClassLinkId(subjectClassId);
        int size = allStuNum.size();
        Integer status = groupsInformation.getStatus();
        Integer groNumber = groupsInformation.getGroNumber();
        if (status.equals(CommonNumConstants.NUM_ZERO)) {
            if (size < groNumber) {
                throw new CustomException("学生人数不足,无法创建分组");
            }
            groupsService.insertList(groupsInformation);
        }
        if (status.equals(CommonNumConstants.NUM_ONE)) {
            Integer groupsnun = groupsInformation.getGroupsNum();
            if (groupsnun == null) {
                throw new CustomException("分组数量未设置");
            }
        }
    }

    @Override
    protected void createPostpose(GroupsInformation groupsInformation, String userId) {
        if (groupsInformation.getStatus().equals(CommonNumConstants.NUM_ONE)) {
            List<SubjectClassesStu> allStuNum = subjectClassesStuService.selectNumBySubClassLinkId(groupsInformation.getSubjectClassId());
            int size = allStuNum.size();
            Integer groupsnun = groupsInformation.getGroupsNum();
            int num;
            int numGroups;
            if (size >= groupsnun) {
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
            groupsService.insertList(groupsInformation1);
        }
    }

    @Override
    public void deletePreExecution(String id) {
        groupsService.deleteGroups(id);
    }

    @Override
    public void editGroupsInformationStuNum(String id, Boolean isAdd) {
        String lockKey = String.format("editGroupsInformationStuNum_%s", id);
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

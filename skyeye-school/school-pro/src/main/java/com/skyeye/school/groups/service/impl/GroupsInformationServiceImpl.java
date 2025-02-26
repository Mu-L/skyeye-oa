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
import com.skyeye.eve.service.IAuthUserService;
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
import java.util.Map;

@Service
@SkyeyeService(name = "学生分组信息管理", groupName = "分组管理")
public class GroupsInformationServiceImpl extends SkyeyeBusinessServiceImpl<GroupsInformationDao, GroupsInformation> implements GroupsInformationService {


    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private GroupsService groupsService;

    @Autowired
    private IAuthUserService iAuthUserService;

    private static Logger LOGGER = LoggerFactory.getLogger(SubjectClassesServiceImpl.class);


    @Override
    public QueryWrapper<GroupsInformation> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<GroupsInformation> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(GroupsInformation::getSubClassLinkId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setMationForMap(beans, "createId", "createMation");
        return beans;
    }

    @Override
    public void createPostpose(GroupsInformation groupsInformation, String id) {
        Integer status = groupsInformation.getStatus();
        if (status.equals(CommonNumConstants.NUM_ZERO)) {
            groupsService.insertList(groupsInformation);
        }
        if (status.equals(CommonNumConstants.NUM_ONE)) {
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkId(groupsInformation.getSubClassLinkId());
            int size = subjectClassesStuList.size();
            Integer groupsnun = groupsInformation.getGroupsNum();
            if (size < groupsnun) {
                throw new RuntimeException("学生人数不足,无法创建分组");
            } else {
                Integer num = size % groupsnun;
                if (num != 0) {
                    Integer num1 = size / groupsnun + 1;
                    groupsInformation.setGroupsNumber(num1);//放不进去
                } else {
                    int num2 = size / groupsnun;
                    groupsInformation.setGroupsNumber(num2);
                }
            }
            groupsService.insertList(groupsInformation);
        }
    }

    @Override
    public void deletePreExecution(String id) {
        QueryWrapper<GroupsInformation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
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
            }
            else {
                //删除
                UpdateWrapper<GroupsInformation> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(GroupsInformation::getJoinGroupsStu), groupsInformation.getJoinGroupsStu() - CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            }
            refreshCache(id);
            LOGGER.info("editGroupsInformationStuNum is success.");
        }
        catch (Exception ee) {
            LOGGER.warn("editGroupsInformationStuNum error, because {}", ee);
            if(ee instanceof CustomException) {
                throw new CustomException(ee.getMessage());
            }
            throw new RuntimeException(ee.getMessage());
        }
        finally {
            lock.unlock();
        }

    }
}

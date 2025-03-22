package com.skyeye.school.chat.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.chat.dao.FriendRelationshipDao;
import com.skyeye.school.chat.entity.FriendRelationship;
import com.skyeye.school.chat.service.FriendRelationshipService;
import com.skyeye.school.personnel.entity.SysEveUserStaff;
import com.skyeye.school.personnel.service.SysEveUserStaffService;
import com.skyeye.school.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "好友关系", groupName = "好友关系")
public class FriendRelationshipServiceImpl extends SkyeyeBusinessServiceImpl<FriendRelationshipDao, FriendRelationship> implements FriendRelationshipService {

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private SysEveUserStaffService sysEveUserStaffService;

    @Autowired
    private StudentService studentService;

    @Autowired
    private IUserService iUserService;

    @Override
    protected QueryWrapper<FriendRelationship> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<FriendRelationship> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(FriendRelationship::getStatus), commonPageInfo.getState());
        }
        return queryWrapper;
    }

    @Override
    protected List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setMationForMap(beans, "createId", "createName");
        return beans;
    }

    @Override
    public void addFriendRelationship(String id, String applicantId, String recipientId, Integer status, String createId) {
        FriendRelationship friendRelationship = new FriendRelationship();
        friendRelationship.setUserId(applicantId);
        friendRelationship.setFriendId(recipientId);
        friendRelationship.setStatus(status);
        friendRelationship.setTalkRequestId(id);
        createEntity(friendRelationship, createId);
    }

    @Override
    public void changeFriendStatus(String userId, String status) {
        UpdateWrapper<FriendRelationship> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(MybatisPlusUtil.toColumns(FriendRelationship::getTalkRequestId), userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(FriendRelationship::getStatus), status);
        update(updateWrapper);
    }

    @Override
    public void queryNoPageFriendsList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        QueryWrapper<FriendRelationship> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(FriendRelationship::getCreateTime));
        queryWrapper.eq(MybatisPlusUtil.toColumns(FriendRelationship::getStatus), CommonNumConstants.NUM_ONE);
        queryWrapper.and(wrapper -> wrapper
                .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), id)
                .or()
                .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), id));
        List<FriendRelationship> list = list(queryWrapper);
        for (FriendRelationship item : list) {
            String remainingId;
            if (item.getUserId().equals(id)) {
                remainingId = item.getFriendId();
            } else {
                remainingId = item.getUserId();
            }
            List<Map<String, Object>> studentMationList = iUserService.queryEntityMationByIds(remainingId);
            SysEveUserStaff teacherMation = sysEveUserStaffService.selectById(remainingId);
            if (ObjectUtil.isNotEmpty(studentMationList)) {
                item.setStudentMation(studentMationList);
            }
            if (ObjectUtil.isNotEmpty(teacherMation)) {
                item.setTeacherMation(teacherMation);
            }
        }
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public List<FriendRelationship> queryFriendList( String holderId, String friendId) {
        QueryWrapper<FriendRelationship> friendQueryWrapper = new QueryWrapper<>();
        friendQueryWrapper
                .and(wapper -> wapper
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), holderId)
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), friendId)
                )
                .or()
                .and(wapper -> wapper
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getFriendId), holderId)
                        .eq(MybatisPlusUtil.toColumns(FriendRelationship::getUserId), friendId)
                );
        return list(friendQueryWrapper);
    }
}

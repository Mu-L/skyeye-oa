package com.skyeye.school.chat.service.impl;

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
import com.skyeye.school.chat.dao.FriendRelationshipDao;
import com.skyeye.school.chat.entity.FriendRelationship;
import com.skyeye.school.chat.service.FriendRelationshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "好友关系", groupName = "好友关系")
public class FriendRelationshipServiceImpl extends SkyeyeBusinessServiceImpl<FriendRelationshipDao, FriendRelationship> implements FriendRelationshipService {

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    protected void createPrepose(FriendRelationship entity) {
        entity.setStatus(CommonNumConstants.NUM_ZERO);
    }

    @Override
    protected void updatePrepose(FriendRelationship entity) {
        entity.setStatus(entity.getStatus());
    }

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
    public void selectById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        Map<String, Object> map = iAuthUserService.queryDataMationById(id);
        outputObject.setBean(map);
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

}

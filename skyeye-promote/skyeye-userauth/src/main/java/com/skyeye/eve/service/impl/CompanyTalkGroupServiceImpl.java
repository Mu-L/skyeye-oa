/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.cache.redis.RedisCache;
import com.skyeye.chat.enums.TalkChatType;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.Constants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.dao.CompanyTalkGroupDao;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroup;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupInvite;
import com.skyeye.eve.entity.talk.group.CompanyTalkGroupUser;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteInGroupType;
import com.skyeye.eve.enumclass.CompanyTalkGroupInviteState;
import com.skyeye.eve.enumclass.CompanyTalkGroupState;
import com.skyeye.eve.service.CompanyTalkGroupInviteService;
import com.skyeye.eve.service.CompanyTalkGroupService;
import com.skyeye.eve.service.CompanyTalkGroupUserService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CompanyTalkGroupServiceImpl
 * @Description: 群组信息管理服务类--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 22:51
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "群组管理", groupName = "聊天模块")
public class CompanyTalkGroupServiceImpl extends SkyeyeBusinessServiceImpl<CompanyTalkGroupDao, CompanyTalkGroup> implements CompanyTalkGroupService {

    @Autowired
    private CompanyTalkGroupDao companyTalkGroupDao;

    @Autowired
    private RedisCache redisCache;

    @Autowired
    private CompanyTalkGroupInviteService companyTalkGroupInviteService;

    @Autowired
    private CompanyTalkGroupUserService companyTalkGroupUserService;

    @Override
    public void validatorEntity(CompanyTalkGroup entity) {
        super.validatorEntity(entity);
        String[] invites = entity.getUserIds().split(",");
        if (invites.length < 1) {
            throw new IllegalArgumentException("群组中最少拥有两名成员。");
        }
    }

    @Override
    public void createPrepose(CompanyTalkGroup entity) {
        super.createPrepose(entity);
        entity.setGroupUserNum(200);
        entity.setGroupNum(ToolUtil.getTalkGroupNum());
        entity.setGroupHistroyImg(entity.getGroupImg() + ",");
        entity.setState(CompanyTalkGroupState.NORMAL.getKey());
    }

    @Override
    public void createPostpose(CompanyTalkGroup entity, String userId) {
        // 保存群组信息
        String[] invites = entity.getUserIds().split(",");
        companyTalkGroupInviteService.saveList(entity.getId(), Arrays.asList(invites), userId);

        // 将当前用户添加到群组中
        CompanyTalkGroupUser groupUser = new CompanyTalkGroupUser();
        groupUser.setUserId(userId);
        groupUser.setGroupId(entity.getId());
        groupUser.setCreateTime(DateUtil.getTimeAndToString());
        companyTalkGroupUserService.createEntity(groupUser, userId);

        // 删除该用户在redis中存储的群组列表信息
        jedisClientService.del(Constants.getSysTalkUserHasGroupListMationById(userId));
    }

    @Override
    public QueryWrapper<CompanyTalkGroup> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<CompanyTalkGroup> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(CompanyTalkGroup::getState), CompanyTalkGroupState.NORMAL.getKey());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return beans;
        }
        String userId = inputObject.getLogParams().get("id").toString();
        List<String> groupIds = beans.stream().map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        Map<String, String> groupUserIsExit = companyTalkGroupUserService.batchCheckGroupUserIsExit(groupIds, userId);
        beans.forEach(bean -> {
            String groupId = bean.get("id").toString();
            bean.put("inId", groupUserIsExit.get(groupId));
        });
        return beans;
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertGroupMationToTalk(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        // 判断用户是否在该群聊
        boolean userIsExit = companyTalkGroupUserService.checkGroupUserIsExit(groupId, userId);
        if (userIsExit) {
            throw new CustomException("您已在该群聊。");
        }
        // 判断是否有该用户的未审批的群聊申请信息
        boolean invitation = companyTalkGroupInviteService.checkGroupInvitationMationByUserId(userId, groupId);
        if (invitation) {
            return;
        }

        CompanyTalkGroup companyTalkGroup = selectById(groupId);
        // 判断群组人数是否已达上限
        long userCount = companyTalkGroupUserService.countByGroupId(companyTalkGroup.getId());
        if (companyTalkGroup.getGroupUserNum() <= userCount) {
            throw new CustomException("群组人数已达上限！");
        }
        CompanyTalkGroupInvite companyTalkGroupInvite = new CompanyTalkGroupInvite();
        companyTalkGroupInvite.setGroupId(groupId);
        companyTalkGroupInvite.setInviteUserId(companyTalkGroup.getCreateId());
        companyTalkGroupInvite.setState(CompanyTalkGroupInviteState.WAITING_CHECK.getKey());
        companyTalkGroupInvite.setInGroupType(CompanyTalkGroupInviteInGroupType.SEARCH_ACCOUNT.getKey());
        companyTalkGroupInvite.setWhetherRead(WhetherEnum.DISABLE_USING.getKey());
        companyTalkGroupInviteService.createEntity(companyTalkGroupInvite, userId);
        outputObject.setBean(companyTalkGroupInvite);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryGroupMemberByGroupId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("id").toString();
        String cacheKey = Constants.checkSysEveTalkGroupUserListByGroupId(groupId);
        List<Map<String, Object>> beans = redisCache.getList(cacheKey, key -> companyTalkGroupDao.queryGroupMemberByGroupId(groupId), RedisConstants.ALL_USE_TIME);
        map.clear();
        map.put("members", beans.size());
        map.put("list", beans);
        outputObject.setBean(map);
    }

    @Override
    public void queryChatLogByType(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String chatType = map.get("chatType").toString();
        if (StrUtil.equals(TalkChatType.PERSONAL_TO_PERSONAL.getChType(), chatType)) {//个人对个人
            Map<String, Object> user = inputObject.getLogParams();
            map.put("userId", user.get("id"));
            Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
            List<Map<String, Object>> beans = companyTalkGroupDao.queryChatLogByPerToPer(map);
            outputObject.setBeans(beans);
            outputObject.settotal(pages.getTotal());
        } else if (StrUtil.equals(TalkChatType.GROUP_CHAT.getChType(), chatType)) {//个人对群组
            Map<String, Object> user = inputObject.getLogParams();
            map.put("userId", user.get("id"));
            Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
            List<Map<String, Object>> beans = companyTalkGroupDao.queryChatLogByPerToGroup(map);
            outputObject.setBeans(beans);
            outputObject.settotal(pages.getTotal());
        } else {
            outputObject.setreturnMessage("参数错误");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editUserToExitGroup(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        CompanyTalkGroup companyTalkGroup = selectById(groupId);
        if (companyTalkGroup == null || StrUtil.isEmpty(companyTalkGroup.getId())) {
            throw new CustomException("群信息不存在，请核实后进行操作。");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        if (StrUtil.equals(companyTalkGroup.getCreateId(), userId)) {
            outputObject.setreturnMessage("您是该群聊的创建人，无法退群，请进行解散群聊操作。");
        }

        companyTalkGroupUserService.deleteByGroupIdAndUserId(groupId, userId);
        // 删除群组成员缓存
        jedisClientService.del(Constants.checkSysEveTalkGroupUserListByGroupId(groupId));
        // 删除该用户在redis中存储的群组列表信息
        jedisClientService.del(Constants.getSysTalkUserHasGroupListMationById(userId));
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void editCreateToExitGroup(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String groupId = map.get("groupId").toString();
        CompanyTalkGroup companyTalkGroup = selectById(groupId);
        if (companyTalkGroup == null || StrUtil.isEmpty(companyTalkGroup.getId())) {
            throw new CustomException("群信息不存在，请核实后进行操作。");
        }
        String userId = inputObject.getLogParams().get("id").toString();
        if (!StrUtil.equals(companyTalkGroup.getCreateId(), userId)) {
            outputObject.setreturnMessage("您不是该群聊的创建人，无法退群，请进行退出群聊操作。");
        }
        List<CompanyTalkGroupUser> companyTalkGroupUsers = companyTalkGroupUserService.selectByGroupId(groupId);
        companyTalkGroupUsers.forEach(groupUser -> {
            // 删除该用户在redis中存储的群组列表信息
            jedisClientService.del(Constants.getSysTalkUserHasGroupListMationById(groupUser.getUserId()));
        });
        // 删除群组成员缓存
        jedisClientService.del(Constants.checkSysEveTalkGroupUserListByGroupId(groupId));
        // 删除该用户在redis中存储的群组列表信息
        jedisClientService.del(Constants.getSysTalkUserHasGroupListMationById(userId));
        dissolvedGroup(groupId);
    }

    private void dissolvedGroup(String groupId) {
        // 解散群聊
        UpdateWrapper<CompanyTalkGroup> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, groupId);
        updateWrapper.set(MybatisPlusUtil.toColumns(CompanyTalkGroup::getState), CompanyTalkGroupState.DISSOLVED.getKey());
        update(updateWrapper);
        refreshCache(groupId);
    }

}

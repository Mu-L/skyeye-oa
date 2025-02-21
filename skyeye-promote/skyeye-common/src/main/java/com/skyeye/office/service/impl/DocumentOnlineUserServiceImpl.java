package com.skyeye.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.office.dao.DocumentOnlineUserDao;
import com.skyeye.office.entity.DocumentOnlineUser;
import com.skyeye.office.service.DocumentOnlineUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: DocumentOnlineUserServiceImpl
 * @Description: 文档在线用户服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Service
public class DocumentOnlineUserServiceImpl extends SkyeyeBusinessServiceImpl<DocumentOnlineUserDao, DocumentOnlineUser> implements DocumentOnlineUserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userJoin(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        Date now = new Date();

        // 检查用户是否已在线
        QueryWrapper<DocumentOnlineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getDocumentId), documentId)
            .eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getUserId), userId);
        DocumentOnlineUser existUser = getOne(queryWrapper);


        if (existUser != null) {
            // 更新最后活跃时间
            existUser.setLastActiveTime(now);
            super.updateById(existUser);
            outputObject.setBean(existUser);
        } else {
            // 创建新的在线记录
            DocumentOnlineUser onlineUser = new DocumentOnlineUser();
            onlineUser.setDocumentId(documentId);
            onlineUser.setUserId(userId);
            onlineUser.setLoginTime(now);
            onlineUser.setLastActiveTime(now);
            super.createEntity(onlineUser, userId);
            outputObject.setBean(onlineUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userLeave(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<DocumentOnlineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getDocumentId), documentId)
            .eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getUserId), userId);
        remove(queryWrapper);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActiveTime(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        Date now = new Date();

        QueryWrapper<DocumentOnlineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getDocumentId), documentId)
            .eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getUserId), userId);
        DocumentOnlineUser onlineUser = getOne(queryWrapper);

        if (onlineUser != null) {
            onlineUser.setLastActiveTime(now);
            super.updateById(onlineUser);
            outputObject.setBean(onlineUser);
        }
    }

    @Override
    public void getOnlineUsers(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().get("documentId").toString();
        QueryWrapper<DocumentOnlineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getDocumentId), documentId)
            .orderByDesc(MybatisPlusUtil.toColumns(DocumentOnlineUser::getLastActiveTime));
        List<DocumentOnlineUser> onlineUsers = list(queryWrapper);
        outputObject.setBean(onlineUsers);
    }

    @Override
    public void userJoin(String documentId, String userId) {
        Date now = new Date();
        QueryWrapper<DocumentOnlineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getDocumentId), documentId)
            .eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getUserId), userId);
        DocumentOnlineUser existUser = getOne(queryWrapper);

        if (existUser != null) {
            // 更新最后活跃时间
            existUser.setLastActiveTime(now);
            super.updateById(existUser);
        } else {
            // 创建新的在线记录
            DocumentOnlineUser onlineUser = new DocumentOnlineUser();
            onlineUser.setDocumentId(documentId);
            onlineUser.setUserId(userId);
            onlineUser.setLoginTime(now);
            onlineUser.setLastActiveTime(now);
            super.createEntity(onlineUser, userId);
        }
    }

    @Override
    public void userLeave(String documentId, String userId) {
        QueryWrapper<DocumentOnlineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getDocumentId), documentId)
            .eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getUserId), userId);
        remove(queryWrapper);
    }

    @Override
    public void updateActiveTime(String documentId, String userId) {
        Date now = new Date();

        QueryWrapper<DocumentOnlineUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getDocumentId), documentId)
            .eq(MybatisPlusUtil.toColumns(DocumentOnlineUser::getUserId), userId);
        DocumentOnlineUser onlineUser = getOne(queryWrapper);

        if (onlineUser != null) {
            onlineUser.setLastActiveTime(now);
            super.updateById(onlineUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public int deleteOverTimeUser() {
        // 获取10分钟前的时间点
        Date inactiveTime = new Date(System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(10));
        // 删除超过10分钟未活跃的用户
        QueryWrapper<DocumentOnlineUser> wrapper = new QueryWrapper<>();
        wrapper.lt(MybatisPlusUtil.toColumns(DocumentOnlineUser::getLastActiveTime), inactiveTime);
        long count = count(wrapper);
        remove(wrapper);
        return (int) count;
    }
} 
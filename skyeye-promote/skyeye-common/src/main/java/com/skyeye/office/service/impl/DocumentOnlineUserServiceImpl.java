package com.skyeye.office.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.office.entity.DocumentOnlineUser;
import com.skyeye.office.service.DocumentOnlineUserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * @ClassName: DocumentOnlineUserServiceImpl
 * @Description: 文档在线用户服务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
@Service
public class DocumentOnlineUserServiceImpl extends SkyeyeBusinessServiceImpl<DocumentOnlineUser> implements DocumentOnlineUserService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userJoin(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String userId = inputObject.getParams().getString("userId");
        Date now = new Date();

        // 检查用户是否已在线
        DocumentOnlineUser existUser = super.selectOne(ToolUtil.getWrapper(DocumentOnlineUser.class)
            .eq("document_id", documentId)
            .eq("user_id", userId));

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
            super.createEntity(onlineUser);
            outputObject.setBean(onlineUser);
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void userLeave(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String userId = inputObject.getParams().getString("userId");

        super.deleteByWrapper(ToolUtil.getWrapper(DocumentOnlineUser.class)
            .eq("document_id", documentId)
            .eq("user_id", userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateActiveTime(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");
        String userId = inputObject.getParams().getString("userId");
        Date now = new Date();

        DocumentOnlineUser onlineUser = super.selectOne(ToolUtil.getWrapper(DocumentOnlineUser.class)
            .eq("document_id", documentId)
            .eq("user_id", userId));

        if (onlineUser != null) {
            onlineUser.setLastActiveTime(now);
            super.updateById(onlineUser);
            outputObject.setBean(onlineUser);
        }
    }

    @Override
    public void getOnlineUsers(InputObject inputObject, OutputObject outputObject) {
        String documentId = inputObject.getParams().getString("documentId");

        List<DocumentOnlineUser> onlineUsers = super.selectList(ToolUtil.getWrapper(DocumentOnlineUser.class)
            .eq("document_id", documentId)
            .orderByDesc("last_active_time"));

        outputObject.setBean(onlineUsers);
    }
} 
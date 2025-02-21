package com.skyeye.office.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.office.entity.DocumentOnlineUser;

/**
 * @ClassName: DocumentOnlineUserService
 * @Description: 文档在线用户服务接口类，处理用户在线状态管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/10
 */
public interface DocumentOnlineUserService extends SkyeyeBusinessService<DocumentOnlineUser> {

    /**
     * 用户加入文档（Controller调用）
     * @param inputObject 输入参数
     * @param outputObject 输出参数
     */
    void userJoin(InputObject inputObject, OutputObject outputObject);

    /**
     * 用户离开文档（Controller调用）
     * @param inputObject 输入参数
     * @param outputObject 输出参数
     */
    void userLeave(InputObject inputObject, OutputObject outputObject);

    /**
     * 更新用户活跃时间（Controller调用）
     * @param inputObject 输入参数
     * @param outputObject 输出参数
     */
    void updateActiveTime(InputObject inputObject, OutputObject outputObject);

    /**
     * 获取在线用户列表
     * @param inputObject 输入参数
     * @param outputObject 输出参数
     */
    void getOnlineUsers(InputObject inputObject, OutputObject outputObject);

    /**
     * 用户加入文档（WebSocket调用）
     * @param documentId 文档ID
     * @param userId 用户ID
     */
    void userJoin(String documentId, String userId);

    /**
     * 用户离开文档（WebSocket调用）
     * @param documentId 文档ID
     * @param userId 用户ID
     */
    void userLeave(String documentId, String userId);

    /**
     * 更新用户活跃时间（WebSocket调用）
     * @param documentId 文档ID
     * @param userId 用户ID
     */
    void updateActiveTime(String documentId, String userId);
}
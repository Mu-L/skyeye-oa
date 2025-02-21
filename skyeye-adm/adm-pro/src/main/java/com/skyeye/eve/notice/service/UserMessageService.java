/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.notice.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.notice.entity.UserMessage;

/**
 * @ClassName: UserMessageService
 * @Description: 用户消息管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/1/31 21:35
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目
 */
public interface UserMessageService extends SkyeyeBusinessService<UserMessage> {

    void getTopEightMessageList(InputObject inputObject, OutputObject outputObject);

    void editMessageById(InputObject inputObject, OutputObject outputObject);

    void deleteAllMessage(InputObject inputObject, OutputObject outputObject);

    void insertUserMessage(InputObject inputObject, OutputObject outputObject);

    void queryUnReadMessageCount(InputObject inputObject, OutputObject outputObject);
}

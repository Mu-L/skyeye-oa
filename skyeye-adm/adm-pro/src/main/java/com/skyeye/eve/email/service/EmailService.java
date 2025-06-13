/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.email.entity.Email;

/**
 * @ClassName: EmailService
 * @Description: 邮件管理服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/8 9:14
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface EmailService extends SkyeyeBusinessService<Email> {

    void querySendedEmailListByEmailId(InputObject inputObject, OutputObject outputObject);

    void queryDeleteEmailListByEmailId(InputObject inputObject, OutputObject outputObject);

    void queryDraftsEmailListByEmailId(InputObject inputObject, OutputObject outputObject);

    void insertToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject);

    void insertToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject);

    void editToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject);

    void insertToSendEmailMationByEmailId(InputObject inputObject, OutputObject outputObject);

    void insertForwardToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject);

    void clearEmailByObjectId(InputObject inputObject, OutputObject outputObject);
}

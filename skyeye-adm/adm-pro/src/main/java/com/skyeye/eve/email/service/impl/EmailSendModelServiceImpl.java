/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.email.dao.EmailSendModelDao;
import com.skyeye.eve.email.entity.EmailSendModel;
import com.skyeye.eve.email.service.EmailSendModelService;
import org.springframework.stereotype.Service;

/**
 * @ClassName: EmailSendModelServiceImpl
 * @Description: 邮件发送模板服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/9 21:48
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "邮件发送模板", groupName = "邮件发送模板")
public class EmailSendModelServiceImpl extends SkyeyeBusinessServiceImpl<EmailSendModelDao, EmailSendModel> implements EmailSendModelService {

    @Override
    protected QueryWrapper<EmailSendModel> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<EmailSendModel> queryWrapper = super.getQueryWrapper(commonPageInfo);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EmailSendModel::getCreateId), userId);
        return queryWrapper;
    }
}

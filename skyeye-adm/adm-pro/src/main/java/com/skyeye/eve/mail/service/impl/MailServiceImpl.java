/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.mail.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.mail.classenum.MailCategory;
import com.skyeye.eve.mail.dao.MailDao;
import com.skyeye.eve.mail.entity.Mail;
import com.skyeye.eve.mail.service.MailService;
import com.skyeye.eve.mail.service.MailTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @ClassName: MailServiceImpl
 * @Description: 通讯录管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:54
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "通讯录管理", groupName = "通讯录管理")
public class MailServiceImpl extends SkyeyeBusinessServiceImpl<MailDao, Mail> implements MailService {

    @Autowired
    private MailTypeService mailTypeService;

    @Override
    protected QueryWrapper<Mail> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Mail> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Mail::getCategory), commonPageInfo.getType());
        if (StrUtil.equals(commonPageInfo.getType(), MailCategory.PERSON.getKey().toString())) {
            // 个人通讯录
            queryWrapper.eq(MybatisPlusUtil.toColumns(Mail::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
            if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
                // 所属分组
                queryWrapper.eq(MybatisPlusUtil.toColumns(Mail::getTypeId), commonPageInfo.getObjectId());
            }
        }
        return queryWrapper;
    }

    @Override
    public Mail selectById(String id) {
        Mail mail = super.selectById(id);
        mailTypeService.setDataMation(mail, Mail::getTypeId);
        return mail;
    }
}

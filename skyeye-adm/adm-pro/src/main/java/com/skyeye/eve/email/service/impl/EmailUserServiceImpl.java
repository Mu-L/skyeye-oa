/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.MailUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.email.dao.EmailUserDao;
import com.skyeye.eve.email.entity.EmailUser;
import com.skyeye.eve.email.service.EmailUserService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: EmailUserServiceImpl
 * @Description: 用户绑定的邮箱服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/8 10:26
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "用户绑定的邮箱", groupName = "用户绑定的邮箱")
public class EmailUserServiceImpl extends SkyeyeBusinessServiceImpl<EmailUserDao, EmailUser> implements EmailUserService {

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Override
    public void queryEmailListByUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<EmailUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EmailUser::getCreateId), userId);
        queryWrapper.select(CommonConstants.ID, MybatisPlusUtil.toColumns(EmailUser::getEmailAddress),
            MybatisPlusUtil.toColumns(EmailUser::getEmailCheck));
        List<EmailUser> emailUserList = list(queryWrapper);
        outputObject.setBeans(emailUserList);
        outputObject.settotal(emailUserList.size());
    }

    @Override
    public void validatorEntity(EmailUser entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        entity.setCreateId(userId);
        super.validatorEntity(entity);
        // 获取服务器信息
        Map<String, Object> emailServer = iSystemFoundationSettingsService.querySystemFoundationSettingsList();
        boolean login = new MailUtil(entity.getEmailAddress(), entity.getEmailPassword(),
            emailServer.get("emailSendServer").toString()).authLogin();
        if (!login) {
            throw new CustomException("邮箱登录失败，请检查账号密码是否正确。");
        }
    }

    @Override
    public void createPrepose(EmailUser entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<EmailUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(EmailUser::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(EmailUser::getEmailCheck), WhetherEnum.ENABLE_USING.getKey());
        EmailUser checkItem = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(checkItem)) {
            entity.setEmailCheck(WhetherEnum.ENABLE_USING.getKey());
        } else {
            entity.setEmailCheck(WhetherEnum.DISABLE_USING.getKey());
        }
        entity.setCreateId(userId);
        entity.setCreateTime(DateUtil.getTimeAndToString());
    }

    @Override
    protected void deletePreExecution(EmailUser entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!StrUtil.equals(entity.getCreateId(), userId)) {
            throw new CustomException("只能删除自己绑定的邮箱信息。");
        }
    }

    /**
     * 从服务器上获取收件箱里的邮件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void insertEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) {
        getDataFromServer(inputObject, MqConstants.JobMateMationJobType.MAIL_ACCESS_INBOX.getJobType());
    }

    private void getDataFromServer(InputObject inputObject, Integer type) {
        Map<String, Object> map = inputObject.getParams();
        String emailUserId = map.get("emailUserId").toString();
        EmailUser emailUser = selectById(emailUserId);

        String userId = inputObject.getLogParams().get("id").toString();
        if (ObjectUtil.isNotEmpty(emailUser) && emailUser.getCreateId().equals(userId)) {
            // 消息队列参数对象
            Map<String, Object> emailNotice = new HashMap<>();
            emailNotice.put("type", type);//消息队列任务类型
            if (tenantEnable) {
                emailNotice.put("tenantId", TenantContext.getTenantId());//租户id
            }
            emailNotice.put("userAddress", emailUser.getEmailAddress());//邮箱地址
            emailNotice.put("userPassword", emailUser.getEmailPassword());//邮箱密码
            emailNotice.put("userId", userId);
            this.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
        } else {
            throw new CustomException("该邮箱信息不存在或者该邮箱信息不属于当前账号。");
        }
    }

    /**
     * 从服务器上获取已发送邮件
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void insertSendedEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) {
        getDataFromServer(inputObject, MqConstants.JobMateMationJobType.MAIL_ACCESS_SENDED.getJobType());
    }

    @Override
    public void insertDelsteEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) {
        getDataFromServer(inputObject, MqConstants.JobMateMationJobType.MAIL_ACCESS_DELETE.getJobType());
    }

    @Override
    public void insertDraftsEmailListFromServiceByUserId(InputObject inputObject, OutputObject outputObject) {
        getDataFromServer(inputObject, MqConstants.JobMateMationJobType.MAIL_ACCESS_DRAFTS.getJobType());
    }

    private void sendMQProducer(String jsonStr, String userId) {
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(jsonStr);
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }
}

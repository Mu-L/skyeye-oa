/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.email.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.annotation.tenant.IgnoreTenant;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.email.classenum.EmailState;
import com.skyeye.eve.email.dao.EmailDao;
import com.skyeye.eve.email.entity.Email;
import com.skyeye.eve.email.entity.EmailEnclosure;
import com.skyeye.eve.email.entity.EmailParams;
import com.skyeye.eve.email.entity.EmailUser;
import com.skyeye.eve.email.service.EmailEnclosureService;
import com.skyeye.eve.email.service.EmailService;
import com.skyeye.eve.email.service.EmailUserService;
import com.skyeye.eve.rest.mq.JobMateMation;
import com.skyeye.eve.service.IJobMateMationService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: EmailServiceImpl
 * @Description: 邮件管理服务层--强隔离
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/8 9:15
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "邮件管理", groupName = "邮件管理")
public class EmailServiceImpl extends SkyeyeBusinessServiceImpl<EmailDao, Email> implements EmailService {

    @Autowired
    private EmailEnclosureService emailEnclosureService;

    @Autowired
    private IJobMateMationService iJobMateMationService;

    @Autowired
    private EmailUserService emailUserService;

    @Override
    @IgnoreTenant
    public void queryPageList(InputObject inputObject, OutputObject outputObject) {
        super.queryPageList(inputObject, outputObject);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = getPageObject(inputObject);
        commonPageInfo.setState(String.valueOf(EmailState.NORMAL.getKey()));
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryEmailListByEmailId(commonPageInfo);
        return beans;
    }

    private CommonPageInfo getPageObject(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        if (StrUtil.isEmpty(commonPageInfo.getObjectId())) {
            throw new CustomException("请先绑定邮箱后再进行操作。");
        }
        EmailUser emailUser = emailUserService.selectById(commonPageInfo.getObjectId());
        String userId = inputObject.getLogParams().get("id").toString();
        if (!userId.equals(emailUser.getCreateId())) {
            throw new CustomException("该邮箱信息不存在或者该邮箱信息不属于当前账号。");
        }
        commonPageInfo.setObjectId(emailUser.getEmailAddress());
        if (tenantEnable) {
            commonPageInfo.setTenantId(TenantContext.getTenantId());
        }
        return commonPageInfo;
    }

    @Override
    public void validatorEntity(Email entity) {
        if (StrUtil.isNotEmpty(entity.getEmailEnclosure())) {
            entity.setIsContainAttach(WhetherEnum.ENABLE_USING.getKey());
        } else {
            entity.setIsContainAttach(WhetherEnum.DISABLE_USING.getKey());
        }
    }

    @Override
    public void createPrepose(Email entity) {
        entity.setSendDate(DateUtil.getTimeAndToString());
        entity.setReplaySign(WhetherEnum.ENABLE_USING.getKey());
        entity.setIsNew(WhetherEnum.DISABLE_USING.getKey());
    }

    @Override
    public void writePostpose(Email entity, String userId) {
        List<EmailEnclosure> emailEnclosures = new ArrayList<>();
        emailEnclosureService.deleteByObjectId(entity.getId());
        if (StrUtil.isNotEmpty(entity.getEmailEnclosure())) {
            // 保存邮件附件
            List<Map<String, Object>> enclosureList = iEnclosureService.queryEnclosureInfoByIds(entity.getEmailEnclosure());
            enclosureList.forEach(enclosure -> {
                EmailEnclosure emailEnclosure = new EmailEnclosure();
                emailEnclosure.setFileName(enclosure.get("name").toString());
                emailEnclosure.setFilePath(enclosure.get("fileAddress").toString());
                emailEnclosure.setFileExtName(enclosure.get("type").toString());
                emailEnclosure.setFileSize(enclosure.get("size").toString());
                emailEnclosure.setParentId(entity.getId());
                emailEnclosures.add(emailEnclosure);
            });
            emailEnclosureService.createEntity(emailEnclosures, userId);
        }

        EmailUser emailUser = emailUserService.selectById(entity.getEmailUserId());
        // 消息队列参数对象
        Map<String, Object> emailNotice = new HashMap<>();
        emailNotice.put("type", entity.getMessageJobType());//消息队列任务类型
        emailNotice.put("userAddress", emailUser.getEmailAddress());//邮箱地址
        emailNotice.put("userPassword", emailUser.getEmailPassword());//邮箱密码
        if (tenantEnable) {
            emailNotice.put("tenantId", TenantContext.getTenantId());//租户id
        }
        emailNotice.put("title", entity.getTitle());//邮件标题
        emailNotice.put("content", entity.getContent());//邮件内容
        emailNotice.put("toPeople", entity.getToPeople());//邮件接收人
        emailNotice.put("toCc", entity.getToCc());//邮件抄送人
        emailNotice.put("toBcc", entity.getToBcc());//邮件暗送人
        emailNotice.put("emailId", entity.getId());//邮件id
        emailNotice.put("emailEnclosure", JSONUtil.toJsonStr(emailEnclosures));//邮件附件
        this.sendMQProducer(JSONUtil.toJsonStr(emailNotice), userId);
    }

    private void sendMQProducer(String jsonStr, String userId) {
        JobMateMation jobMateMation = new JobMateMation();
        jobMateMation.setJsonStr(jsonStr);
        jobMateMation.setUserId(userId);
        iJobMateMationService.sendMQProducer(jobMateMation);
    }

    @Override
    public Email getDataFromDb(String id) {
        Email email = super.getDataFromDb(id);
        if (email.getIsContainAttach().equals(WhetherEnum.ENABLE_USING.getKey())) {
            List<EmailEnclosure> emailEnclosureList = emailEnclosureService.queryByObjectId(id);
            email.setEmailEnclosureList(emailEnclosureList);
        }
        SimpleDateFormat sdf1 = new SimpleDateFormat(DateUtil.YYYY_MM_DD);
        email.setSendDay(sdf1.format(DateUtil.getPointTime(email.getSendDate(), DateUtil.YYYY_MM_DD)));
        SimpleDateFormat sdf2 = new SimpleDateFormat(DateUtil.HH_MM);
        email.setSendTime(sdf2.format(DateUtil.getPointTime(email.getSendDate(), DateUtil.YYYY_MM_DD_HH_MM_SS)));
        return email;
    }

    @Override
    public void querySendedEmailListByEmailId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = getPageObject(inputObject);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        commonPageInfo.setType(String.valueOf(CommonNumConstants.NUM_FOUR));
        commonPageInfo.setState(String.valueOf(EmailState.NORMAL.getKey()));
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryEmailListByEmailId(commonPageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryDeleteEmailListByEmailId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = getPageObject(inputObject);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        commonPageInfo.setType(String.valueOf(CommonNumConstants.NUM_FIVE));
        commonPageInfo.setState(String.valueOf(EmailState.DELETE.getKey()));
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryEmailListByEmailId(commonPageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void queryDraftsEmailListByEmailId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = getPageObject(inputObject);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        commonPageInfo.setType(String.valueOf(CommonNumConstants.NUM_FOUR));
        commonPageInfo.setState(String.valueOf(EmailState.DRAFT.getKey()));
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryEmailListByEmailId(commonPageInfo);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void insertToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        EmailParams emailParams = inputObject.getParams(EmailParams.class);
        createEmail(emailParams, EmailState.NORMAL.getKey(), inputObject.getLogParams().get("id").toString(), MqConstants.JobMateMationJobType.COMPLEX_MAIL_DELIVERY.getJobType(), null);
    }

    private void createEmail(EmailParams emailParams, Integer state, String userId, Integer messageJobType, String id) {
        Email email = new Email();
        email.setTitle(emailParams.getTitle());
        email.setContent(emailParams.getContent());
        setToEmail(emailParams, email);
        email.setState(state);
        email.setMessageJobType(messageJobType);
        email.setEmailUserId(emailParams.getEmailUserId());

        EmailUser emailUser = emailUserService.selectById(emailParams.getEmailUserId());
        email.setFromPeople(emailUser.getEmailAddress());

        email.setEmailEnclosure(emailParams.getEmailEnclosure());
        if (StrUtil.isNotEmpty(id)) {
            email.setId(id);
            updateEntity(email, userId);
        } else {
            createEntity(email, userId);
        }
    }

    private void setToEmail(EmailParams emailParams, Email email) {
        // 接收人邮箱校验
        String[] toPeopleEmails = emailParams.getToPeople().split(",");
        String toPeople = Joiner.on(CommonCharConstants.COMMA_MARK).join(Arrays.asList(toPeopleEmails).stream()
            .filter(str -> ToolUtil.isEmail(str)).collect(Collectors.toList()));
        if (ToolUtil.isBlank(toPeople)) {
            throw new CustomException("请选择收件人");
        }

        // 抄送人邮箱校验
        String[] toCcEmails = emailParams.getToCc().split(",");
        String toCc = Joiner.on(CommonCharConstants.COMMA_MARK).join(Arrays.asList(toCcEmails).stream()
            .filter(str -> ToolUtil.isEmail(str)).collect(Collectors.toList()));
        // 暗送人邮箱校验
        String[] toBccEmails = emailParams.getToBcc().split(",");
        String toBcc = Joiner.on(CommonCharConstants.COMMA_MARK).join(Arrays.asList(toBccEmails).stream()
            .filter(str -> ToolUtil.isEmail(str)).collect(Collectors.toList()));
        email.setToPeople(toPeople);
        email.setToCc(toCc);
        email.setToBcc(toBcc);
    }

    @Override
    public void insertToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        EmailParams emailParams = inputObject.getParams(EmailParams.class);
        createEmail(emailParams, EmailState.DRAFT.getKey(), inputObject.getLogParams().get("id").toString(), MqConstants.JobMateMationJobType.MAIL_DRAFTS_SAVE.getJobType(), null);
    }

    @Override
    public void editToDraftsEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        EmailParams emailParams = inputObject.getParams(EmailParams.class);
        createEmail(emailParams, EmailState.DRAFT.getKey(), inputObject.getLogParams().get("id").toString(), MqConstants.JobMateMationJobType.MAIL_DRAFTS_EDIT.getJobType(), emailParams.getId());
    }

    @Override
    public void insertToSendEmailMationByEmailId(InputObject inputObject, OutputObject outputObject) {
        EmailParams emailParams = inputObject.getParams(EmailParams.class);
        createEmail(emailParams, EmailState.NORMAL.getKey(), inputObject.getLogParams().get("id").toString(), MqConstants.JobMateMationJobType.MAIL_DRAFTS_SEND.getJobType(), emailParams.getId());
    }

    @Override
    public void insertForwardToSendEmailMationByUserId(InputObject inputObject, OutputObject outputObject) {
        EmailParams emailParams = inputObject.getParams(EmailParams.class);
        createEmail(emailParams, EmailState.NORMAL.getKey(), inputObject.getLogParams().get("id").toString(), MqConstants.JobMateMationJobType.COMPLEX_MAIL_DELIVERY.getJobType(), null);
    }

    @Override
    public void clearEmailByObjectId(InputObject inputObject, OutputObject outputObject) {
        String objectId = inputObject.getParams().get("objectId").toString();
        EmailUser emailUser = emailUserService.selectById(objectId);
        String userId = inputObject.getLogParams().get("id").toString();
        if (!userId.equals(emailUser.getCreateId())) {
            throw new CustomException("该邮箱信息不存在或者该邮箱信息不属于当前账号。");
        }
        QueryWrapper<Email> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(w -> w
            .eq(MybatisPlusUtil.toColumns(Email::getFromPeople), emailUser.getEmailAddress())
            .or()
            .apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(Email::getToPeople) + ", ','), CONCAT(',', {0}, ','))", emailUser.getEmailAddress())
            .or()
            .apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(Email::getToCc) + ", ','), CONCAT(',', {0}, ','))", emailUser.getEmailAddress())
            .or()
            .apply("INSTR(CONCAT(',', " + MybatisPlusUtil.toColumns(Email::getToBcc) + ", ','), CONCAT(',', {0}, ','))", emailUser.getEmailAddress())
        );
        remove(queryWrapper);
    }
}

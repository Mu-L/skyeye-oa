/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.mq.job.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.constans.MqConstants;
import com.skyeye.common.tenant.context.TenantContext;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.EmailUtil;
import com.skyeye.common.util.ShowMail;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.eve.email.classenum.EmailState;
import com.skyeye.eve.email.dao.EmailDao;
import com.skyeye.eve.service.ISystemFoundationSettingsService;
import com.skyeye.util.MqSendUtil;
import org.apache.rocketmq.spring.annotation.RocketMQMessageListener;
import org.apache.rocketmq.spring.core.RocketMQListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Part;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: MailAccessDeleteServiceImpl
 * @Description: 已删除邮件获取
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/4 21:58
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Component
@RocketMQMessageListener(
    topic = "${topic.mail-access-delete-service}",
    consumerGroup = "${topic.mail-access-delete-service}",
    selectorExpression = "${spring.profiles.active}")
public class MailAccessDeleteServiceImpl implements RocketMQListener<String> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MailAccessDeleteServiceImpl.class);

    @Value("${IMAGES_PATH}")
    private String tPath;

    @Autowired
    private EmailDao emailDao;

    @Value("${skyeye.tenant.enable}")
    protected boolean tenantEnable;

    @Autowired
    private ISystemFoundationSettingsService iSystemFoundationSettingsService;

    @Override
    public void onMessage(String data) {
        Map<String, Object> map = JSONUtil.toBean(data, null);
        String jobId = map.get("jobMateId").toString();
        try {
            String tenantId = StrUtil.EMPTY;
            if (tenantEnable) {
                tenantId = map.get("tenantId").toString();
                TenantContext.setTenantId(tenantId);
            }
            // 任务开始
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_PROCESSING, StrUtil.EMPTY);
            //获取服务器信息
            Map<String, Object> emailServer = iSystemFoundationSettingsService.querySystemFoundationSettingsList();

            String storeType = emailServer.get("emailType").toString();//邮箱类型
            String host = emailServer.get("emailReceiptServer").toString();//邮箱收件服务器
            String username = map.get("userAddress").toString();//登录邮箱账号
            String password = map.get("userPassword").toString();//密码
            String basePath = tPath + FileConstants.FileUploadPath.getSavePath(
                FileConstants.FileUploadPath.EMAIL_ENCLOSURE.getType()[0]
            );//附件存储路径

            Folder folder = ToolUtil.getFolderByServer(host, username, password, storeType, "Deleted Messages");
            if (!folder.exists()) {//如果文件夹不存在，则创建
                folder.create(Folder.HOLDS_MESSAGES);
            }
            folder.open(Folder.READ_ONLY);
            Message[] message = folder.getMessages();//获取邮件信息
            ShowMail re;
            //邮件集合
            List<Map<String, Object>> beans = new ArrayList<>();
            Map<String, Object> bean;
            //附件集合
            List<Map<String, Object>> enclosureBeans = new ArrayList<>();
            //获取当前邮箱已有的邮件
            List<Map<String, Object>> emailHasMail = emailDao.queryEmailListByEmailAddress(username, EmailState.DELETE.getKey(), tenantId);

            //创建目录
            ToolUtil.createFolder(basePath);

            //遍历邮件数据
            for (int i = 0; i < message.length; i++) {
                if (!message[i].getFolder().isOpen()) {
                    // 判断是否open,如果close，就重新open
                    message[i].getFolder().open(Folder.READ_ONLY);
                }
                re = new ShowMail((MimeMessage) message[i]);
                //如果该邮件在本地数据库中不存在并且messageId不为空
                //收件人或者抄送人或者暗送人是当前账号
                if (!ToolUtil.judgeInListByMessage(emailHasMail, re.getMessageId()) && !ToolUtil.isBlank(re.getMessageId())
                    && (re.getMailAddress("to").indexOf(username) > -1 || re.getMailAddress("cc").indexOf(username) > -1 || re.getMailAddress("bcc").indexOf(username) > -1)) {
                    bean = EmailUtil.getEmailMationByUtil(re, message[i]);
                    String rowId = ToolUtil.getSurFaceId();
                    re.setDateFormat(DateUtil.YYYY_MM_DD_HH_MM_SS);
                    bean.put("id", rowId);//id
                    bean.put("emailState", "2");//邮件状态 0.草稿  1.正常  2.已删除
                    re.setAttachPath(basePath);//设置附件保存基础路径
                    enclosureBeans.addAll(re.saveAttachMent((Part) message[i], rowId));//保存附件
                    beans.add(bean);
                }
                if (beans.size() >= 20) {//每20条数据保存一次
                    if (!beans.isEmpty()) {
                        emailDao.insertEmailListToServer(beans, tenantId);
                    }
                    if (!enclosureBeans.isEmpty()) {
                        emailDao.insertEmailEnclosureListToServer(enclosureBeans, tenantId);
                    }
                    beans.clear();
                    enclosureBeans.clear();
                    emailHasMail = emailDao.queryEmailListByEmailAddress(username, EmailState.DELETE.getKey(), tenantId);
                }
            }
            if (!beans.isEmpty()) {
                emailDao.insertEmailListToServer(beans, tenantId);
            }
            if (!enclosureBeans.isEmpty()) {
                emailDao.insertEmailEnclosureListToServer(enclosureBeans, tenantId);
            }
            // 任务完成
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_SUCCESS, StrUtil.EMPTY);
        } catch (Exception e) {
            LOGGER.warn("get Trash acquisition failed, reason is {}.", e);
            // 任务失败
            MqSendUtil.comMQJobMation(jobId, MqConstants.JOB_TYPE_IS_FAIL, StrUtil.EMPTY);
        }
    }

}

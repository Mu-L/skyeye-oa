package com.skyeye.eve.servey.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.question.classenum.SendingStatus;
import com.skyeye.eve.question.entity.DwQuestionBank;
import com.skyeye.eve.servey.dao.DwSurveyMailInviteDao;
import com.skyeye.eve.servey.entity.DwSurveyMailInvite;
import com.skyeye.eve.servey.service.DwSurveyMailInviteService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Pattern;

@Service
@SkyeyeService(name = "问卷发送邮件服务管理", groupName = "问卷发送邮件服务管理")
public class DwSurveyMailInviteServiceImpl extends SkyeyeBusinessServiceImpl<DwSurveyMailInviteDao, DwSurveyMailInvite> implements DwSurveyMailInviteService {

    private static final String EMAIL_REGEX = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$";

    public static boolean isValidEmail(String email) {
        return Pattern.matches(EMAIL_REGEX, email);
    }

    @Override
    protected void createPrepose(DwSurveyMailInvite entity) {
        Integer audit = entity.getAudit();
        if (audit == null) {
            throw new CustomException("审核状态不能为空");
        } else if (audit.equals(CommonNumConstants.NUM_ONE)) {
        } else {
            throw new CustomException("邮件未通过审核，无法发送");
        }
        Integer status = entity.getStatus();
        if (status == null) {
            throw new CustomException("状态不能为空");
        } else if (!status.equals(CommonNumConstants.NUM_ZERO) &&
            !status.equals(CommonNumConstants.NUM_ONE) &&
            !status.equals(CommonNumConstants.NUM_TWO) &&
            !status.equals(CommonNumConstants.NUM_THREE) &&
            !status.equals(CommonNumConstants.NUM_FOUR)) {
            throw new CustomException("状态不合法");
        }
        String dwSendUserMail = entity.getDwSendUserMail();
        if (StrUtil.isEmpty(dwSendUserMail)) {
            throw new CustomException("发送人邮箱不能为空");
        }
        // 检查邮箱是否符合正则表达式规则
        if (!isValidEmail(dwSendUserMail)) {
            throw new CustomException("发送人邮箱格式不正确");
        }
        entity.setSendNum(CommonNumConstants.NUM_ZERO);
        entity.setSuccessNum(CommonNumConstants.NUM_ZERO);
        entity.setFailNum(CommonNumConstants.NUM_ZERO);
    }

    @Override
    public String createEntity(DwSurveyMailInvite entity, String userId) {
        String errorMsg = entity.getError_msg();
        if (StrUtil.isNotEmpty(errorMsg)) {
            entity.setStatus(SendingStatus.SENDFAILED.getIndex());
        } else if (StrUtil.isEmpty(errorMsg)) {
            entity.setStatus(SendingStatus.SENT.getIndex());
        }
        return errorMsg;
    }

    @Override
    public void queryDwSurveyMailInviteList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyMailInvite> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwQuestionBank::getCreateTime));
        List<DwSurveyMailInvite> dwSurveyMailInvites = list(queryWrapper);
        iAuthUserService.setName(dwSurveyMailInvites, "createId", "createName");
        iAuthUserService.setName(dwSurveyMailInvites, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(dwSurveyMailInvites);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryMyDwSurveyMailInviteList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<DwSurveyMailInvite> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(DwSurveyMailInvite::getCreateId), InputObject.getLogParamsStatic().get("id").toString());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(DwSurveyMailInvite::getCreateTime));
        List<DwSurveyMailInvite> dwSurveyMailInviteList = list(queryWrapper);
        iAuthUserService.setName(dwSurveyMailInviteList, "createId", "createName");
        iAuthUserService.setName(dwSurveyMailInviteList, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(dwSurveyMailInviteList);
        outputObject.settotal(page.getTotal());
    }
}

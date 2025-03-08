package com.skyeye.school.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.school.chat.dao.TalkRequestDao;
import com.skyeye.school.chat.entity.FriendRelationship;
import com.skyeye.school.chat.entity.TalkRequest;
import com.skyeye.school.chat.service.FriendRelationshipService;
import com.skyeye.school.chat.service.TalkRequestService;
import org.aspectj.weaver.ast.Var;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "好友申请管理", groupName = "好友申请管理")
public class TalkRequestServiceImpl extends SkyeyeBusinessServiceImpl<TalkRequestDao, TalkRequest> implements TalkRequestService {

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private FriendRelationshipService friendRelationshipService;

    @Override
    protected void createPrepose(TalkRequest entity) {
        try {
            String createTime = entity.getCreateTime();
            if (createTime == null || createTime.trim().isEmpty()) {
                throw new CustomException("createTime不能为空");
            }
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(createTime, formatter);
            // 直接计算并设置 LocalDateTime
            LocalDateTime newDateTime = dateTime.plusWeeks(1);
            entity.setExpireTime(newDateTime);
        } catch (DateTimeParseException e) {
            throw new CustomException("createTime格式不正确: " + e.getMessage());
        } catch (Exception e) {
            throw new CustomException("处理过期时间失败: " + e.getMessage());
        }
        entity.setStatus(CommonNumConstants.NUM_ZERO);
    }


    @Override
    protected void createPostpose(TalkRequest entity, String userId) {
        String recipientId = entity.getRecipientId();
        String applicantId = entity.getApplicantId();
        Integer status = entity.getStatus();
        friendRelationshipService.addFriendRelationship(entity.getId(),applicantId, recipientId,status,entity.getCreateId());
    }

    @Override
    public void queryTalkRequest(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<TalkRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkRequest::getCreateTime));
        List<TalkRequest> talkRequestList = list(queryWrapper);
        for (TalkRequest talkRequest : talkRequestList) {
            String recipientId = talkRequest.getRecipientId();
            Map<String, Object> userInfo = iAuthUserService.queryDataMationById(recipientId);
            talkRequest.setRecipientMation(userInfo);
        }
        outputObject.setBean(talkRequestList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    @Transactional
    public void changeFriendStatus(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String userId = map.get("id").toString();
        String status = map.get("status").toString();
        UpdateWrapper<TalkRequest> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, userId);
        updateWrapper.set(MybatisPlusUtil.toColumns(TalkRequest::getStatus), status);
        update(updateWrapper);
        friendRelationshipService.changeFriendStatus(userId,status);
    }
}


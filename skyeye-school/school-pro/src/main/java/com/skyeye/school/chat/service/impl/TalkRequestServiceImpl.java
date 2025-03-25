package com.skyeye.school.chat.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.school.chat.classenum.ChatType;
import com.skyeye.school.chat.dao.TalkRequestDao;
import com.skyeye.school.chat.entity.TalkRequest;
import com.skyeye.school.chat.service.FriendRelationshipService;
import com.skyeye.school.chat.service.TalkRequestService;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.student.entity.Student;
import com.skyeye.school.student.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeParseException;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
@SkyeyeService(name = "好友申请管理", groupName = "好友申请管理")
public class TalkRequestServiceImpl extends SkyeyeBusinessServiceImpl<TalkRequestDao, TalkRequest> implements TalkRequestService {

    @Autowired
    private StudentService studentService;

    @Autowired
    private FriendRelationshipService friendRelationshipService;

    @Autowired
    private SchoolCommonService schoolCommonService;

    @Override
    protected void createPrepose(TalkRequest entity) {
        try {
            String createTime = entity.getCreateTime();
            if (StrUtil.isEmpty(createTime)) {
                throw new CustomException("createTime不能为空");
            }
            Date date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(createTime);
            Date afDate = DateUtil.getAfDate(date, 7, "d");
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String formattedAfDate = sdf.format(afDate); // 格式化为 MySQL 所需的格式
            entity.setExpireTime(formattedAfDate);
        } catch (DateTimeParseException e) {
            throw new CustomException("时间格式不正确: " + e.getMessage());
        } catch (Exception e) {
            throw new CustomException("处理过期时间失败: " + e.getMessage());
        }
        entity.setStatus(ChatType.PENDING_REQUEST.getIndex());
        String recipientId = entity.getRecipientId();//被申请人Id
        String applicantId = entity.getApplicantId();//申请人Id
        QueryWrapper<TalkRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.and(wrapper ->
            wrapper.or(wrapperOr -> wrapperOr
                    .eq(MybatisPlusUtil.toColumns(TalkRequest::getRecipientId), recipientId)
                    .eq(MybatisPlusUtil.toColumns(TalkRequest::getApplicantId), applicantId))
                .or(wrapperOr -> wrapperOr
                    .eq(MybatisPlusUtil.toColumns(TalkRequest::getRecipientId), applicantId)
                    .eq(MybatisPlusUtil.toColumns(TalkRequest::getApplicantId), recipientId)));
        List<TalkRequest> talkRequestList = list(queryWrapper);
        if (CollectionUtil.isNotEmpty(talkRequestList)) {
            throw new CustomException("禁止重新添加好友");
        }
    }

    @Override
    protected void createPostpose(TalkRequest entity, String userId) {
        String recipientId = entity.getRecipientId();
        String applicantId = entity.getApplicantId();
        Integer status = entity.getStatus();
        friendRelationshipService.addFriendRelationship(entity.getId(), applicantId, recipientId, status, entity.getCreateId());
    }

    @Override
    public void queryTalkRequestByRecipient(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<TalkRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkRequest::getCreateTime));
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkRequest::getRecipientId), commonPageInfo.getHolderId());
        String state = commonPageInfo.getState();
        if (StrUtil.isNotEmpty(state)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TalkRequest::getStatus), state);
        }
        List<TalkRequest> talkRequestList = list(queryWrapper);
        for (TalkRequest talkRequest : talkRequestList) {
            String applicantId = talkRequest.getApplicantId();
            UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(applicantId);
            Boolean userOrStudent1 = userOrStudent.getUserOrStudent();
            Map<String, Object> maps = userOrStudent.getDataMation();
            if (userOrStudent1) {
                if (ObjectUtil.isNotEmpty(maps)) {
                    talkRequest.setStudentApplicantMation(maps);
                }
            }
            if (!userOrStudent1) {
                if (ObjectUtil.isNotEmpty(maps)) {
                    talkRequest.setTeacherApplicantMation(maps);
                }
            }
        }
        outputObject.setBeans(talkRequestList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryTalkRequestByApplicant(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<TalkRequest> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(TalkRequest::getCreateTime));
        queryWrapper.eq(MybatisPlusUtil.toColumns(TalkRequest::getApplicantId), commonPageInfo.getHolderId());
        if (StrUtil.isNotEmpty(commonPageInfo.getState())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TalkRequest::getStatus), commonPageInfo.getState());
        }
        List<TalkRequest> talkRequestList = list(queryWrapper);
        for (TalkRequest talkRequest : talkRequestList) {
            UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(talkRequest.getRecipientId());
            if (userOrStudent.getUserOrStudent()) {
                talkRequest.setStudentRecipientMation(userOrStudent.getDataMation());
            } else {
                talkRequest.setTeacherRecipientMation(userOrStudent.getDataMation());
            }
        }
        outputObject.setBeans(talkRequestList);
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
        friendRelationshipService.changeFriendStatus(userId, status);
    }

    @Override
    public void queryTalkRequestFriend(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString();
        Student student = studentService.selectById(id);
        outputObject.setBean(student);
    }
}


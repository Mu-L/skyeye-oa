package com.skyeye.notice.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.circle.entity.Circle;
import com.skyeye.circle.service.CircleService;
import com.skyeye.comment.service.CommentService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.joincircle.service.JoinCircleService;
import com.skyeye.notice.constants.NoticeContent;
import com.skyeye.notice.dao.NoticeDao;
import com.skyeye.notice.entity.Notice;
import com.skyeye.notice.noticeenum.NoticeTypeEnum;
import com.skyeye.notice.noticeenum.ReadEnum;
import com.skyeye.notice.noticeenum.TypeEnum;
import com.skyeye.notice.service.NoticeService;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import com.skyeye.post.entity.Post;
import com.skyeye.post.service.PostService;
import com.skyeye.user.service.UserService;
import com.skyeye.video.service.VideoService;
import com.skyeye.videocomment.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: NoticeServiceImpl
 * @Description: 通知信息服务实现层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/24 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "通知信息管理", groupName = "通知信息管理")
public class NoticeServiceImpl extends SkyeyeBusinessServiceImpl<NoticeDao, Notice> implements NoticeService {

    @Autowired
    private UserService userService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Autowired
    private PostService postService;

    @Autowired
    private VideoService videoService;

    @Autowired
    private JoinCircleService joinCircleService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private VideoCommentService videoCommentService;

    @Autowired
    private CircleService circleService;

    private Notice setUserMation(Notice notice) {
        if (notice.getType() == TypeEnum.COMMENT.getKey()) {
            Picture picture = pictureService.getPictureByObjectId(notice.getCommentId());
            notice.setPicture(picture);
        }
        String sendId = notice.getSendId();
        String receiveId = notice.getReceiveId();
        if (userService.checkCreateIdIsStudent(sendId)) {
            iAuthUserService.setDataMation(notice, Notice::getSendId);
        } else {
            userService.setDataMation(notice, Notice::getSendId);
        }
        if (userService.checkCreateIdIsStudent(receiveId)) {
            iAuthUserService.setDataMation(notice, Notice::getReceiveId);
        } else {
            userService.setDataMation(notice, Notice::getReceiveId);
        }
        return notice;
    }

    @Override
    protected void createPrepose(Notice entity) {
        entity.setState(ReadEnum.UNREAD.getKey());
    }

    @Override
    public String createEntity(Notice entity, String userId) {
        // 如果接收者是本人，直接不新增通知
        if (userId.equals(entity.getReceiveId())) {
            return StrUtil.EMPTY;
        }
        if (StrUtil.isNotEmpty(entity.getCircleId())) {
            // 判断接收人是否还在圈子中--不在则不通知
            Boolean isJoin = joinCircleService.checkIsJoinCircle(entity.getCircleId(), entity.getReceiveId());
            if (!isJoin) {
                return StrUtil.EMPTY;
            }
        }
        // 如果是点赞，只通知一次
        if (entity.getType() == TypeEnum.LIKE.getKey()) {
            QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getSendId), entity.getSendId());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getType), TypeEnum.LIKE.getKey());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getObjectId), entity.getObjectId());
            if (StrUtil.isNotEmpty(entity.getCommentId())) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getCommentId), entity.getCommentId());
            }
            long length = count(queryWrapper);
            if (length > CommonNumConstants.NUM_ZERO) {
                return StrUtil.EMPTY;
            }
        }
        return super.createEntity(entity, userId);
    }

    private List<Notice> filterNotice(List<Notice> entity, String userId) {
        if (CollectionUtil.isEmpty(entity)) {
            return new ArrayList();
        }
        // 使用stream流过滤receiveId = userId
        List<Notice> beans = entity.stream().filter(notice -> !Objects.equals(notice.getReceiveId(), userId)).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(beans)) {
            return new ArrayList();
        }
        if(StrUtil.isNotEmpty(beans.get(CommonNumConstants.NUM_ZERO).getCircleId())){
            // 分享圈子——去除不在圈子用户
            List<String> receiveIds = beans.stream().map(Notice::getReceiveId).collect(Collectors.toList());
            Map<String,Boolean> isJoinCircleMap = joinCircleService.checkIsJoinCircle(beans.get(CommonNumConstants.NUM_ZERO).getCircleId(),receiveIds);
            beans = beans.stream().filter(notice -> isJoinCircleMap.get(notice.getReceiveId())).collect(Collectors.toList());
            if(CollectionUtil.isEmpty(beans)){
                throw new CustomException("分享的用户不在圈子中");
            }
        }
        return beans;
    }

    private void setNoticeOtherInfo(Notice notice) {
        if (StrUtil.isNotEmpty(notice.getCommentId())) {
            // 评论信息
            if (Objects.equals(notice.getCommentKey(), commentService.getServiceClassName())) {
                commentService.setDataMation(notice, Notice::getCommentId);
            } else if (Objects.equals(notice.getCommentKey(), videoCommentService.getServiceClassName())) {
                videoCommentService.setDataMation(notice, Notice::getCommentId);
            }
        }
        // 帖子或视频
        if (Objects.equals(notice.getObjectKey(), postService.getServiceClassName())) {
            postService.setDataMation(notice, Notice::getObjectId);
        } else if (Objects.equals(notice.getObjectKey(), videoService.getServiceClassName())) {
            videoService.setDataMation(notice, Notice::getObjectId);
        }
        // 圈子
        if (StrUtil.isNotEmpty(notice.getCircleId())) {
            circleService.setDataMation(notice, Notice::getCircleId);
        }
    }

    @Override
    public Notice selectById(String id) {
        Notice notice = super.selectById(id);
        // 设置通知其他内容
        setNoticeOtherInfo(notice);
        return setUserMation(notice);
    }

    @Override
    protected void deletePreExecution(Notice entity) {
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        if (!userId.equals(entity.getReceiveId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    public void queryNoticeByType(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            Integer type = Integer.valueOf(commonPageInfo.getType());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getNoticeType), type);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId)
                .orderByDesc(MybatisPlusUtil.toColumns(Notice::getCreateTime));
        List<Notice> bean = list(queryWrapper);
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }
        List<Notice> beans = bean.stream().map(this::setUserMation).collect(Collectors.toList());
        for (Notice notice : beans) {
            setNoticeOtherInfo(notice);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void updateStateById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get(CommonConstants.ID).toString();
        UpdateWrapper<Notice> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Notice::getState), ReadEnum.READ.getKey());
        update(updateWrapper);
    }

    @Override
    public void queryUnReadNum(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getParams().get(CommonConstants.ID).toString();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getState), ReadEnum.UNREAD.getKey());
        List<Notice> bean = list(queryWrapper);
        // 按分类分组
        Map<Integer, List<Notice>> map = bean.stream().collect(Collectors.groupingBy(Notice::getNoticeType));
        // 计算各组数量
        Map<Integer, Long> countMap = map.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> (long) e.getValue().size()));
        outputObject.setBean(countMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    /**
     * 删除视频评论时候把通知删除
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteVideoNoticeByCommentIds(List<String> commentIds) {
        if (CollectionUtils.isEmpty(commentIds)) {
            return;
        }
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Notice::getCommentId), commentIds);
        List<Notice> bean = list(queryWrapper);
        for (Notice notice : bean) {
            notice.setContent(NoticeContent.COMMENT_DELETE);
        }
        updateEntity(bean, userId);
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void sharePostOrComment(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        String userId = params.get("userId").toString();
        String[] userIds = userId.split(",");
        String postId = params.get("postId").toString();
        Post post = postService.selectById(postId);
        if (StrUtil.isEmpty(post.getId())) {
            throw new CustomException("该帖子不存在");
        }
        Notice notice = new Notice();
        if (params.containsKey("description") && StrUtil.isNotEmpty(params.get("description").toString())) {
            notice.setDescription(params.get("description").toString());
        }
        notice.setObjectId(postId);
        notice.setSendId(currentUserId);
        notice.setType(TypeEnum.SHARE.getKey());
        notice.setObjectKey(postService.getServiceClassName());
        if (StrUtil.isNotEmpty(post.getCircleId())) {
            // 圈子
            notice.setCircleId(post.getCircleId());
            notice.setNoticeType(NoticeTypeEnum.TYPE_CIRCLE.getKey());
        } else {
            notice.setNoticeType(NoticeTypeEnum.TYPE_WALL.getKey());
        }
        if (params.containsKey("commentId") && StrUtil.isNotEmpty(params.get("commentId").toString())) {
            notice.setCommentId(params.get("commentId").toString());
            notice.setContent(NoticeContent.SHARE_COMMENT);
        } else {
            notice.setContent(NoticeContent.SHARE_POST);
        }
        List<Notice> notices = new ArrayList<>();
        for (String id : userIds) {
            if (StrUtil.isEmpty(id)) {
                continue;
            }
            Notice item = new Notice();
            BeanUtil.copyProperties(notice, item);
            item.setReceiveId(id);
            notices.add(item);
        }
        List<Notice> beans = filterNotice(notices, currentUserId);
        if(CollectionUtil.isEmpty(beans)){
            createEntity(beans, currentUserId);
            postService.updatePostShareNum(postId,beans.size());
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void shareVideoOrComment(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        String userId = params.get("userId").toString();
        String[] userIds = userId.split(",");
        String videoId = params.get("videoId").toString();
        Notice notice = new Notice();
        if (params.containsKey("description") && StrUtil.isNotEmpty(params.get("description").toString())) {
            notice.setDescription(params.get("description").toString());
        }
        notice.setSendId(currentUserId);
        notice.setObjectId(videoId);
        notice.setType(TypeEnum.SHARE.getKey());
        notice.setObjectKey(videoService.getServiceClassName());
        notice.setNoticeType(NoticeTypeEnum.TYPE_VIDEO.getKey());
        if (params.containsKey("commentId") && StrUtil.isNotEmpty(params.get("commentId").toString())) {
            notice.setCommentId(params.get("commentId").toString());
            notice.setContent(NoticeContent.SHARE_COMMENT);
            notice.setCommentKey(videoCommentService.getServiceClassName());
        } else {
            notice.setContent(NoticeContent.SHARE_VIDEO);
        }
        List<Notice> notices = new ArrayList<>();
        for (String id : userIds) {
            if (StrUtil.isEmpty(id)) {
                continue;
            }
            Notice item = new Notice();
            BeanUtil.copyProperties(notice, item);
            item.setReceiveId(id);
            notices.add(item);
        }
        List<Notice> beans = filterNotice(notices, currentUserId);
        if(CollectionUtil.isEmpty(beans)){
            createEntity(notices, currentUserId);
            videoService.updateVideoShareNum(videoId,beans.size());
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void shareCircle(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String currentUserId = InputObject.getLogParamsStatic().get("id").toString();
        String userId = params.get("userId").toString();
        String[] userIds = userId.split(",");
        String circleId = params.get("circleId").toString();

        Notice notice = new Notice();
        if (params.containsKey("description") && StrUtil.isNotEmpty(params.get("description").toString())) {
            notice.setDescription(params.get("description").toString());
        }
        notice.setSendId(currentUserId);
        notice.setCircleId(circleId);
        notice.setType(TypeEnum.SHARE.getKey());
        notice.setNoticeType(NoticeTypeEnum.TYPE_CIRCLE.getKey());
        notice.setContent(NoticeContent.SHARE_CIRCLE);
        List<Notice> notices = new ArrayList<>();
        for (String id : userIds) {
            if (StrUtil.isEmpty(id)) {
                continue;
            }
            Notice item = new Notice();
            BeanUtil.copyProperties(notice, item);
            item.setReceiveId(id);
            notices.add(item);
        }
        List<Notice> beans = filterNotice(notices, currentUserId);
        if(CollectionUtil.isEmpty(beans)){
            createEntity(notices, currentUserId);
            circleService.updateCircleShareNum(circleId,beans.size());
        }
    }

    /**
     * 删除帖子、视频之后将修改通知内容
     */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteByObjectId(String id, String serviceClassName) {
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getObjectId), id);
        queryWrapper.isNull(MybatisPlusUtil.toColumns(Notice::getCommentId));
        List<Notice> bean = list(queryWrapper);
        if (CollectionUtil.isEmpty(bean)) {
            return;
        }
        String userId = InputObject.getLogParamsStatic().get(id).toString();
        String content = StrUtil.EMPTY;
        if (videoService.getServiceClassName().equals(serviceClassName)) {
            content = NoticeContent.DELETE_VIDEO;
        } else if (postService.getServiceClassName().equals(serviceClassName)) {
            content = NoticeContent.POST_DELETE;
        }
        for (Notice notice : bean) {
            notice.setContent(content);
        }
        updateEntity(bean, userId);
    }

    @Override
    public void updateAllNoticeRead(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<Notice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getReceiveId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Notice::getState), ReadEnum.UNREAD.getKey());
        List<Notice> bean = list(queryWrapper);
        List<Notice> collect = bean.stream().map(notice -> {
            notice.setState(ReadEnum.READ.getKey());
            return notice;
        }).collect(Collectors.toList());
        updateEntity(collect, userId);
    }

}

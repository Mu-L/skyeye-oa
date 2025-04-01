package com.skyeye.videocomment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.WallConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.notice.service.NoticeService;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import com.skyeye.upvote.entity.Upvote;
import com.skyeye.upvote.service.UpvoteService;
import com.skyeye.user.service.UserService;
import com.skyeye.user.userenum.LoginIdentity;
import com.skyeye.video.entity.Video;
import com.skyeye.video.service.VideoService;
import com.skyeye.videocomment.dao.VideoCommentDao;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videocomment.service.VideoCommentService;
import com.xxl.job.core.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: VideoCommentServiceImpl
 * @Description: 视频评论业务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频评论管理", groupName = "视频评论管理")
public class VideoCommentServiceImpl extends SkyeyeBusinessServiceImpl<VideoCommentDao, VideoComment> implements VideoCommentService {

    @Autowired
    private VideoService videoService;

    @Autowired
    private VideoCommentService videoCommentService;

    @Autowired
    private UserService userService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private NoticeService noticeService;

    @Override
    public void createEntity(InputObject inputObject, OutputObject outputObject) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isEmpty(userToken)) {
            throw new CustomException("请先完成登录！");
        }
        super.createEntity(inputObject, outputObject);
    }

    @Override
    public void createPrepose(VideoComment entity) {
        String userIdentity = PutObject.getRequest().getHeader(WallConstants.USER_IDENTITY_KEY);
        entity.setLoginIdentity(userIdentity);
        entity.setIp(IpUtil.getLocalAddress().toString());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void createPostpose(VideoComment entity, String userId) {
        String videoId = entity.getVideoId();
        Video video = videoService.selectById(videoId);
        Integer remarkNum = Integer.parseInt(video.getRemarkNum());
        remarkNum++;
        video.setRemarkNum(String.valueOf(remarkNum));
        videoService.updateEntity(video, userId);
        if (ObjectUtil.isNotEmpty(entity.getPicture())) {
            Picture picture = entity.getPicture();
            picture.setObjectId(entity.getId());
            pictureService.createEntity(entity.getPicture(), userId);
        }
    }

    @Override
    protected void deletePostpose(VideoComment entity) {
        super.deletePostpose(entity);
        String id = entity.getId();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoComment::getParentId), id);
        List<VideoComment> videoComments = list(queryWrapper);
        remove(queryWrapper);
        List<String> ids = videoComments.stream().map(VideoComment::getId).collect(Collectors.toList());
        pictureService.deleteByCommentIds(ids);
        String videoId = entity.getVideoId();
        //根据 videoId 获取评论数量
        Video video = videoService.selectById(videoId);
        Integer videoRemarkNum = Integer.parseInt(video.getRemarkNum());
        videoRemarkNum = videoRemarkNum - videoComments.size() - 1;
        video.setRemarkNum(String.valueOf(videoRemarkNum));
        videoService.updateEntity(video, userId);
        // TODO 通知管理
//        noticeService.deleteVideoNoticeByCommentIds(ids);
    }

    private void setCommentPicture(List<VideoComment> list) {
        List<String> ids = list.stream().map(VideoComment::getId).collect(Collectors.toList());
        Map<String, List<Picture>> pictureMapListByIds = pictureService.getPictureMapListByIds(ids);
        for (VideoComment videoComment : list) {
            List<Picture> pictures = pictureMapListByIds.get(videoComment.getId());
            if (CollectionUtil.isNotEmpty(pictures)) {
                videoComment.setPicture(pictures.get(CommonNumConstants.NUM_ZERO));
            }
        }
    }

    private void checkUpvote(List<VideoComment> list, String userId) {
        List<String> ids = list.stream().map(VideoComment::getId).collect(Collectors.toList());
        String[] commentIds = ids.toArray(new String[0]);
        Map<String, Boolean> stringBooleanMap = upvoteService.checkUpvote(userId, commentIds);
        for (VideoComment videoComment : list) {
            videoComment.setCheckUpvote(stringBooleanMap.get(videoComment.getId()));
        }
    }

    @Override
    public void queryCommentListByVideoId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String videoId = commonPageInfo.getObjectId();
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        String userId;
        if(StrUtil.isEmpty(userToken)){
            userId = null;
        }else {
            userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        }
        if (StrUtil.isEmpty(videoId)) {
            throw new CustomException("视频id(objectId)不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoComment::getVideoId), videoId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoComment::getCreateTime));
        List<VideoComment> videoCommentList = list(queryWrapper);
        if (CollectionUtil.isEmpty(videoCommentList)) {
            return;
        }
        setCommentPicture(videoCommentList);
        checkUpvote(videoCommentList, userId);
        List<VideoComment> bean = videoCommentList.stream().map(videoComment -> {
            if (LoginIdentity.STUDENT.getKey().equals(videoComment.getLoginIdentity())) {
                userService.setDataMation(videoComment, VideoComment::getCreateId);
            } else {
                iAuthUserService.setDataMation(videoComment, VideoComment::getCreateId);
            }
            return videoComment;
        }).collect(Collectors.toList());
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void supportOrNotComment(InputObject inputObject, OutputObject outputObject) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isEmpty(userToken)) {
            throw new CustomException("请先完成登录！");
        }
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        String commentId = inputObject.getParams().get("commentId").toString();
        Map<String, Boolean> stringBooleanMap = upvoteService.checkUpvote(userId, commentId);
        if (!stringBooleanMap.get(commentId)) {
            // 该用户没有对这个评论进行点赞
            VideoComment videoComment = selectById(commentId);
            int supportNum = Integer.parseInt(videoComment.getUpvoteNum()) + CommonNumConstants.NUM_ONE;
            videoComment.setUpvoteNum(Integer.toString(supportNum));
            updateById(videoComment);
            // 保存点赞记录
            Upvote upvote = new Upvote();
            upvote.setObjectId(commentId);
            upvote.setUserId(userId);
            upvote.setObjectKey(videoCommentService.getServiceClassName());
            upvote.setCreateTime(LocalDateTime.now().toString());
            upvoteService.createEntity(upvote, null);
        } else {
            // 该用户已经对这个评论进行点赞
            VideoComment videoComment = selectById(commentId);
            int supportNum = Integer.parseInt(videoComment.getUpvoteNum()) - CommonNumConstants.NUM_ONE;
            videoComment.setUpvoteNum(Integer.toString(supportNum));
            updateById(videoComment);
            // 删除点赞记录
            upvoteService.deleteUpvoteByObjectId(userId, commentId);
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deleteByVideoId(String id) {
        QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoComment::getVideoId), id);
        List<VideoComment> list = list(queryWrapper);
        remove(queryWrapper);
        List<String> ids = list.stream().map(VideoComment::getId).collect(Collectors.toList());
        pictureService.deleteByCommentIds(ids);
    }

    @Override
    public void deletePreExecution(VideoComment entity) {
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        Video video = videoService.selectById(entity.getVideoId());
        if (video.getCreateId().equals(userId)) {
            return;
        }
        if (!userId.equals(entity.getCreateId())) {
            throw new CustomException("无权限");
        }
    }
}

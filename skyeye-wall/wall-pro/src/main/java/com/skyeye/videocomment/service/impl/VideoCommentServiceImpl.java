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
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.notice.service.NoticeService;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import com.skyeye.upvote.entity.Upvote;
import com.skyeye.upvote.service.UpvoteService;
import com.skyeye.user.service.UserService;
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
    public void validatorEntity(VideoComment entity) {
        String parentId = entity.getParentId();
        // 判断传入的parentId是否为空
        VideoComment videoComment = videoCommentService.selectById(parentId);
        if (StrUtil.isNotEmpty(videoComment.getParentId())) {
            throw new CustomException("不可评论");
        }
    }

    @Override
    public void createPrepose(VideoComment entity) {
        entity.setIp(IpUtil.getLocalAddress().toString());
    }

    // 新增评论 事务 评论+1
    @Transactional
    @Override
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

    @Transactional
    @Override
    public void deleteById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String id = params.get("id").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        VideoComment videoComment = selectById(id);
        //查询子数据并删除
        QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoComment::getParentId), id);
        List<VideoComment> videoComments = list(queryWrapper);
        List<String> ids = videoComments.stream().map(VideoComment::getId).collect(Collectors.toList());
        ids.add(id);
        remove(queryWrapper);
        deleteById(id);
        pictureService.deleteByCommentIds(ids);
        String videoId = videoComment.getVideoId();
        //根据 videoId 获取评论数量
        Video video = videoService.selectById(videoId);
        Integer videoRemarkNum = Integer.parseInt(video.getRemarkNum());
        videoRemarkNum = videoRemarkNum - videoComments.size() - 1;
        video.setRemarkNum(String.valueOf(videoRemarkNum));
        videoService.updateEntity(video, userId);
        noticeService.deleteVideoNoticeByCommentIds(ids);
    }

    private void setCommentPicture(List<VideoComment> list) {
        List<String> ids = list.stream().map(VideoComment::getId).collect(Collectors.toList());
        Map<String, List<Picture>> pictureMapListByIds = pictureService.getPictureMapListByIds(ids);
        for (VideoComment videoComment : list) {
            List<Picture> pictures = pictureMapListByIds.get(videoComment.getId());
            if(CollectionUtil.isNotEmpty(pictures)){
                videoComment.setPicture(pictures.get(CommonNumConstants.NUM_ZERO));
            }
        }
    }

    private void checkUpvote(List<VideoComment> list, String userId) {
        List<String> ids = list.stream().map(VideoComment::getCreateId).collect(Collectors.toList());
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
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        if (StrUtil.isEmpty(videoId)) {
            throw new CustomException("视频id(objectId)不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoComment::getVideoId), videoId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoComment::getCreateTime));
        List<VideoComment> videoCommentList = list(queryWrapper);
        if (CollectionUtil.isEmpty(videoCommentList)) {
            throw new CustomException("该视频暂无评论");
        }
        setCommentPicture(videoCommentList);
        checkUpvote(videoCommentList, userId);
        try {
            userService.setDataMation(videoCommentList, VideoComment::getCreateId);
        }catch (Exception e){
            iAuthUserService.setDataMation(videoCommentList, VideoComment::getCreateId);
        }
        outputObject.setBeans(videoCommentList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void supportOrNotComment(InputObject inputObject, OutputObject outputObject) {
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
            upvoteService.deleteUpvoteByObjectId(userId,commentId);
        }
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

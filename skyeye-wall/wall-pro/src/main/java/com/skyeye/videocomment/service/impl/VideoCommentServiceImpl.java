package com.skyeye.videocomment.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.user.service.UserService;
import com.skyeye.video.entity.Video;
import com.skyeye.video.service.VideoService;
import com.skyeye.videocomment.dao.VideoCommentDao;
import com.skyeye.videocomment.entity.VideoComment;
import com.skyeye.videocomment.service.VideoCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

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

    @Override
    public void validatorEntity(VideoComment entity) {
        String parentId = entity.getParentId();
        // 判断传入的parentId是否为空
        VideoComment videoComment = videoCommentService.selectById(parentId);
        if (StrUtil.isNotEmpty(videoComment.getParentId())) {
            throw new CustomException("不可评论");
        }
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
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoComment::getParentId),id);
        List<VideoComment> videoComments = list(queryWrapper);
        remove(queryWrapper);
        super.deleteById(id);
        String videoId = videoComment.getVideoId();
        //根据 videoId 获取评论数量
        Video video = videoService.selectById(videoId);
        Integer videoRemarkNum = Integer.parseInt(video.getRemarkNum());
        videoRemarkNum = videoRemarkNum-videoComments.size()-1;
        video.setRemarkNum(String.valueOf(videoRemarkNum));
        videoService.updateEntity(video, userId);
    }

    @Override
    protected List<Map<String, Object>> queryDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryDataList(inputObject);
        userService.setMationForMap(beans, "createId", "createMation");
        userService.setMationForMap(beans, "userId", "userMation");
        return beans;
    }
}

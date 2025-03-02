package com.skyeye.video.service.impl;

import cn.hutool.core.collection.CollectionUtil;
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
import com.skyeye.exception.CustomException;
import com.skyeye.user.service.UserService;
import com.skyeye.video.dao.VideoDao;
import com.skyeye.video.entity.Video;
import com.skyeye.video.entity.VideoRecord;
import com.skyeye.video.service.VideoRecordService;
import com.skyeye.video.service.VideoService;
import org.joda.time.LocalDateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: VideoRecordServiceImpl
 * @Description: 视频管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频管理", groupName = "视频管理")
public class VideoServiceImpl extends SkyeyeBusinessServiceImpl<VideoDao, Video> implements VideoService {

    @Autowired
    private VideoRecordService videoRecordService;

    @Autowired
    private UserService userService;


    @Override
    public Video selectById(String id) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Video video = super.selectById(id);
        setCheckUpvote(video,userId);
        setCheckCollection(video,userId);
        userService.setDataMation(video, Video::getCreateId);
        return video;
    }

    // 检验当前登录人是否对视频点赞
    private void setCheckUpvote(Video video, String userId) {
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), video.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), CommonNumConstants.NUM_ONE);
        video.setCheckUpvote(videoRecordService.count(queryWrapper) > 0);
    }

    private void checkUpvote(List<Video> videoList,String userId) {
        for (Video video : videoList) {
            setCheckUpvote(video, userId);
        }
    }

    // 检验当前登录人是否对视频收藏
    private void setCheckCollection(Video video, String userId) {
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), video.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), CommonNumConstants.NUM_TWO);
        video.setCheckCollection(videoRecordService.count(queryWrapper) > 0);
    }

    private void checkCollection(List<Video> videoList,String userId) {
        for (Video video : videoList) {
            setCheckCollection(video, userId);
        }
    }

    @Override
    public void queryMyVideoList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Video::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getCreateTime));
        List<Video> list = this.list(queryWrapper);
        // 检验当前登录人是否点赞
        checkUpvote(list, userId);
        // 检验当前登录人是否收藏
        checkCollection(list, userId);
        userService.setDataMation(list,Video::getCreateId);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }



    /**
     * 点赞或取消点赞
     */
    @Override
    @Transactional
    public void supportOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Video video = selectById(videoId);
        int supportNum = Integer.parseInt(video.getTasnNum());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), videoId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        // 过滤到单一的点赞视频
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_ONE)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(videoRecordList)) {
            //  不为空证明已经点赞了——进行取消点赞——删除记录表 点赞数减1
            supportNum--;
            video.setTasnNum(String.valueOf(supportNum));
            updateById(video);
            videoRecordService.removeById(videoRecordList.get(0).getId());
        } else {
            // 点赞数加1
            supportNum++;
            video.setTasnNum(String.valueOf(supportNum));
            updateById(video);
            // 为空则进行点赞记录
            VideoRecord videoRecord = new VideoRecord();
            videoRecord.setVideoId(videoId);
            videoRecord.setUserId(userId);
            videoRecord.setCtFlag(CommonNumConstants.NUM_ONE);
            videoRecord.setCreateTime(LocalDateTime.now().toString());
            videoRecordService.createEntity(videoRecord, userId);
        }
    }

    @Override
    @Transactional
    public void collectOrNotVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Video video = selectById(videoId);
        int collectNum = Integer.parseInt(video.getCollectionNum());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), videoId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_TWO)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(videoRecordList)) {
            // 不为空——取消收藏——删除记录,收藏数-1
            collectNum--;
            video.setCollectionNum(String.valueOf(collectNum));
            updateById(video);
            videoRecordService.removeById(videoRecordList.get(0).getId());
        } else {
            // 收藏数加1
            collectNum++;
            video.setCollectionNum(String.valueOf(collectNum));
            updateById(video);
            // 为空则_进行收藏记录
            VideoRecord videoRecord = new VideoRecord();
            videoRecord.setVideoId(videoId);
            videoRecord.setUserId(userId);
            videoRecord.setCtFlag(CommonNumConstants.NUM_TWO);
            videoRecord.setCreateTime(LocalDateTime.now().toString());
            videoRecordService.createEntity(videoRecord, userId);
        }
    }

    @Override
    public void queryMySupportVideo(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag));
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_ONE)
                .collect(Collectors.toList());
        List<Video> supportList = new ArrayList<>();
        for (VideoRecord videoRecord : videoRecordList) {
            supportList.add(selectById(videoRecord.getVideoId()));
        }
        checkCollection(supportList,userId);
        userService.setDataMation(supportList, Video::getCreateId);
        outputObject.setBeans(supportList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryMyCollectVideo(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag));
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_TWO)
                .collect(Collectors.toList());
        List<Video> collectList = new ArrayList<>();
        for (VideoRecord videoRecord : videoRecordList) {
            collectList.add(selectById(videoRecord.getVideoId()));
        }
        checkUpvote(collectList,userId);
        userService.setDataMation(collectList, Video::getCreateId);
        outputObject.setBeans(collectList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void refreshVisitVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        Video video = selectById(videoId);
        int visitNum = Integer.parseInt(video.getVisitNum());
        visitNum++;
        video.setVisitNum(String.valueOf(visitNum));
        updateById(video);
    }

    @Override
    public void queryAllVideoList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getVisitNum));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getTasnNum));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getCollectionNum));
        List<Video> bean = list(queryWrapper);
        checkUpvote(bean,userId);
        checkCollection(bean,userId);
        userService.setDataMation(bean, Video::getCreateId);
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }

    @Override
    protected void deletePreExecution(Video entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!userId.equals(entity.getCreateId())) {
            throw new CustomException("无权限");
        }
    }
}

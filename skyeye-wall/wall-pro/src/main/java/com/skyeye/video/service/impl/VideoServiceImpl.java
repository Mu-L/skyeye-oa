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
import com.skyeye.eve.service.IAuthUserService;
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
    private IAuthUserService iAuthUserService;


    @Override
    public Video selectById(String id) {
        Video video = super.selectById(id);
        iAuthUserService.setDataMation(video,Video::getCreateId);
        return video;
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
        outputObject.setBeans(list);
        outputObject.settotal(page.size());
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
        int supportNum = video.getTasnNum();
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
            video.setTasnNum(supportNum);
            updateById(video);
            videoRecordService.removeById(videoRecordList.get(0).getId());
        } else {
            // 点赞数加1
            supportNum++;
            video.setTasnNum(supportNum);
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
        int collectNum = video.getCollectionNum();
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), videoId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        List<VideoRecord> videoRecordList = videoRecordService.list(queryWrapper).stream()
                .filter(item -> item.getCtFlag() == CommonNumConstants.NUM_TWO)
                .collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(videoRecordList)) {
            // 不为空——取消收藏——删除记录,收藏数-1
            collectNum--;
            video.setCollectionNum(collectNum);
            updateById(video);
            videoRecordService.removeById(videoRecordList.get(0).getId());
        } else {
            // 收藏数加1
            collectNum++;
            video.setCollectionNum(collectNum);
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
                .collect(Collectors.toList());;
        List<Video> supportList = new ArrayList<>();
        for (VideoRecord videoRecord : videoRecordList) {
            supportList.add(selectById(videoRecord.getVideoId()));
        }
        outputObject.setBeans(supportList);
        outputObject.settotal(page.size());
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
        outputObject.setBeans(collectList);
        outputObject.settotal(page.size());
    }

    @Override
    public void refreshVisitVideo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String videoId = map.get("videoId").toString();
        Video video = selectById(videoId);
        int visitNum = video.getVisitNum();
        visitNum++;
        video.setVisitNum(visitNum);
        updateById(video);
    }

    @Override
    public void queryAllVideoList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(),commonPageInfo.getLimit());
        QueryWrapper<Video> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getVisitNum));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getTasnNum));
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Video::getCollectionNum));
        List<Video> bean = list(queryWrapper);
        outputObject.setBeans(bean);
        outputObject.settotal(page.getTotal());
    }
}

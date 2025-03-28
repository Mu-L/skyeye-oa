package com.skyeye.video.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.video.dao.VideoRecordDao;
import com.skyeye.video.entity.Video;
import com.skyeye.video.entity.VideoRecord;
import com.skyeye.video.service.VideoRecordService;
import org.joda.time.LocalDateTime;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: VideoRecordServiceImpl
 * @Description: 视频记录管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频点赞收藏管理", groupName = "视频点赞收藏管理")
public class VideoRecordServiceImpl extends SkyeyeBusinessServiceImpl<VideoRecordDao, VideoRecord> implements VideoRecordService {

    /**
     * 检验当前登录人是否对视频点赞 或 收藏
     */
    @Override
    public boolean checkUpvoteOrCollectByUserId(Video video, int type) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), video.getId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), type);
        return count(queryWrapper) > 0;
    }

    /**
     * 检验当前登录人是否对视频点赞 或 收藏
     */
    @Override
    public Map<String, Boolean> checkUpvoteOrCollect(List<String> videoIds, int type){
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), videoIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), type);
        List<VideoRecord> list = list(queryWrapper);
        List<String> recordVideoIds = list.stream().map(VideoRecord::getVideoId).collect(Collectors.toList());
        Map<String,Boolean> map = new HashMap<>();
        for(String videoId : videoIds){
            map.put(videoId, recordVideoIds.contains(videoId));
        }
        return map;
    }

    /**
     * 分页
     * 获取我的，他的点赞视频id
     * 获取我的,他的收藏视频id
     * 获取全部点赞视频
     */
    @Override
    public Map<String, List<String>> queryAllCollectSupportVideoIds(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        // 用户id
        String objectId = commonPageInfo.getObjectId();
        String type = commonPageInfo.getType();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(objectId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), objectId);
        }
        if (StrUtil.isNotEmpty(type)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), Integer.parseInt(type));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoRecord::getCreateTime));
        List<VideoRecord> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return null;
        }
        Map<String, List<String>> map = new HashMap<>();
        map.put("videoIds", list.stream().map(VideoRecord::getVideoId).collect(Collectors.toList()));
        map.put("total", new ArrayList<>(Collections.singleton(String.valueOf(page.getTotal()))));
        return map;
    }


   /**
    *  取消点赞/收藏
    *  点赞/收藏
    * */
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public boolean checkSupportOrCollectByVideoId(String videoId, int type) {
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getUserId), userId)
                .eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), videoId)
                .eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), type);
        VideoRecord one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            VideoRecord videoRecord = new VideoRecord();
            videoRecord.setVideoId(videoId);
            videoRecord.setUserId(userId);
            videoRecord.setCtFlag(type);
            videoRecord.setCreateTime(LocalDateTime.now().toString());
            createEntity(videoRecord,userId);
            return false;
        }else {
            deleteById(one.getId());
            return true;
        }
    }

    @Override
    public List<VideoRecord> queryAllSupportOrCollect(int type) {
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getCtFlag), type);
        return list(queryWrapper);
    }

    @Override
    public void deleteByVideoId(String id) {
        QueryWrapper<VideoRecord> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoRecord::getVideoId), id);
        remove(queryWrapper);
    }
}
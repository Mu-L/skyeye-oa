package com.skyeye.video.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import com.skyeye.video.dao.VideoViewDao;
import com.skyeye.video.entity.VideoView;
import com.skyeye.video.service.VideoViewService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @ClassName: VideoViewServiceImpl
 * @Description: 视频观看记录业务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频浏览记录管理", groupName = "视频浏览记录管理")
public class VideoViewServiceImpl extends SkyeyeBusinessServiceImpl<VideoViewDao, VideoView> implements VideoViewService {

    @Override
    @Transactional
    public String createEntity(VideoView entity, String userId) {
        QueryWrapper<VideoView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoView::getVideoId), entity.getVideoId())
                .eq(MybatisPlusUtil.toColumns(VideoView::getUserId), userId);
        VideoView videoView = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(videoView)) {
            videoView.setViewCount(videoView.getViewCount() + CommonNumConstants.NUM_ONE);
            updateEntity(videoView, userId);
            return StrUtil.EMPTY;
        }
        return super.createEntity(entity, userId);
    }

    @Override
    public void deleteAllVideoView(InputObject inputObject, OutputObject outputObject) {
        String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
        QueryWrapper<VideoView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoView::getUserId), userId);
        remove(queryWrapper);
    }

    @Override
    public void queryAllVideoView(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String objectId = commonPageInfo.getObjectId();
        if(StrUtil.isEmpty(objectId)){
            throw new CustomException("用户id（objectId）不能为空");
        }
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<VideoView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoView::getUserId), objectId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoView::getCreateTime));
        List<VideoView> videoViews = list(queryWrapper);
        outputObject.setBean(videoViews);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public List<VideoView> queryVideoViewByUserId(String userId){
        QueryWrapper<VideoView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoView::getUserId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoView::getCreateTime));
        return list(queryWrapper);
    }

    @Override
    public void deleteByVideoId(String id) {
        QueryWrapper<VideoView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(VideoView::getVideoId), id);
        remove(queryWrapper);
    }
}

package com.skyeye.videotag.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.video.entity.Video;
import com.skyeye.videotag.dao.VideoTagDao;
import com.skyeye.videotag.entity.VideoTag;
import com.skyeye.videotag.service.VideoTagService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * @ClassName: VideoTagServiceImpl
 * @Description: 视频标签业务实现类
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "视频标签管理", groupName = "视频标签管理")
public class VideoTagServiceImpl extends SkyeyeBusinessServiceImpl<VideoTagDao, VideoTag> implements VideoTagService {

    @Override
    public QueryWrapper<VideoTag> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<VideoTag> wrapper = super.getQueryWrapper(commonPageInfo);
        wrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoTag::getOrderBy));
        return wrapper;
    }

    @Override
    public VideoTag selectById(String id) {
        VideoTag videoTag = super.selectById(id);
        iAuthUserService.setName(videoTag, "createId", "createName");
        iAuthUserService.setName(videoTag, "lastUpdateId", "lastUpdateName");
        return videoTag;
    }

    /**
     * 为视频中设置标签信息
     */
    @Override
    public void setTagMationForVideoList(Video... beans) {
        List<String> tagIdList = new ArrayList<>();
        for (Video video : beans) {
            String tagId = video.getTagId();
            if (StrUtil.isEmpty(tagId)) {
                continue;
            }
            String[] tagIdArr = tagId.split(",");
            tagIdList.addAll(Arrays.asList(tagIdArr));
            List<String> distinctTagIds = tagIdList.stream().distinct().collect(Collectors.toList());
            if (CollectionUtil.isEmpty(distinctTagIds)) {
                continue;
            }
            QueryWrapper<VideoTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(CommonConstants.ID, distinctTagIds)
                .orderByDesc(MybatisPlusUtil.toColumns(VideoTag::getOrderBy));
            List<VideoTag> videoTags = list(queryWrapper);
            video.setTagMation(videoTags);
            tagIdList = new ArrayList<>();
        }
    }

    @Override
    public void queryAllVideoTagList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<VideoTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(VideoTag::getCreateTime))
            .orderByDesc(MybatisPlusUtil.toColumns(VideoTag::getOrderBy));
        List<VideoTag> videoTags = list(queryWrapper);
        iAuthUserService.setName(videoTags, "createId", "createName");
        iAuthUserService.setName(videoTags, "lastUpdateId", "lastUpdateName");
        outputObject.setBeans(videoTags);
        outputObject.settotal(videoTags.size());
    }
}

package com.skyeye.videotag.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ReflectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.cache.helper.ModelFieldCacheHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.DeleteFlagEnum;
import com.skyeye.common.enumeration.EnableEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.video.entity.Video;
import com.skyeye.videotag.dao.VideoTagDao;
import com.skyeye.videotag.entity.VideoTag;
import com.skyeye.videotag.service.VideoTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
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

    @Autowired
    private IAuthUserService iAuthUserService;


    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        return beans;
    }

    @Override
    public QueryWrapper<VideoTag> getQueryWrapper(CommonPageInfo commonPageInfo) {
        // 69-87均为父类的代码，父类代码设置了默认排序（根据时间见徐排序），所以重新设置排序（88）
        QueryWrapper<VideoTag> wrapper = new QueryWrapper<>();
        // 获取模糊匹配的字段，使用or
        List<String> fuzzyLikeField = ModelFieldCacheHelper.get(clazz);
        if (CollectionUtil.isNotEmpty(fuzzyLikeField) && StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            wrapper.and(wra -> {
                for (String field : fuzzyLikeField) {
                    wra.or().like(field, commonPageInfo.getKeyword());
                }
            });
        }
        // 高级搜索
        if (StrUtil.isNotEmpty(commonPageInfo.getSqlExtract())) {
            wrapper.apply(commonPageInfo.getSqlExtract());
        }
        if (ReflectUtil.hasField(clazz, CommonConstants.DELETE_FLAG)) {
            // 判断是否有删除标识并放入，只查询没有删除的数据
            wrapper.ne(MybatisPlusUtil.toColumns(clazz, CommonConstants.DELETE_FLAG), DeleteFlagEnum.DELETED.getKey());
        }
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
    public void setTagMationForVideoList(Video ...beans) {
        List<String> tagIdList = new ArrayList<>();
        for (Video video : beans) {
            String tagId = video.getTagId();
            if (StrUtil.isEmpty(tagId)) {
                continue;
            }
            String[] tagIdArr = tagId.split(",");
            tagIdList.addAll(Arrays.asList(tagIdArr));
            List<String> distinctTagIds = tagIdList.stream().distinct().collect(Collectors.toList());
            if(CollectionUtil.isEmpty(distinctTagIds)) {
                continue;
            }
            QueryWrapper<VideoTag> queryWrapper = new QueryWrapper<>();
            queryWrapper.in(CommonConstants.ID, distinctTagIds)
                    .orderByDesc(MybatisPlusUtil.toColumns(VideoTag::getOrderBy));
            List<VideoTag> videoTags = list(queryWrapper);
            video.setTagMation(videoTags);
        }
    }
}

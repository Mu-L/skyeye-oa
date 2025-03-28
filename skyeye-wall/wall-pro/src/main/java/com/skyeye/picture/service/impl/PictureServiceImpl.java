package com.skyeye.picture.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.picture.dao.PictureDao;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: PictureServiceImpl
 * @Description: 图片服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "图片管理", groupName = "图片管理")
public class PictureServiceImpl extends SkyeyeBusinessServiceImpl<PictureDao, Picture> implements PictureService {

    @Override
    public Map<String, List<Picture>> getPictureMapListByIds(List<String> objectIds) {
        if (CollectionUtil.isEmpty(objectIds)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Picture::getObjectId), objectIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Picture::getOrderBy));
        List<Picture> pictureList = list(queryWrapper);
        Map<String, List<Picture>> pictureMap = pictureList.stream()
            .collect(Collectors.groupingBy(Picture::getObjectId));
        return pictureMap;
    }

    @Override
    public void deleteByPostId(String id) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Picture::getObjectId), id);
        remove(queryWrapper);
    }

    @Override
    public void deleteByPostIds(List<String> postIds) {
        if (CollectionUtil.isEmpty(postIds)) {
            return;
        }
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Picture::getObjectId), postIds);
        remove(queryWrapper);
    }

    @Override
    public void deleteByCommentIds(List<String> ids) {
        if (CollectionUtil.isEmpty(ids)) {
            return;
        }
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Picture::getObjectId), ids);
        remove(queryWrapper);
    }

    @Override
    public Picture getPictureByObjectId(String objectId) {
        QueryWrapper<Picture> queryPicture = new QueryWrapper<>();
        queryPicture.eq(MybatisPlusUtil.toColumns(Picture::getObjectId), objectId);
        return getOne(queryPicture);
    }

    @Override
    public List<Picture> queryLinkListByPostId(String PostId) {
        QueryWrapper<Picture> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Picture::getObjectId), PostId);
        return list(queryWrapper);
    }
}
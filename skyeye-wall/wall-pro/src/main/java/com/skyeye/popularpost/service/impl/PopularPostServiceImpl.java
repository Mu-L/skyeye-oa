/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.popularpost.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CacheConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.RedisConstants;
import com.skyeye.common.constans.TipsConstants;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.popularpost.dao.PopularPostDao;
import com.skyeye.popularpost.entity.PopularPost;
import com.skyeye.popularpost.service.PopularPostService;
import com.skyeye.post.entity.Post;
import com.skyeye.post.service.PostService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName: PopularPostServiceImpl
 * @Description: 热门帖子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "热门帖子管理", groupName = "热门帖子管理")
public class PopularPostServiceImpl extends SkyeyeBusinessServiceImpl<PopularPostDao, PopularPost> implements PopularPostService {

    @Autowired
    private PostService postService;

    @Autowired
    private PopularPostService popularPostService;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    private static Logger LOGGER = LoggerFactory.getLogger(PopularPostServiceImpl.class);

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void insertPopularPostList(String tenantId) {
        //取出前30天内的post
        List<Post> postList;
        // 获取当前时间
        String pointTime = DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH);

        if (tenantEnable) {
            String key = CacheConstants.XXL_JOP_POST + tenantId + StrUtil.COLON + pointTime;
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, key, RedisConstants.XXL_JOP_POST_EXPIRE, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(flag)) {
                LOGGER.info(TipsConstants.TASK_IS_SKIPPING);
                return;
            }
            postList = postService.getBeforeThirtyDaysPost(tenantId);
        } else {
            String key = CacheConstants.XXL_JOP_POST + pointTime;
            // 判断redis中是否存在该key-- 不存在ture 存在false
            Boolean flag = stringRedisTemplate.opsForValue().setIfAbsent(key, key, RedisConstants.XXL_JOP_POST_EXPIRE, TimeUnit.SECONDS);
            if (Boolean.FALSE.equals(flag)) {
                LOGGER.info(TipsConstants.TASK_IS_SKIPPING);
                return;
            }
            postList = postService.getBeforeThirtyDaysPost(null);
        }
        //取出点赞量、评论量、浏览量
        Map<String, List<Integer>> listMap = getAll(postList);
        //计算权重
        Map<String, Double> powerMap = calculatePower(listMap);
        //排序权重
        List<String> powerResult = sortByMapValue(powerMap);
        //取出前十的帖子
        List<PopularPost> popularPostList = getBeforeTenPopularPostList(powerResult, postList);
        popularPostService.createEntity(popularPostList, null);
    }


    public Map<String, List<Integer>> getAll(List<Post> postList) {
        Map<String, List<Integer>> listMap = new HashMap<>();
        for (Post post : postList) {
            List<Integer> flag = new ArrayList<>();
            flag.add(Integer.parseInt(post.getUpvoteNum()));
            flag.add(Integer.parseInt(post.getCommentNum()));
            if (StrUtil.isEmpty(post.getViewNum())) {
                flag.add(CommonNumConstants.NUM_ZERO);
            } else {
                flag.add(Integer.parseInt(post.getViewNum()));
            }
            listMap.put(post.getId(), flag);
        }
        return listMap;
    }

    public Map<String, Double> calculatePower(Map<String, List<Integer>> listMap) {
        Map<String, Double> powerMap = new HashMap<>();
        listMap.forEach((key, list) -> {
            double power = list.get(CommonNumConstants.NUM_ZERO) * 0.5 +
                    list.get(CommonNumConstants.NUM_ONE) * 0.2 + list.get(CommonNumConstants.NUM_TWO) * 0.3;
            powerMap.put(key, power);
        });
        return powerMap;
    }

    public List<String> sortByMapValue(Map<String, Double> doubleMap) {
        List<Map.Entry<String, Double>> list = new LinkedList<>(doubleMap.entrySet());
        CollectionUtil.sort(list, (o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        List<String> result = new ArrayList<>(list.size());
        for (Map.Entry<String, Double> entry : list) {
            result.add(entry.getKey());
        }
        return result;
    }

    public List<PopularPost> getBeforeTenPopularPostList(List<String> powerResult, List<Post> postList) {
        List<PopularPost> popularPostList = new ArrayList<>();
        for (int i = 0; i < postList.size(); i++) {
            for (String s : powerResult) {
                if (postList.get(i).getId().equals(s)) {
                    PopularPost popularPost = new PopularPost();
                    popularPost.setPostId(s);
                    popularPost.setTop(i + CommonNumConstants.NUM_ONE);
                    popularPost.setUpvoteNum(Integer.parseInt(postList.get(i).getUpvoteNum()));
                    popularPost.setCommentNum(Integer.parseInt(postList.get(i).getCommentNum()));
                    if (StrUtil.isNotEmpty(postList.get(i).getViewNum())) {
                        popularPost.setViewNum(Integer.parseInt(postList.get(i).getViewNum()));
                    } else {
                        popularPost.setViewNum(CommonNumConstants.NUM_ZERO);
                    }
                    popularPost.setCreateTime(DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH));
                    popularPostList.add(popularPost);
                }
            }
        }
        return CollectionUtil.sub(popularPostList, 0, 10);
    }

    @Override
    public List<PopularPost> queryTodayHourPopularPostList() {
        QueryWrapper<PopularPost> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(PopularPost::getCreateTime), DateUtil.getPointTime(DateUtil.YYYY_MM_DD_HH))
                .orderByAsc(MybatisPlusUtil.toColumns(PopularPost::getTop));
        return list(queryWrapper);
    }
}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.comment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.comment.dao.CommentDao;
import com.skyeye.comment.entity.Comment;
import com.skyeye.comment.service.CommentService;
import com.skyeye.common.WallConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import com.skyeye.post.entity.Post;
import com.skyeye.post.service.PostService;
import com.skyeye.upvote.service.UpvoteService;
import com.skyeye.user.service.UserService;
import com.skyeye.user.userenum.LoginIdentity;
import com.xxl.job.core.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: CommentServiceImpl
 * @Description: 评论服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "评论管理", groupName = "评论管理")
public class CommentServiceImpl extends SkyeyeBusinessServiceImpl<CommentDao, Comment> implements CommentService {

    @Autowired
    private UserService userService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private PostService postService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        List<Map<String, Object>> beans = skyeyeBaseMapper.queryCommentList(commonPageInfo);
        List<String> ids = beans.stream()
            .map(bean -> bean.get("id").toString()).collect(Collectors.toList());

        // 查询子评论
        List<Comment> child = getCommentMapList(ids);
        List<Map<String, Object>> mapList = JSONUtil.toList(JSONUtil.toJsonStr(child), null);
        beans.addAll(mapList);

        // 获取评论的匿名信息
        Map<String, Integer> anonymityMap = beans.stream()
            .collect(Collectors.toMap(bean -> bean.get("id").toString(), bean -> Integer.parseInt(bean.get("anonymity").toString())));

        // 获取评论图片
        ids = beans.stream()
            .map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        Map<String, List<Picture>> picturetMap = pictureService.getPictureMapListByIds(ids);
        // 获取点赞信息
        String userId = inputObject.getLogParams().get("id").toString();
        Map<String, Boolean> checkUpvoteMap = upvoteService.checkUpvote(userId, ids.toArray(new String[]{}));

        beans.forEach(bean -> {
            String id = bean.get("id").toString();
            // 被回复评论id
            String commentId = bean.get("commentId").toString();
            // 是否匿名
            Integer anonymity = Integer.parseInt(bean.get("anonymity").toString());
            if (anonymity == WhetherEnum.ENABLE_USING.getKey()) {
                // 匿名
                bean.put("createId", StrUtil.EMPTY);
                bean.put("lastUpdateId", StrUtil.EMPTY);
            }
            if (StrUtil.isNotEmpty(commentId)) {
                if (anonymityMap.get(commentId) == WhetherEnum.ENABLE_USING.getKey()) {
                    // 匿名
                    bean.put("userId", StrUtil.EMPTY);
                }
            }
            // 设置图片信息
            List<Picture> pictures = picturetMap.get(id);
            if (CollectionUtil.isNotEmpty(pictures)) {
                bean.put("picture", pictures.stream().findFirst().orElse(null));
            }
            // 设置点赞信息
            bean.put("checkUpvote", checkUpvoteMap.get(id));
            // 设置用户信息
            // TODO 空指针异常
            String loginIdentity = bean.get("loginIdentity").toString();
            if(LoginIdentity.STUDENT.getKey().equals(loginIdentity)) {
                userService.setMationForMap(bean, "createId", "createMation");
                userService.setMationForMap(bean, "userId", "userMation");
            }else {
                iAuthUserService.setMationForMap(bean, "createId", "createMation");
                iAuthUserService.setMationForMap(bean, "userId", "userMation");
            }
        });
        return beans;
    }


    public List<Comment> getCommentMapList(List<String> parentIds) {
        if (CollectionUtil.isEmpty(parentIds)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Comment::getParentId), parentIds);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Comment::getCreateTime));
        List<Comment> commentList = list(queryWrapper);
        return commentList;
    }

    @Override
    public Map<String, List<Comment>> getCommentMapListByIds(List<String> postIds) {
        if (CollectionUtil.isEmpty(postIds)) {
            return MapUtil.newHashMap();
        }
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Comment::getPostId), postIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Comment::getParentId), StrUtil.EMPTY);
        queryWrapper.orderByAsc(MybatisPlusUtil.toColumns(Comment::getCreateTime));
        List<Comment> commentList = list(queryWrapper);

        List<String> ids = commentList.stream()
            .map(Comment::getId).collect(Collectors.toList());
        Map<String, List<Picture>> picturetMap = pictureService.getPictureMapListByIds(ids);
        commentList.forEach(comment -> {
            String id = comment.getId();
            if (comment.getAnonymity() == WhetherEnum.ENABLE_USING.getKey()) {
                // 匿名
                comment.setCreateId(StrUtil.EMPTY);
                comment.setLastUpdateId(StrUtil.EMPTY);
            }
            List<Picture> pictures = picturetMap.get(id);
            if (CollectionUtil.isNotEmpty(pictures)) {
                comment.setPicture(JSONUtil.toBean(JSON.toJSONString(pictures.stream().findFirst().orElse(null)), null));
            }
        });
        List<Comment> bean = commentList.stream().map(comment -> {
            if (LoginIdentity.STUDENT.getKey().equals(comment.getLoginIdentity())) {
                userService.setDataMation(commentList, Comment::getCreateId);
            } else {
                iAuthUserService.setDataMation(commentList, Comment::getCreateId);
            }
            return comment;
        }).collect(Collectors.toList());
        Map<String, List<Comment>> commentMap = bean.stream()
            .collect(Collectors.groupingBy(Comment::getPostId));
        return commentMap;
    }

    @Override
    public void deleteByPostId(String id) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Comment::getPostId), id);
        List<Comment> list = list(queryWrapper);
        List<String> ids = list.stream().map(Comment::getId).collect(Collectors.toList());
        pictureService.deleteByCommentIds(ids);
        remove(queryWrapper);
    }

    @Override
    public void deleteByPostIds(List<String> postIds) {
        if (CollectionUtil.isEmpty(postIds)) {
            return;
        }
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Comment::getPostId), postIds);
        List<Comment> list = list(queryWrapper);
        List<String> ids = list.stream().map(Comment::getId).collect(Collectors.toList());
        pictureService.deleteByCommentIds(ids);
        remove(queryWrapper);
    }

    @Override
    public void deletePostpose(Comment entity) {
        // 删除子评论
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Comment::getParentId), entity.getId());
        List<Comment> commentList = list(queryWrapper);
        List<String> ids = commentList.stream()
            .map(Comment::getId).collect(Collectors.toList());
        ids.add(entity.getId());
        pictureService.deleteByCommentIds(ids);
        remove(queryWrapper);
        // 查询所有评论条数，更新帖子评论总数
        QueryWrapper<Comment> countQueryWrapper = new QueryWrapper<>();
        countQueryWrapper.eq(MybatisPlusUtil.toColumns(Comment::getPostId), entity.getPostId());
        long count = count(countQueryWrapper);
        postService.updateCommentCount(entity.getPostId(), String.valueOf(count));
    }

    @Override
    protected void deletePreExecution(Comment comment) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String postId = comment.getPostId();
        Post post = postService.selectById(postId);
        if (post.getCreateId().equals(userId)) {
            return;
        }
        if (!userId.equals(comment.getCreateId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    public void createPrepose(Comment entity) {
        String userIdentity = PutObject.getRequest().getHeader(WallConstants.USER_IDENTITY_KEY);
        entity.setLoginIdentity(userIdentity);
        entity.setUpvoteNum(String.valueOf(CommonNumConstants.NUM_ZERO));
        if (StrUtil.isNotEmpty(entity.getCommentId())) {
            Comment comment = commentService.selectById(entity.getCommentId());
            entity.setUserId(comment.getCreateId());
        }
        entity.setIp(IpUtil.getIp());
    }

    @Override
    public void createPostpose(Comment comment, String userId) {
        addCommentNum(comment.getPostId());
        if (CollectionUtil.isNotEmpty(comment.getPicture())) {
            Picture picture = new Picture();
            picture.setImg(comment.getPicture().get("img").toString());
            picture.setOrderBy(comment.getPicture().get("orderBy").toString());
            picture.setObjectId(comment.getId());
            pictureService.createEntity(picture, userId);
        }
    }

    public void addCommentNum(String postId) {
        Post post = postService.selectById(postId);
        if (post.getCommentNum() != null) {
            postService.updateCommentCount(post.getId(), CalculationUtil.add(CommonNumConstants.NUM_ZERO,
                post.getCommentNum(), String.valueOf(CommonNumConstants.NUM_ONE)));
        }
    }

    @Override
    public void updateCommentUpvoteNum(String id, String count) {
        UpdateWrapper<Comment> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Comment::getUpvoteNum), count);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void validatorEntity(Comment comment) {
        super.validatorEntity(comment);
        Post post = postService.selectById(comment.getPostId());
        if (ObjectUtil.isEmpty(post) && StrUtil.isEmpty(post.getId())) {
            throw new CustomException("无此帖子，不可评论！");
        }
        Comment parentComment = commentService.selectById(comment.getCommentId());
        if (ObjectUtil.isEmpty(parentComment) && StrUtil.isEmpty(parentComment.getId())) {
            throw new CustomException("无此评论，不可评论！");
        }
    }

    @Override
    public List<Comment> queryCommentList(String userId) {
        QueryWrapper<Comment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Comment::getCreateId), userId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Comment::getCreateTime));
        return list(queryWrapper);
    }
}
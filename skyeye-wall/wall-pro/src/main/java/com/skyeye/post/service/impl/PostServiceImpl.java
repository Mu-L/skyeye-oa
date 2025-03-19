/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.post.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.comment.entity.Comment;
import com.skyeye.comment.service.CommentService;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.IPSeeker;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.historypost.entity.HistoryPost;
import com.skyeye.historypost.service.HistoryPostService;
import com.skyeye.joincircle.service.JoinCircleService;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import com.skyeye.popularpost.entity.PopularPost;
import com.skyeye.popularpost.service.PopularPostService;
import com.skyeye.post.dao.PostDao;
import com.skyeye.post.entity.Post;
import com.skyeye.post.service.PostService;
import com.skyeye.upvote.entity.Upvote;
import com.skyeye.upvote.service.UpvoteService;
import com.skyeye.user.entity.User;
import com.skyeye.user.service.UserService;
import com.xxl.job.core.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: PostServiceImpl
 * @Description: 帖子服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "帖子管理", groupName = "帖子管理")
public class PostServiceImpl extends SkyeyeBusinessServiceImpl<PostDao, Post> implements PostService {

    @Autowired
    private CommentService commentService;

    @Autowired
    private PictureService pictureService;

    @Autowired
    private UserService userService;

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private HistoryPostService historyPostService;

    @Autowired
    private PopularPostService popularPostService;

    @Autowired
    private JoinCircleService joinCircleService;


    private List<Map<String, Object>> queryPostList(InputObject inputObject) {
        Map<String, Object> params = inputObject.getParams();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String keyword = commonPageInfo.getKeyword();
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotEmpty(keyword)){
            queryWrapper.like(MybatisPlusUtil.toColumns(Post::getTitle), keyword);
        }
        if (params.containsKey("holderId") && StrUtil.isNotEmpty(params.get("holderId").toString())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), params.get("holderId").toString());
            queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Post::getCreateTime));
            List<Post> postList = list(queryWrapper);
            List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(postList), null);
            //  传holderId时，判断是否已加入该圈子
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            String circleId = params.get("holderId").toString();
            String createId = joinCircleService.selectByCircleId(circleId, userId).getCreateId();
            return StrUtil.isEmpty(createId) ? CollectionUtil.sub(beans, CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_TEN) : beans;
        } else if (params.containsKey("type") && StrUtil.isNotEmpty(params.get("type").toString())) {
            String typeId = params.get("type").toString();
            String userId = InputObject.getLogParamsStatic().get(CommonConstants.ID).toString();
            if(StrUtil.isEmpty(userId) || !typeId.equals(userId)){
                queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getAnonymity), WhetherEnum.DISABLE_USING.getKey());
            }
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCreateId), typeId).or()
                .eq(MybatisPlusUtil.toColumns(Post::getTypeId), typeId)
                .orderByDesc(MybatisPlusUtil.toColumns(Post::getCreateTime));
            List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list(queryWrapper)), null);
            return beans;
        } else {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), null).or()
                .eq(MybatisPlusUtil.toColumns(Post::getCircleId), "")
                .orderByDesc(MybatisPlusUtil.toColumns(Post::getCreateTime));
            List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(list(queryWrapper)), null);
            return beans;
        }
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = queryPostList(inputObject);
        List<String> postIds = beans.stream()
            .map(bean -> bean.get("id").toString()).collect(Collectors.toList());
        // 获取评论信息
        Map<String, List<Comment>> commentMap = commentService.getCommentMapListByIds(postIds);
        // 获取帖子图片信息
        Map<String, List<Picture>> pictureMap = pictureService.getPictureMapListByIds(postIds);
        // 获取点赞信息
        Map<String, Boolean> checkUpvoteMap = new HashMap<>();
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isNotEmpty(userToken)){
            String userId = inputObject.getLogParams().get("id").toString();
            if(StrUtil.isNotEmpty(userId)){
                checkUpvoteMap = upvoteService.checkUpvote(userId, postIds.toArray(new String[]{}));
            }
        }
        Map<String, Boolean> finalCheckUpvoteMap = checkUpvoteMap;
        beans.forEach(bean -> {
            String id = bean.get("id").toString();
            // 是否匿名
            Integer anonymity = Integer.parseInt(bean.get("anonymity").toString());
            if (anonymity == WhetherEnum.ENABLE_USING.getKey()) {
                // 匿名
                bean.put("createId", StrUtil.EMPTY);
                bean.put("lastUpdateId", StrUtil.EMPTY);
            }
            // 设置评论信息
            bean.put("commentList", CollectionUtil.sub(commentMap.get(id), CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_FIVE));
            // 设置图片信息
            bean.put("pictureList", CollectionUtil.sub(pictureMap.get(id), CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_NINE));
            bean.put("pictureSize", pictureMap.size());
            // 设置点赞信息
            if(CollectionUtil.isNotEmpty(finalCheckUpvoteMap)){
                bean.put("checkUpvote", finalCheckUpvoteMap.get(id));
            }
        });
        userService.setMationForMap(beans, "createId", "createMation");
        return beans;
    }

    @Override
    public void createPrepose(Post entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        entity.setCreateId(userId);
        entity.setIp(IpUtil.getLocalAddress().toString());
        HttpServletRequest request = PutObject.getRequest();
        String ipAddress = ToolUtil.getIpByRequest(request);
        String address = IPSeeker.getCountry(ipAddress);
        entity.setAddress(address);
        String city = IPSeeker.getCurCityByCountry(address);
        entity.setCity(city);
        entity.setUpvoteNum("0");
        entity.setCommentNum("0");
        entity.setViewNum("0");
        if (entity.getPictureList().size() > 20) {
            throw new CustomException("超过可上传的图片数量");
        }
    }

    @Override
    public void createPostpose(Post entity, String userId) {
        entity.getPictureList().forEach(picture -> {
            picture.setObjectId(entity.getId());
        });
        pictureService.createEntity(entity.getPictureList(), userId);
    }

    @Override
    public Post getDataFromDb(String id) {
        Post post = super.getDataFromDb(id);
        post.setPictureList(pictureService.queryLinkListByPostId(id));
        return post;
    }

    @Override
    public Post selectById(String id) {
        Post post = super.selectById(id);
        if (post.getAnonymity() == WhetherEnum.ENABLE_USING.getKey()) {
            // 匿名
            post.setCreateId(StrUtil.EMPTY);
            post.setLastUpdateId(StrUtil.EMPTY);
        } else {
            userService.setDataMation(post, Post::getCreateId);
        }
        return post;
    }

    @Override
    public void deletePreExecution(Post post) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        if (!userId.equals(post.getCreateId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    public void deletePostpose(String id) {
        pictureService.deleteByPostId(id);
        commentService.deleteByPostId(id);
    }

    @Override
    public void updateCommentCount(String id, String count) {
        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Post::getCommentNum), count);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void updateUpvoteCount(String id, String count) {
        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Post::getUpvoteNum), count);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void updateViewCount(String id, String count) {
        UpdateWrapper<Post> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(Post::getViewNum), count);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public void queryPostListByUpvote(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = commonPageInfo.getObjectId();
        List<Upvote> upvoteList = upvoteService.queryUpvoteList(userId);
        if (CollectionUtil.isEmpty(upvoteList)) {
            return;
        }
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < upvoteList.size(); i++) {
            map.put(i, upvoteList.get(i).getObjectId());
        }
        //分页
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Post> list = commentAndUpvoteOperationPost(userId, map);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryPostListByComment(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = commonPageInfo.getObjectId();
        //查询自己的评论
        List<Comment> myCommentList = commentService.queryCommentList(userId);
        if (CollectionUtil.isEmpty(myCommentList)) {
            return;
        }
        Map<Integer, String> map = new HashMap<>();
        for (int i = 0; i < myCommentList.size(); i++) {
            map.put(i, myCommentList.get(i).getPostId());
        }
        //分页
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Post> list = commentAndUpvoteOperationPost(userId, map);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    public List<Post> commentAndUpvoteOperationPost(String userId, Map<Integer, String> map) {
        List<String> postIds = new ArrayList<>();
        map.forEach((key, value) -> postIds.add(value));
        //查询post
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, postIds);
        List<Post> list = list(queryWrapper);
        List<Post> sortPostList = new ArrayList<>();
        map.forEach((key, value) -> {
            for (Post post : list) {
                if (value.equals(post.getId())) {
                    sortPostList.add(post);
                }
            }
        });
        //查询PictureList、checkUpvote、commentList
        Map<String, List<Picture>> pictureMap = pictureService.getPictureMapListByIds(postIds);
        Map<String, Boolean> booleanMap = upvoteService.checkUpvote(userId, postIds.toArray(new String[]{}));
        Map<String, List<Comment>> commentListMap = commentService.getCommentMapListByIds(postIds);
        //设置PictureList、checkUpvote、commentList
        sortPostList.forEach(post -> {
            if (post.getAnonymity() == WhetherEnum.ENABLE_USING.getKey()) {
                // 匿名
                post.setCreateId(StrUtil.EMPTY);
                post.setLastUpdateId(StrUtil.EMPTY);
            }
            post.setPictureList(pictureMap.get(post.getId()));
            post.setCheckUpvote(booleanMap.get(post.getId()));
            post.setCommentList(CollectionUtil.sub(commentListMap.get(post.getId()),
                    CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_FIVE));
        });
        userService.setDataMation(list, Post::getCreateId);
        return sortPostList;
    }

    @Override
    public void queryPostListByHistoryPost(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        // 查询自己的历史记录
        List<HistoryPost> historyPostList = historyPostService.getHistoryPostById(userId);
        if (CollectionUtil.isEmpty(historyPostList)) {
            return;
        }
        List<String> postIds = historyPostList.stream()
                .map(HistoryPost::getPostId).collect(Collectors.toList());
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, postIds);
        List<Post> postList = list(queryWrapper);
        //设置点赞信息
        Map<String, Boolean> checkUpvoteMap = upvoteService.checkUpvote(userId, postIds.toArray(new String[]{}));
        checkUpvoteMap.forEach((key, value) -> {
            postList.forEach(post -> {
                if (post.getId().equals(key)) {
                    post.setCheckUpvote(value);
                }
            });
        });
        outputObject.setBeans(postList);
        outputObject.settotal(pages.size());
    }

    @Override
    public List<Post> getBeforeThirtyDaysPost() {
        //获取前三十天以内的日期
        String beforeDay = getBeforeOrFutureDay(-29);
        String today = DateUtil.getTimeAndToString();
        //查询近三十天的帖子
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper
                // 取浏览量超过1的帖子
                .ge(MybatisPlusUtil.toColumns(Post::getViewNum), "1")
                // 取前三十天以内的帖子
                .between(MybatisPlusUtil.toColumns(Post::getCreateTime), beforeDay, today)
                .orderByDesc(MybatisPlusUtil.toColumns(Post::getCreateTime));
        return list(queryWrapper);
    }

    public String getBeforeOrFutureDay(int num) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.setTime(new Date());
        c.add(Calendar.DATE, num);
        Date m = c.getTime();
        return format.format(m);
    }

    @Override
    public void deleteByCircleId(String circleId) {
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), circleId);
        List<Post> postList = list(queryWrapper);
        List<String> postIds = postList.stream()
                .map(Post::getId).collect(Collectors.toList());
        pictureService.deleteByPostIds(postIds);
        commentService.deleteByPostIds(postIds);
        remove(queryWrapper);
    }

    @Override
    public void queryHotPostList(InputObject inputObject, OutputObject outputObject) {
        List<PopularPost> popularPostList = popularPostService.queryTodayPopularPostList();
        List<String> postIds = popularPostList.stream()
                .map(PopularPost::getPostId).collect(Collectors.toList());
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (CollectionUtil.isEmpty(postIds)) {
            throw new CustomException("暂无热门帖子");
        }
        queryWrapper.in(CommonConstants.ID, postIds);
        List<Post> postList = list(queryWrapper);
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        Map<String, Boolean> checkUpvoteMap = new HashMap<>();
        if(StrUtil.isNotEmpty(userToken)){
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            checkUpvoteMap = upvoteService.checkUpvote(userId, postIds.toArray(new String[]{}));
        }
        //获取点赞信息
        if(CollectionUtil.isNotEmpty(checkUpvoteMap)){
            checkUpvoteMap.forEach((key, value) -> {
                postList.forEach(post -> {
                    if (key.equals(post.getId())) {
                        post.setCheckUpvote(value);
                    }
                });
            });
        }
        outputObject.setBeans(postList);
        outputObject.settotal(popularPostList.size());
    }

    @Override
    public void queryUserPostCount(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userId = params.get("userId").toString();
        List<Map<String,Integer>> beans = new ArrayList<>();
        Map<String,Integer> countMap = new HashMap<>();
        QueryWrapper<Post> postRapper = new QueryWrapper<>();
        postRapper.eq(MybatisPlusUtil.toColumns(Post::getCreateId),userId);
        List<Post> postList = list(postRapper);
        if(CollectionUtil.isEmpty(postList)){
            return;
        }
        // 计算总评论数量
        int commentNum = postList.stream().mapToInt(item -> Integer.parseInt(item.getCommentNum())).sum();
        // 计算总点赞数量
        int upvoteNum = postList.stream().mapToInt(item -> Integer.parseInt(item.getUpvoteNum())).sum();
        countMap.put("commentNum",commentNum);
        countMap.put("upvoteNum",upvoteNum);
        countMap.put("postNum",postList.size());
        beans.add(countMap);
        outputObject.setBeans(beans);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryPostVisitor(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String postId =  params.get("postId").toString();
        List<String> visitors = historyPostService.queryRecordUserIdByPostId(postId);
        if(CollectionUtil.isEmpty(visitors)){
            return;
        }
        // 转为数组
        String[] userIs = visitors.toArray(new String[0]);
        List<User> beans = userService.selectByIds(userIs);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
}
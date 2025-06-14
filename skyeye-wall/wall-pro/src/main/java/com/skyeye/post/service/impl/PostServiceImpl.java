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
import com.skyeye.circle.service.CircleService;
import com.skyeye.comment.entity.Comment;
import com.skyeye.comment.service.CommentService;
import com.skyeye.common.WallConstants;
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
import com.skyeye.historypost.service.HistoryPostService;
import com.skyeye.joincircle.entity.JoinCircle;
import com.skyeye.joincircle.service.JoinCircleService;
import com.skyeye.notice.service.NoticeService;
import com.skyeye.picture.entity.Picture;
import com.skyeye.picture.service.PictureService;
import com.skyeye.popularpost.entity.PopularPost;
import com.skyeye.popularpost.service.PopularPostService;
import com.skyeye.post.dao.PostDao;
import com.skyeye.post.entity.Post;
import com.skyeye.post.service.PostService;
import com.skyeye.upvote.entity.Upvote;
import com.skyeye.upvote.service.UpvoteService;
import com.skyeye.user.service.UserService;
import com.skyeye.user.userenum.LoginIdentity;
import com.xxl.job.core.util.IpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    @Autowired
    private CircleService circleService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private PostService postService;

    @Value("${skyeye.tenant.enable}")
    private boolean tenantEnable;

    @Override
    public void validatorEntity(Post entity) {
        super.validatorEntity(entity);
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if (StrUtil.isEmpty(userToken)) {
            throw new CustomException("请先完成登录！");
        }
    }

    private Post setUserMation(Post post) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        Map<String, Boolean> checkUpvote = new HashMap<>();
        if (StrUtil.isNotEmpty(userToken)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            if (StrUtil.isNotEmpty(userId)) {
                checkUpvote = upvoteService.checkUpvote(userId, post.getId());
            }
        }
        if (CollectionUtil.isNotEmpty(checkUpvote)) {
            post.setCheckUpvote(checkUpvote.get(post.getId()));
        }
        if (post.getAnonymity() == WhetherEnum.DISABLE_USING.getKey()) {
            if (LoginIdentity.STUDENT.getKey().equals(post.getLoginIdentity())) {
                userService.setDataMation(post, Post::getCreateId);
                String username = post.getCreateMation().getOrDefault("name", "").toString();
                post.getCreateMation().put("username", username);
            } else {
                iAuthUserService.setDataMation(post, Post::getCreateId);
            }
        }
        return post;
    }

    @Override
    public void setUserMations(List<Post> posts) {
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        List<String> postIds = posts.stream().map(Post::getId).collect(Collectors.toList());
        Map<String, Boolean> checkUpvote = new HashMap<>();
        if (StrUtil.isNotEmpty(userToken)) {
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            if (StrUtil.isNotEmpty(userId)) {
                checkUpvote = upvoteService.checkUpvote(userId, postIds.toArray(new String[0]));
            }
        }
        for (Post post : posts) {
            post.setCheckUpvote(checkUpvote.get(post.getId()));
            if (post.getAnonymity() == WhetherEnum.ENABLE_USING.getKey()) {
                continue;
            }
            if (LoginIdentity.STUDENT.getKey().equals(post.getLoginIdentity())) {
                userService.setDataMation(post, Post::getCreateId);
                String username = post.getCreateMation().getOrDefault("name", StrUtil.EMPTY).toString();
                post.getCreateMation().put("username", username);
            } else {
                iAuthUserService.setDataMation(post, Post::getCreateId);
            }
        }
    }

    private List<Map<String, Object>> queryPostList(InputObject inputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String keyword = commonPageInfo.getKeyword();
        String objectId = commonPageInfo.getObjectId();
        String holderId = commonPageInfo.getHolderId();
        String type = commonPageInfo.getType();
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        String userId;
        if (StrUtil.isEmpty(userToken)) {
            userId = null;
        } else {
            userId = InputObject.getLogParamsStatic().get("id").toString();
        }
        List<Post> bean;
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Post::getCreateTime));
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.like(MybatisPlusUtil.toColumns(Post::getTitle), keyword);
        }
        if (StrUtil.isNotEmpty(holderId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), holderId);
            // 设置用户信息--和点赞信息
            bean = list(queryWrapper);
            setUserMations(bean);
            List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(bean), null);
            //  传holderId时，判断是否已加入该圈子
            String createId = joinCircleService.selectByCircleId(holderId, userId).getCreateId();
            return StrUtil.isEmpty(createId) ? CollectionUtil.sub(beans, CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_FIVE) : beans;
        } else if (StrUtil.isNotEmpty(type)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), StrUtil.EMPTY);
            if (type.equals(userId)) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCreateId), type);
            } else {
                queryWrapper.ne(MybatisPlusUtil.toColumns(Post::getTypeId), StrUtil.EMPTY)
                        .eq(MybatisPlusUtil.toColumns(Post::getTypeId), type);
            }
            bean = list(queryWrapper);
        } else if (StrUtil.isNotEmpty(objectId)) {
            if (!objectId.equals(userId)) {
                queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getAnonymity), WhetherEnum.DISABLE_USING.getKey());
            }
            // 获取用户加入的圈子
            List<JoinCircle> joinCircleList = joinCircleService.queryMyJoinCircle(userId);
            List<String> circleIds = joinCircleList.stream().map(JoinCircle::getCircleId).collect(Collectors.toList());
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCreateId), objectId);
            if(CollectionUtil.isNotEmpty(circleIds)){
                queryWrapper.and(wrapper -> wrapper.in(MybatisPlusUtil.toColumns(Post::getCircleId), circleIds)
                        .or().eq(MybatisPlusUtil.toColumns(Post::getCircleId), StrUtil.EMPTY));
            }else {
                queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), StrUtil.EMPTY);
            }
            bean = list(queryWrapper);
            circleService.setDataMation(bean,Post::getCircleId);
        } else {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), StrUtil.EMPTY);
            bean = list(queryWrapper);
        }
        if (CollectionUtil.isEmpty(bean)) {
            return new ArrayList<>();
        }
        setUserMations(bean);
        List<Map<String, Object>> beans = JSONUtil.toList(JSONUtil.toJsonStr(bean), null);
        return beans;
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
        beans.forEach(bean -> {
            String id = bean.get("id").toString();
            // 设置评论信息
            bean.put("commentList", CollectionUtil.sub(commentMap.get(id), CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_FIVE));
            // 设置图片信息
            bean.put("pictureList", CollectionUtil.sub(pictureMap.get(id), CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_NINE));
            bean.put("pictureSize", pictureMap.size());
        });
        return beans;
    }

    @Override
    public void createPrepose(Post entity) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        String userIdentity = PutObject.getRequest().getHeader(WallConstants.USER_IDENTITY_KEY);
        entity.setLoginIdentity(userIdentity);
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
        // 发圈子帖子的校验
        String circleId = entity.getCircleId();
        if (StrUtil.isNotEmpty(circleId)) {
            boolean isJoin = joinCircleService.checkIsJoinCircle(circleId, userId);
            if (!isJoin) {
                throw new CustomException("您还没有加入该圈子，不能发帖");
            }
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
        setUserMation(post);
        return post;
    }

    @Override
    public void deletePreExecution(String id) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        Post one = getOne(queryWrapper);
        if (!userId.equals(one.getCreateId())) {
            throw new CustomException("无权限");
        }
    }

    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deletePostpose(String id) {
        pictureService.deleteByPostId(id);
        commentService.deleteByPostId(id);
        noticeService.deleteByObjectId(id, postService.getServiceClassName());
        historyPostService.deleteHisPostByPostIds(Collections.singletonList(id));
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
        List<String> postIds = upvoteList.stream().map(Upvote::getObjectId).collect(Collectors.toList());
        // 获取用户加入的圈子
        List<JoinCircle> joinCircleList = joinCircleService.queryMyJoinCircle(userId);
        List<String> circleIds = joinCircleList.stream().map(JoinCircle::getCircleId).collect(Collectors.toList());
        //分页
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Post> list = commentAndUpvoteOperationPost(postIds, circleIds);
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
        List<String> postIds = myCommentList.stream().map(Comment::getPostId).collect(Collectors.toList());
        // 获取用户加入的圈子
        List<JoinCircle> joinCircleList = joinCircleService.queryMyJoinCircle(userId);
        List<String> circleIds = joinCircleList.stream().map(JoinCircle::getCircleId).collect(Collectors.toList());
        //分页
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<Post> list = commentAndUpvoteOperationPost(postIds, circleIds);
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    public List<Post> commentAndUpvoteOperationPost(List<String> postIds, List<String> circleIds) {

        //查询post
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, postIds);
        if (CollectionUtil.isEmpty(circleIds)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Post::getCircleId), StrUtil.EMPTY);
        } else {
            queryWrapper.and(wrapper -> wrapper.in(MybatisPlusUtil.toColumns(Post::getCircleId), circleIds)
                    .or()
                    .eq(MybatisPlusUtil.toColumns(Post::getCircleId), StrUtil.EMPTY));
        }
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Post::getCreateTime));
        List<Post> bean = list(queryWrapper).stream().map(this::setUserMation).collect(Collectors.toList());
        //查询PictureList、commentList
        Map<String, List<Picture>> pictureMap = pictureService.getPictureMapListByIds(postIds);
        Map<String, List<Comment>> commentListMap = commentService.getCommentMapListByIds(postIds);
        //设置PictureList、commentList
        bean.forEach(post -> {
            post.setPictureList(pictureMap.get(post.getId()));
            post.setCommentList(CollectionUtil.sub(commentListMap.get(post.getId()),
                    CommonNumConstants.NUM_ZERO, CommonNumConstants.NUM_FIVE));
            String serviceClassName = getServiceClassName();
            post.setObjectKey(serviceClassName);
        });
        circleService.setDataMation(bean, Post::getCircleId);
        return bean;
    }

    @Override
    public List<Post> getBeforeThirtyDaysPost(String tenantId) {
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
        if (tenantEnable) {
            queryWrapper.eq(CommonConstants.TENANT_ID_FIELD, tenantId);
        }
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
        historyPostService.deleteHisPostByPostIds(postIds);
        remove(queryWrapper);
    }

    @Override
    public void queryHotPostList(InputObject inputObject, OutputObject outputObject) {
        List<PopularPost> popularPostList = popularPostService.queryTodayHourPopularPostList();
        List<String> postIds = popularPostList.stream()
                .map(PopularPost::getPostId).collect(Collectors.toList());
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        if (CollectionUtil.isEmpty(postIds)) {
            return;
        }
        queryWrapper.in(CommonConstants.ID, postIds);
        List<Post> bean = list(queryWrapper).stream().map(this::setUserMation).collect(Collectors.toList());
        outputObject.setBeans(bean);
        outputObject.settotal(popularPostList.size());
    }

    @Override
    public void queryUserPostCount(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String userId = params.get("userId").toString();
        List<Map<String, Integer>> beans = new ArrayList<>();
        Map<String, Integer> countMap = new HashMap<>();
        QueryWrapper<Post> postRapper = new QueryWrapper<>();
        postRapper.eq(MybatisPlusUtil.toColumns(Post::getCreateId), userId);
        List<Post> postList = list(postRapper);
        if (CollectionUtil.isEmpty(postList)) {
            return;
        }
        // 计算总评论数量
        int commentNum = postList.stream().mapToInt(item -> Integer.parseInt(item.getCommentNum())).sum();
        // 计算总点赞数量
        int upvoteNum = postList.stream().mapToInt(item -> Integer.parseInt(item.getUpvoteNum())).sum();
        countMap.put("commentNum", commentNum);
        countMap.put("upvoteNum", upvoteNum);
        countMap.put("postNum", postList.size());
        beans.add(countMap);
        outputObject.setBeans(beans);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    // 管理员删除
    @Override
    @Transactional(value = TRANSACTION_MANAGER_VALUE, rollbackFor = Exception.class)
    public void deletePost(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, id);
        remove(queryWrapper);
        noticeService.deleteByObjectId(id, postService.getServiceClassName());
    }

    @Override
    public List<Post> queryPostListByIds(List<String> postIds) {
        if (CollectionUtil.isEmpty(postIds)) {
            return Collections.emptyList();
        }
        QueryWrapper<Post> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, postIds);
        return list(queryWrapper);
    }


    @Override
    public void updatePostShareNum(String postId, int num) {
        Post post = selectById(postId);
        int shareNum = Integer.parseInt(post.getShareNum()) + num;
        post.setShareNum(String.valueOf(shareNum));
        updateById(post);
        refreshCache(postId);
    }
}
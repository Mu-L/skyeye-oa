/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.post.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.post.entity.Post;

import java.util.List;

/**
 * @ClassName: PostService
 * @Description: 帖子服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface PostService extends SkyeyeBusinessService<Post> {

    void setUserMations(List<Post> posts);

    void updateCommentCount(String id, String count);

    void updateUpvoteCount(String id, String count);

    void updateViewCount(String id, String count);

    void queryPostListByUpvote(InputObject inputObject, OutputObject outputObject);

    void queryPostListByComment(InputObject inputObject, OutputObject outputObject);

    List<Post> getBeforeThirtyDaysPost(String tenantId);

    void deleteByCircleId(String circleId);

    void queryHotPostList(InputObject inputObject, OutputObject outputObject);

    void queryUserPostCount(InputObject inputObject, OutputObject outputObject);

    void deletePost(InputObject inputObject, OutputObject outputObject);

    List<Post> queryPostListByIds(List<String> postIds);

    void updatePostShareNum(String postId, int num);

    void queryPostLists(InputObject inputObject, OutputObject outputObject);
}
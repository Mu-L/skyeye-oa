package com.skyeye.upvote.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.upvote.entity.Upvote;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: UpvoteServiceImpl
 * @Description: 点赞服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/6 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface UpvoteService extends SkyeyeBusinessService<Upvote> {

    void addOrCancelUpvote(InputObject inputObject, OutputObject outputObject);

    /**
     * 判断帖子/评论是否被指定用户点赞
     *
     * @param userId    用户id
     * @param objectIds 帖子/评论id
     * @return
     */
    Map<String, Boolean> checkUpvote(String userId, String... objectIds);

    List<Upvote> queryUpvoteList(String createId);

    void deleteUpvoteByObjectId(String userId, String objectId);
}
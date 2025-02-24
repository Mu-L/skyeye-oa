package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.forum.entity.ForumComment;

public interface ForumCommentService extends SkyeyeBusinessService<ForumComment> {
    Integer countNumByForumId(String forumId);
}

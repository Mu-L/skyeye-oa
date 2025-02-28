package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumComment;

import java.util.List;

public interface ForumCommentService extends SkyeyeBusinessService<ForumComment> {
    Integer countNumByForumId(String forumId);

    List<String> queryListByForumIds(List<String> idList);

    void queryMyForumCommentList(InputObject inputObject, OutputObject outputObject);
}

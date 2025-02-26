/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumContent;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ForumContentService extends SkyeyeBusinessService<ForumContent> {

    void queryMyForumContentList(InputObject inputObject, OutputObject outputObject);

//    void insertForumContentMation(InputObject inputObject, OutputObject outputObject);

    void deleteForumContentById(InputObject inputObject, OutputObject outputObject);

    void queryNewForumContentList(InputObject inputObject, OutputObject outputObject);
    void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject);

    void queryNewCommentList(InputObject inputObject, OutputObject outputObject);

    void queryForumListByTagId(InputObject inputObject, OutputObject outputObject);

    void queryActiveUsersList(InputObject inputObject, OutputObject outputObject);

    void querySearchForumList(InputObject inputObject, OutputObject outputObject);
    void queryMyCommentList(InputObject inputObject, OutputObject outputObject);

    void setAnonymous(List<ForumContent> forumContentList);

    void updateViewCount(String forumId, String count);

    void updateCommentCount(String id, String count);
}

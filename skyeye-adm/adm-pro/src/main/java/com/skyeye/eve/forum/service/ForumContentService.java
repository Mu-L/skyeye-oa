/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumContent;

import java.util.List;

/**
 * @ClassName: ForumContentService
 * @Description: 论坛话题信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ForumContentService extends SkyeyeBusinessService<ForumContent> {

    void queryMyForumContentList(InputObject inputObject, OutputObject outputObject);

    void deleteForumContentById(InputObject inputObject, OutputObject outputObject);

    void queryNewForumContentList(InputObject inputObject, OutputObject outputObject);

    void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject);

    void queryNewCommentList(InputObject inputObject, OutputObject outputObject);

    void queryForumListByTagId(InputObject inputObject, OutputObject outputObject);

    void queryActiveUsersList(InputObject inputObject, OutputObject outputObject);

    void setAnonymous(List<ForumContent> forumContentList);

    void updateViewCount(String forumId, String count);

    void updateCommentCount(String id, String count);

    List<ForumContent> queryForumContentListByForumIds(List<String> forumIds);

    List<ForumContent> getTodayHotForumList(String today);
}

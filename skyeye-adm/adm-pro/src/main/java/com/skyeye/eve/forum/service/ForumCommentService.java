/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumComment;

import java.util.List;

/**
 * @ClassName: ForumCommentService
 * @Description: 论坛评论信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ForumCommentService extends SkyeyeBusinessService<ForumComment> {
    Integer countNumByForumId(String forumId);

    List<String> queryListByForumIds(List<String> idList);

    void queryMyForumCommentList(InputObject inputObject, OutputObject outputObject);

    void deleteByForumId(String id);
}

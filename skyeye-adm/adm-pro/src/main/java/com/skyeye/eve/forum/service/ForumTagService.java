/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumTag;

public interface ForumTagService extends SkyeyeBusinessService<ForumTag> {

    void queryForumTagList(InputObject inputObject, OutputObject outputObject);

    void deleteForumTagById(InputObject inputObject, OutputObject outputObject);

    void editForumTagMationOrderNumUpById(InputObject inputObject, OutputObject outputObject);

    void editForumTagMationOrderNumDownById(InputObject inputObject, OutputObject outputObject);

    void queryForumTagUpStateList(InputObject inputObject, OutputObject outputObject);

    void updateUpOrDownForumTagById(InputObject inputObject, OutputObject outputObject);
}

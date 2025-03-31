/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.eve.forum.entity.ForumHot;

/**
 * @ClassName: ForumHotService
 * @Description: 论坛热门贴管理业务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/24 9:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */

public interface ForumHotService extends SkyeyeBusinessService<ForumHot> {
    void queryHotForumList(InputObject inputObject, OutputObject outputObject);

    void editHotForumMation();

    void queryHotTagList(InputObject inputObject, OutputObject outputObject);

    void queryHotForumTagList();

    void deleteByForumId(String id);
}

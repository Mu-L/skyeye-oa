/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.eve.forum.entity.ForumHistoryView;

import java.util.List;

/**
 * @ClassName: HistoryPostService
 * @Description: 历史帖子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface ForumHistoryViewService extends SkyeyeBusinessService<ForumHistoryView> {

    List<ForumHistoryView> queryMyHistory(String currentUserId);

    void deleteByForumId(String id);

}

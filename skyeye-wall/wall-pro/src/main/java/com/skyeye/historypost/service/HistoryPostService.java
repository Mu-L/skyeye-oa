/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.historypost.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.historypost.entity.HistoryPost;

import java.util.List;

/**
 * @ClassName: HistoryPostService
 * @Description: 历史帖子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface HistoryPostService extends SkyeyeBusinessService<HistoryPost> {
    List<HistoryPost> getHistoryPostById(String userId);

    void deleteMyHistoryPost(InputObject inputObject, OutputObject outputObject);

    void deleteHistoryPostByIds(InputObject inputObject, OutputObject outputObject);
}

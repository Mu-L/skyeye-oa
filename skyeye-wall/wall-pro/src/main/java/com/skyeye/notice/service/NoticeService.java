package com.skyeye.notice.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.notice.entity.Notice;

import java.util.List;

/**
 * @ClassName: NoticeService
 * @Description: 通知信息服务接口层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/24 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface NoticeService extends SkyeyeBusinessService<Notice> {
    void queryNoticeByType(InputObject inputObject, OutputObject outputObject);

    void updateStateById(InputObject inputObject, OutputObject outputObject);

    void queryUnReadNum(InputObject inputObject, OutputObject outputObject);

    void deleteVideoNoticeByCommentIds(List<String> commentIds);
}

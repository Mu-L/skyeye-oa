package com.skyeye.school.topiccomment.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.topiccomment.entity.TopicComment;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: TopicCommentService
 * @Description: 话题评论服务接口层
 * @author: lyj
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TopicCommentService extends SkyeyeBusinessService<TopicComment> {
    Long queryClassTopicJoinNum(String id);

    Long queryClassTopicJoinPersonNum(String id, String stuId);

    Map<String, Long> queryCommentNumByTopicIdsAndStuIds(String subjectClassId, List<String> stuIds);
}

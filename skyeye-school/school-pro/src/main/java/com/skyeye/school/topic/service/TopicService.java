package com.skyeye.school.topic.service;

import com.skyeye.base.business.service.SkyeyeBusinessService;
import com.skyeye.school.topic.entity.Topic;

import java.util.List;
import java.util.Map;

/**
 * @ClassName: TopicService
 * @Description: 话题服务接口层
 * @author: lyj
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
public interface TopicService extends SkyeyeBusinessService<Topic> {

    void updateCommentNum(String topicId, Integer num);

    Long queryClassTopicNum(String id);

    List<String> queryTopicIdsBySubjectClassesId(String id);

    Map<String, Long> queryStuCommentNumBySubClassesId(String id, List<String> stuIds);
}
package com.skyeye.school.topic.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.topic.dao.TopicDao;
import com.skyeye.school.topic.entity.Topic;
import com.skyeye.school.topic.service.TopicService;
import com.skyeye.school.topiccomment.service.TopicCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: TopicServiceImpl
 * @Description: 话题服务层
 * @author: lyj
 * @date: 2024/3/9 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "话题管理", groupName = "话题管理")
public class TopicServiceImpl extends SkyeyeBusinessServiceImpl<TopicDao, Topic> implements TopicService {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private TopicCommentService topicCommentService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)) {
            return CollectionUtil.newArrayList();
        }
        List<String> createIdList = beans.stream()
            .map(bean -> bean.get("createId").toString()).distinct().collect(Collectors.toList());
        Map<String, Map<String, Object>> studentMation = iUserService.queryDataMationForMapByIds(
            Joiner.on(CommonCharConstants.COMMA_MARK).join(createIdList));
        Map<String, Map<String, Object>> userMation = iAuthUserService.queryDataMationForMapByIds(
            Joiner.on(CommonCharConstants.COMMA_MARK).join(createIdList));
        beans.forEach(bean -> {
            String createId = bean.get("createId").toString();
            if (studentMation.containsKey(createId) && studentMation.get(createId) != null) {
                bean.put("createMation", studentMation.get(createId));
            } else {
                bean.put("createMation", userMation.get(createId));
            }
        });
        return beans;
    }

    @Override
    public QueryWrapper<Topic> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<Topic> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Topic::getSubjectClassesId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void updateCommentNum(String topicId, Integer num) {
        UpdateWrapper<Topic> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, topicId);
        updateWrapper.set(MybatisPlusUtil.toColumns(Topic::getCommentNum), num);
        update(updateWrapper);
    }

    @Override
    public Long queryClassTopicNum(String id) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Topic::getSubjectClassesId), id);
        return count(queryWrapper);
    }

    @Override
    public List<String> queryTopicIdsBySubjectClassesId(String id) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Topic::getSubjectClassesId), id);
        List<Topic> list = list(queryWrapper);
        return list.stream().map(Topic::getId).collect(Collectors.toList());
    }

    @Override
    public Map<String, Long> queryStuCommentNumBySubClassesId(String id, List<String> stuIds) {
        QueryWrapper<Topic> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Topic::getSubjectClassesId), id);
        List<Topic> topics = list(queryWrapper);
        if (CollectionUtil.isEmpty(topics)) {
            return Collections.emptyMap();
        }
        List<String> topicIds = topics.stream().map(Topic::getId).collect(Collectors.toList());
        return topicCommentService.queryCommentNumByTopicIdsAndStuIds(topicIds, stuIds);
    }

    @Override
    public Topic selectById(String id) {
        Topic topic = super.selectById(id);
        Map<String, Object> userMation = iAuthUserService.queryDataMationById(topic.getCreateId());
        if (CollectionUtil.isEmpty(userMation)) {
            iUserService.setDataMation(topic, Topic::getCreateId);
        }
        else {
            iAuthUserService.setDataMation(topic, Topic::getCreateId);
        }
        return topic;
    }

    @Override
    public List<Topic> selectByIds(String... ids) {
        List<Topic> topicList = super.selectByIds(ids);
        return topicList;
    }
}
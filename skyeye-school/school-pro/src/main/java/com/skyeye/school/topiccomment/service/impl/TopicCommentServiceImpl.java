package com.skyeye.school.topiccomment.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.topic.service.TopicService;
import com.skyeye.school.topiccomment.dao.TopicCommentDao;
import com.skyeye.school.topiccomment.entity.TopicComment;
import com.skyeye.school.topiccomment.service.TopicCommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Collections;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@SkyeyeService(name = "话题评论管理", groupName = "话题评论管理")
public class TopicCommentServiceImpl extends SkyeyeBusinessServiceImpl<TopicCommentDao, TopicComment> implements TopicCommentService {

    @Autowired
    private TopicService topicService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private SchoolCommonService schoolCommonService;

    public void createPostpose(TopicComment topicComment, String userId) {
        QueryWrapper<TopicComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TopicComment::getTopicId), topicComment.getTopicId());
        topicService.updateCommentNum(topicComment.getTopicId(), (int) (count(queryWrapper)));
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        if (CollectionUtil.isEmpty(beans)){
            return CollectionUtil.newArrayList();
        }
        List<String> createIdList = beans.stream()
            .map(bean -> bean.get("createId").toString()).distinct().collect(Collectors.toList());
        Map<String,Map<Boolean,Map<String,Object>>> userMation = new HashMap<>();
        for (String createId : createIdList) {
            UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(createId);
            Map<Boolean,Map<String,Object>> userOrStudentMation = new HashMap<>();
            userOrStudentMation.put(userOrStudent.getUserOrStudent(), userOrStudent.getDataMation());
            userMation.put(createId,userOrStudentMation);
        }
        beans.forEach(bean -> {
            String id = bean.get("createId").toString();
            Map<Boolean, Map<String, Object>> userOrStudentMation = userMation.get(id);
            if (userOrStudentMation.containsKey(true)){
                bean.put("studentMation", userOrStudentMation.get(true));
            }
            if (userOrStudentMation.containsKey(false)){
                bean.put("teacherMation", userOrStudentMation.get(false));
            }
        });
        return beans;
    }

    @Override
    public QueryWrapper<TopicComment> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<TopicComment> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(TopicComment::getTopicId), commonPageInfo.getHolderId());
        }
        return queryWrapper;
    }

    @Override
    public void deletePostpose(TopicComment topicComment) {
        QueryWrapper<TopicComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(TopicComment::getTopicId), topicComment.getTopicId());
        topicService.updateCommentNum(topicComment.getTopicId(), (int) count(queryWrapper));
    }

    @Override
    public Long queryClassTopicJoinNum(String id) {
        // 获取话题id
        List<String> ids = topicService.queryTopicIdsBySubjectClassesId(id);
        if (CollectionUtil.isEmpty(ids)) {
            return 0L;
        }
        QueryWrapper<TopicComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(TopicComment::getTopicId), ids);
        List<TopicComment> beans = list(queryWrapper);
        // 根据createId去重
        long count = beans.stream().map(TopicComment::getCreateId).distinct().count();
        if(count == 0){
            return (long) ids.size();
        }
        return count;
    }

    @Override
    public Long queryClassTopicJoinPersonNum(String id, String stuId) {
        // 获取话题id
        List<String> ids = topicService.queryTopicIdsBySubjectClassesId(id);
        if (CollectionUtil.isEmpty(ids)) {
            return 0L;
        }
        QueryWrapper<TopicComment> queryWrapper = new QueryWrapper<>();
        if(StrUtil.isNotEmpty(stuId)){
            queryWrapper.eq(MybatisPlusUtil.toColumns(TopicComment::getCreateId), stuId);
        }
        queryWrapper.in(MybatisPlusUtil.toColumns(TopicComment::getTopicId), ids);
        return count(queryWrapper);
    }

    @Override
    public Map<String, Long> queryCommentNumByTopicIdsAndStuIds(String subjectClassId, List<String> stuIds) {
        List<String> ids = topicService.queryTopicIdsBySubjectClassesId(subjectClassId);
        if(CollectionUtil.isEmpty(ids)){
            return Collections.emptyMap();
        }
        QueryWrapper<TopicComment> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(TopicComment::getTopicId), ids);
        queryWrapper.in(MybatisPlusUtil.toColumns(TopicComment::getCreateId), stuIds);
        List<TopicComment> list = list(queryWrapper);
        if(CollectionUtil.isEmpty(list)){
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(TopicComment::getCreateId, Collectors.counting()));
    }

}
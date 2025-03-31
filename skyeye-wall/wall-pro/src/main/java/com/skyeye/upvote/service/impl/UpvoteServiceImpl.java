package com.skyeye.upvote.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.comment.service.CommentService;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.GetUserToken;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.post.service.PostService;
import com.skyeye.upvote.dao.UpvoteDao;
import com.skyeye.upvote.entity.Upvote;
import com.skyeye.upvote.service.UpvoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @ClassName: UpvoteServiceImpl
 * @Description: 点赞服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/4/6 14:31
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "点赞管理", groupName = "点赞管理")
public class UpvoteServiceImpl extends SkyeyeBusinessServiceImpl<UpvoteDao, Upvote> implements UpvoteService {

    @Autowired
    private UpvoteService upvoteService;

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Override
    public void validatorEntity(Upvote entity) {
        super.validatorEntity(entity);
        String userToken = GetUserToken.getUserToken(InputObject.getRequest());
        if(StrUtil.isEmpty(userToken)){
            throw new CustomException("请先完成登录！");
        }
    }

    public void addOrCancelUpvote(InputObject inputObject, OutputObject outputObject) {
        Upvote upvote = inputObject.getParams(clazz);
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        upvote.setUserId(userId);
        if (commentService.getServiceClassName().equals(upvote.getObjectKey())) {
            if (estimateAddOrCancel(upvote)) {
                addUpvoteComment(upvote);
            } else {
                updateUpvoteComment(upvote);
            }
        } else {
            if (estimateAddOrCancel(upvote)) {
                addUpvotePost(upvote);
            } else {
                updateUpvotePost(upvote);
            }
        }
        outputObject.setBean(upvote);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    public boolean estimateAddOrCancel(Upvote upvote) {
        QueryWrapper<Upvote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getObjectId), upvote.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getUserId), upvote.getUserId());
        Upvote one = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(one)) {
            return true;
        }
        return false;
    }

    public void addUpvotePost(Upvote upvote) {
        upvote.setCreateTime(DateUtil.getYmdTimeAndToString());
        upvoteService.createEntity(upvote, StrUtil.EMPTY);
        postService.updateUpvoteCount(upvote.getObjectId(), String.valueOf(getUpvoteNum(upvote.getObjectId())));
    }

    public void updateUpvotePost(Upvote upvote) {
        QueryWrapper<Upvote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getObjectId), upvote.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getUserId), upvote.getUserId());
        remove(queryWrapper);
        postService.updateUpvoteCount(upvote.getObjectId(), String.valueOf(getUpvoteNum(upvote.getObjectId())));
    }

    public long getUpvoteNum(String id) {
        QueryWrapper<Upvote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getObjectId), id);
        return count(queryWrapper);
    }

    public void addUpvoteComment(Upvote upvote) {
        upvote.setCreateTime(DateUtil.getYmdTimeAndToString());
        upvoteService.createEntity(upvote, StrUtil.EMPTY);
        commentService.updateCommentUpvoteNum(upvote.getObjectId(), String.valueOf(getUpvoteNum(upvote.getObjectId())));
    }

    public void updateUpvoteComment(Upvote upvote) {
        QueryWrapper<Upvote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getObjectId), upvote.getObjectId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getUserId), upvote.getUserId());
        remove(queryWrapper);
        commentService.updateCommentUpvoteNum(upvote.getObjectId(), String.valueOf(getUpvoteNum(upvote.getObjectId())));
    }

    @Override
    public Map<String, Boolean> checkUpvote(String userId, String... objectIds) {
        List<String> objectIdList = Arrays.asList(objectIds).stream().distinct().collect(Collectors.toList());
        if (CollectionUtil.isEmpty(objectIdList)) {
            return MapUtil.newHashMap();
        }
        // 查询数据
        QueryWrapper<Upvote> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Upvote::getObjectId), objectIdList);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getUserId), userId);
        List<Upvote> upvoteList = list(queryWrapper);
        List<String> inSqlObjectIds = upvoteList.stream().map(Upvote::getObjectId).collect(Collectors.toList());

        // 构造结果数据，true代表已经点赞，false代表未点赞
        Map<String, Boolean> result = MapUtil.newHashMap(objectIdList.size());
        objectIdList.forEach(objectId -> {
            if (inSqlObjectIds.contains(objectId)) {
                result.put(objectId, true);
            } else {
                result.put(objectId, false);
            }
        });

        return result;
    }

    @Override
    public List<Upvote> queryUpvoteList(String createId) {
        QueryWrapper<Upvote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getUserId), createId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getObjectKey), postService.getServiceClassName());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(Upvote::getCreateTime));
        return list(queryWrapper);
    }

    @Override
    public void deleteUpvoteByObjectId(String userId,String objectId) {
        QueryWrapper<Upvote> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(Upvote::getUserId), userId);
        remove(queryWrapper);
    }
}
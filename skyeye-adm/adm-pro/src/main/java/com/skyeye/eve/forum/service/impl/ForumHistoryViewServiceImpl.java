/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.dao.ForumHistoryViewDao;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumHistoryView;
import com.skyeye.eve.forum.service.ForumContentService;
import com.skyeye.eve.forum.service.ForumHistoryViewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName: HistoryPostServiceImpl
 * @Description: 历史帖子信息管理
 * @author: skyeye云系列--卫志强
 * @date: 2024/3/9 14:31
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "历史帖子管理", groupName = "历史帖子管理")
public class ForumHistoryViewServiceImpl extends SkyeyeBusinessServiceImpl<ForumHistoryViewDao, ForumHistoryView> implements ForumHistoryViewService {

    @Autowired
    private ForumContentService forumContentService;

    @Override
    public String createEntity(ForumHistoryView entity, String userId) {
        QueryWrapper<ForumHistoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getForumId), entity.getForumId());
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getCreateTime), DateUtil.getYmdTimeAndToString());
        long length = count(queryWrapper);
        if (length > 0) {
            return StrUtil.EMPTY;
        }
        return super.createEntity(entity, userId);
    }

    @Override
    public void createPrepose(ForumHistoryView forumHistoryView) {
        String userId = InputObject.getLogParamsStatic().get("id").toString();
        forumHistoryView.setCreateId(userId);
        forumHistoryView.setCreateTime(DateUtil.getYmdTimeAndToString());
    }

    @Override
    public void createPostpose(ForumHistoryView forumHistoryView, String userId) {
        ForumContent forumContent = forumContentService.selectById(forumHistoryView.getForumId());
        if (StrUtil.isNotEmpty(forumContent.getBrowseNum())) {
            Integer flag = Integer.parseInt(forumContent.getBrowseNum()) + CommonNumConstants.NUM_ONE;
            forumContentService.updateViewCount(forumHistoryView.getForumId(), String.valueOf(flag));
        } else {
            forumContentService.updateViewCount(forumHistoryView.getForumId(), String.valueOf(CommonNumConstants.NUM_ZERO));
        }
    }

    @Override
    public List<ForumHistoryView> queryMyHistory(String currentUserId) {
        QueryWrapper<ForumHistoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getCreateId), currentUserId);
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ForumHistoryView::getCreateTime));
        List<ForumHistoryView> list = list(queryWrapper);
        return CollectionUtil.isEmpty(list) ? new ArrayList<>() : list;
    }

    @Override
    public void deleteByForumId(String id) {
        QueryWrapper<ForumHistoryView> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumHistoryView::getForumId), id);
        remove(queryWrapper);
    }
}
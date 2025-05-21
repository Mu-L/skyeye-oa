/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.WhetherEnum;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.SensitiveWordInit;
import com.skyeye.common.util.SensitivewordEngine;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.classenum.ContentStateEnum;
import com.skyeye.eve.forum.dao.ForumContentDao;
import com.skyeye.eve.forum.entity.ForumComment;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumHistoryView;
import com.skyeye.eve.forum.service.*;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: ForumContentServiceImpl
 * @Description: 论坛管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2021/8/7 11:09
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛话题管理", groupName = "论坛话题管理")
public class ForumContentServiceImpl extends SkyeyeBusinessServiceImpl<ForumContentDao, ForumContent> implements ForumContentService {

    @Autowired
    private ForumContentService forumContentService;

    @Autowired
    private ForumCommentService forumCommentService;

    @Autowired
    private ForumHistoryViewService forumHistoryViewService;

    @Autowired
    private ForumTagService forumTagService;

    @Autowired
    private ForumSensitiveWordsService forumSensitiveWordsService;

    @Autowired
    private ForumHotService forumHotService;

    @Override
    protected QueryWrapper<ForumContent> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getType),CommonNumConstants.NUM_ONE);
        return queryWrapper;
    }

    /**
     * 获取我的帖子列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyForumContentList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String currentUserId = inputObject.getLogParams().get("id").toString();
        setCommonPageInfoOtherInfo(commonPageInfo);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getCreateId), currentUserId)
            .eq(MybatisPlusUtil.toColumns(ForumContent::getState), ContentStateEnum.NOT_DELETE.getKey())
            .orderByDesc(MybatisPlusUtil.toColumns(ForumContent::getCreateTime));
        List<ForumContent> beans = list(queryWrapper);
        forumTagService.setTagMationForContentList(beans);
        iAuthUserService.setDataMation(beans, ForumContent::getCreateId);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void validatorEntity(ForumContent forumContent) {
        String str = querySensitiveWordsByMap(forumContent);
        if (StrUtil.isNotEmpty(str)) {
            throw new CustomException("该帖子包含以下敏感词：" + str.substring(0, str.length() - 1) + "！");
        } else {
            forumContent.setState(CommonNumConstants.NUM_ONE);
            forumContent.setReportState(CommonNumConstants.NUM_ONE);
        }
    }

    /**
     * 删除我的帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteForumContentById(InputObject inputObject, OutputObject outputObject) {
        String contentId = inputObject.getParams().get("id").toString();
        String currentUserId = inputObject.getLogParams().get("id").toString();
        UpdateWrapper<ForumContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, contentId);
        ForumContent one = getOne(updateWrapper);
        if (one.getState() == ContentStateEnum.DELETE.getKey()) {
            outputObject.setreturnMessage("该帖子已删除！");
            return;
        }
        if (!one.getCreateId().equals(currentUserId)) {
            outputObject.setreturnMessage("该帖子不是你的！");
            return;
        }
        updateWrapper.set(MybatisPlusUtil.toColumns(ForumContent::getState), ContentStateEnum.DELETE.getKey());
        update(updateWrapper);
        // 删除热门帖子记录
        forumHotService.deleteByForumId(one.getId());
        // 删除评论记录
        forumCommentService.deleteByForumId(one.getId());
        refreshCache(one.getId());
    }

    @Override
    public ForumContent selectById(String id) {
        ForumContent bean = super.selectById(id);
        forumTagService.setTagMationForContentList(Arrays.asList(bean));
        // 设置匿名
        if (bean.getAnonymous() == WhetherEnum.ENABLE_USING.getKey()) {
            bean.setCreateId(StrUtil.EMPTY);
            bean.setLastUpdateId(StrUtil.EMPTY);
        }
        iAuthUserService.setDataMation(bean, ForumContent::getCreateId);
        return bean;
    }

    /**
     * 获取最新帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryNewForumContentList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getType), CommonNumConstants.NUM_ONE);
        queryWrapper.or(
            w -> w.eq(MybatisPlusUtil.toColumns(ForumContent::getCreateId), userId)
                .eq(MybatisPlusUtil.toColumns(ForumContent::getType), CommonNumConstants.NUM_TWO)
        );
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ForumContent::getCreateTime));
        List<ForumContent> beans = list(queryWrapper);
        iAuthUserService.setDataMation(beans, ForumContent::getCreateId);
        setAnonymous(beans);
        forumTagService.setTagMationForContentList(beans);
        // 取前20条
        if (beans.size() > 20) {
            beans = beans.subList(0, 20);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 获取我的浏览信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String currentUserId = inputObject.getLogParams().get("id").toString();
        setCommonPageInfoOtherInfo(commonPageInfo);
        List<ForumHistoryView> forumHistoryViewList = forumHistoryViewService.queryMyHistory(currentUserId);
        if (CollectionUtil.isEmpty(forumHistoryViewList)) {
            return;
        }
        List<String> forumIds = forumHistoryViewList.stream().map(ForumHistoryView::getForumId).collect(Collectors.toList());
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        List<ForumContent> beans = forumContentService.selectByIds(forumIds.toArray(new String[forumIds.size()]));
        forumTagService.setTagMationForContentList(beans);
        setAnonymous(beans);
        iAuthUserService.setDataMation(beans, ForumContent::getCreateId);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 获取最新评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryNewCommentList(InputObject inputObject, OutputObject outputObject) {
        QueryWrapper<ForumComment> queryComment = new QueryWrapper<>();
        queryComment.orderByDesc(MybatisPlusUtil.toColumns(ForumComment::getCreateTime));
        List<ForumComment> forumCommentList = forumCommentService.list(queryComment);
        // 获取前15条
        if (forumCommentList.size() > 15) {
            forumCommentList = forumCommentList.subList(0, 15);
        }
        iAuthUserService.setDataMation(forumCommentList, ForumComment::getCreateId);
        iAuthUserService.setDataMation(forumCommentList, ForumComment::getReplyId);
        outputObject.setBeans(forumCommentList);
        outputObject.settotal(forumCommentList.size());
    }

    /**
     * 根据标签id获取帖子列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumListByTagId(InputObject inputObject, OutputObject outputObject) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        setCommonPageInfoOtherInfo(commonPageInfo);
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        String tagId = commonPageInfo.getObjectId();
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        // A&B&(C|(D&E))
        queryWrapper
            .like(MybatisPlusUtil.toColumns(ForumContent::getTagId), tagId)
            .eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE)
            .and(wrapper -> {
                wrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getType), CommonNumConstants.NUM_ONE)
                    .or(w -> {
                        w.eq(MybatisPlusUtil.toColumns(ForumContent::getType), CommonNumConstants.NUM_TWO)
                            .eq(MybatisPlusUtil.toColumns(ForumContent::getCreateId), currentUserId);
                    });
            });
        List<ForumContent> beans = list(queryWrapper);
        iAuthUserService.setDataMation(beans, ForumContent::getCreateId);
        setAnonymous(beans);
        forumTagService.setTagMationForContentList(beans);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }


    /**
     * 获取活跃用户
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryActiveUsersList(InputObject inputObject, OutputObject outputObject) {
        // 获取活跃用户
        //按 create_id 分组，计算每个用户的发帖数量
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE);
        List<ForumContent> contentBean = list(queryWrapper);
        Map<String, Long> contentMap = contentBean.stream().
            collect(Collectors.groupingBy(ForumContent::getCreateId, Collectors.counting()));

        Map<String, List<Long>> weightMap = new HashMap<>();
        for (Map.Entry<String, Long> entry : contentMap.entrySet()) {
            // 帖子
            List<Long> list = new ArrayList<>();
            String key = entry.getKey();
            Long value = entry.getValue();
            list.add(value);
            // 评论
            QueryWrapper<ForumComment> queryComment = new QueryWrapper<>();
            queryComment.eq(MybatisPlusUtil.toColumns(ForumComment::getCreateId), key);
            long count = forumCommentService.count(queryComment);
            list.add(count);
            weightMap.put(key, list);
        }
        // 计算权重
        Map<String, Double> weight = new HashMap<>();
        for (Map.Entry<String, List<Long>> entry : weightMap.entrySet()) {
            String key = entry.getKey();
            List<Long> value = entry.getValue();
            double w = 0.4 * value.get(CommonNumConstants.NUM_ZERO) + 0.6 * value.get(CommonNumConstants.NUM_ONE);
            weight.put(key, w);
        }
        // 排序
        List<Map.Entry<String, Double>> list = new ArrayList<>(weight.entrySet());
        list.sort((o1, o2) -> o2.getValue().compareTo(o1.getValue()));
        // 返回前15名
        List<String> userIds = new ArrayList<>();
        for (int i = 0; i < list.size() && i < 15; i++) {
            Map.Entry<String, Double> entry = list.get(i);
            userIds.add(entry.getKey());
        }
        // 设置用户信息
        List<Map<String, Object>> userInfos = new ArrayList<>();
        for (String userId : userIds) {
            Map<String, Object> userInfo = new HashMap<>();
            userInfo.put("userId", userId);
            userInfo.put("userMation", StrUtil.EMPTY);
            iAuthUserService.setMationForMap(userInfo, "userId", "userMation");
            userInfos.add(userInfo);
        }
        outputObject.setBeans(userInfos);
        outputObject.settotal(userInfos.size());
    }

    /**
     * 查找内容中的包含的敏感词
     *
     * @param forumContent
     * @return
     */
    public String querySensitiveWordsByMap(ForumContent forumContent) {
        String content = forumContent.getForumTitle() + "," + forumContent.getForumContent();
        List<Map<String, Object>> sensitiveWords = forumSensitiveWordsService.queryAllDataForMap();
        SensitiveWordInit sensitiveWordInit = new SensitiveWordInit();
        Map<String, Object> sensitiveWordMap = sensitiveWordInit.initKeyWord(sensitiveWords);
        SensitivewordEngine.sensitiveWordMap = sensitiveWordMap;
        Set<String> set = SensitivewordEngine.getSensitiveWord(content, 2);
        String str = "";
        if (set.size() > 0) {
            for (String s : set) {
                str += s + "、";
            }
        }
        return str;
    }

    /**
     * 设置匿名
     *
     * @param forumContentList
     */
    @Override
    public void setAnonymous(List<ForumContent> forumContentList) {
        for (ForumContent forumContent : forumContentList) {
            if (forumContent.getAnonymous() == WhetherEnum.ENABLE_USING.getKey()) {
                Map<String, Object> createMation = forumContent.getCreateMation();
                if (createMation == null) {
                    continue;
                }
                createMation.put("name", "匿名用户");
                createMation.put("picture", "/images/upload/wallPost/1726212288676.jpg");
                forumContent.setCreateMation(createMation);
            }
        }
    }

    /**
     * 更新浏览量
     *
     * @param forumId 帖子id
     * @param count   浏览量
     */
    @Override
    public void updateViewCount(String forumId, String count) {
        UpdateWrapper<ForumContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, forumId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ForumContent::getBrowseNum), count);
        update(updateWrapper);
        refreshCache(forumId);
    }

    /**
     * 更新评论量
     *
     * @param id    帖子id
     * @param count 评论量
     */
    @Override
    public void updateCommentCount(String id, String count) {
        UpdateWrapper<ForumContent> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ForumContent::getCommentNum), count);
        update(updateWrapper);
        refreshCache(id);
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> mapList = super.queryPageDataList(inputObject);
        List<ForumContent> beans = mapList.stream().map(map -> {
            return JSONUtil.toBean(JSONUtil.toJsonStr(map), ForumContent.class);
        }).collect(Collectors.toList());
        iAuthUserService.setDataMation(beans, ForumContent::getCreateId);
        setAnonymous(beans);
        forumTagService.setTagMationForContentList(beans);
        return JSONUtil.toList(JSONUtil.toJsonStr(beans), null);
    }

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<ForumContent> wrapper) {
        wrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE);
    }
}

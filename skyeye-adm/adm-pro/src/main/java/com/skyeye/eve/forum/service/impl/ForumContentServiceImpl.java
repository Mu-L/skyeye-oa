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
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.SensitiveWordInit;
import com.skyeye.common.util.SensitivewordEngine;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.constans.ForumConstants;
import com.skyeye.eve.forum.classenum.ContentStateEnum;
import com.skyeye.eve.forum.dao.ForumContentDao;
import com.skyeye.eve.forum.dao.ForumSensitiveWordsDao;
import com.skyeye.eve.forum.entity.ForumComment;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumHistoryView;
import com.skyeye.eve.forum.service.*;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.JedisClientService;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
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
    private ForumContentDao forumContentDao;

    @Autowired
    private ForumContentService forumContentService;

    @Autowired
    private ForumSensitiveWordsDao forumSensitiveWordsDao;

    @Autowired
    private ForumCommentService forumCommentService;

    @Autowired
    private ForumHistoryViewService forumHistoryViewService;

    @Autowired
    private ForumTagService forumTagService;

    @Autowired
    private ForumSensitiveWordsService forumSensitiveWordsService;

    @Autowired
    public JedisClientService jedisClient;

    private SolrClient solrClient;

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
        iAuthUserService.setDataMation(beans, ForumContent::getLastUpdateId);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    //    /**
//     * 新增我的帖子
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
    @Override
    public void validatorEntity(ForumContent forumContent) {
        String str = querySensitiveWordsByMap(forumContent);
        if (StrUtil.isNotEmpty(str)) {
            throw new CustomException("该帖子包含以下敏感词：" + str.substring(0, str.length() - 1) + "！");
        } else {
            forumContent.setState(CommonNumConstants.NUM_ONE);
            forumContent.setReportState(CommonNumConstants.NUM_ONE);
            // 贴子纯文本内容
            String content = forumContent.getForumContent();
            // 简介
            forumContent.setForumDesc(content.length() > 400 ? content.substring(0, 400) : content);
        }
    }

//    @Override
//    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//    public void insertForumContentMation(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        String str = querySensitiveWordsByMap(map);
//        if (str.length() > 0) {
//            outputObject.setreturnMessage("该帖子包含以下敏感词：" + str.substring(0, str.length() - 1) + "！");
//        } else {
//            map.put("state", 1);
//            map.put("reportState", 1);
//            DataCommonUtil.setCommonData(map, inputObject.getLogParams().get("id").toString());
//            // 贴子纯文本内容
//            String content = map.get("textConent").toString();
//            // 简介
//            map.put("desc", content.length() > 400 ? content.substring(0, 400) : content);
//        }
//    }

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
    }

//    @Override
//    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//    public void deleteForumContentById(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        try {
//            UpdateResponse response = solrClient.deleteById(map.get("id").toString(), 1000);
//            int status = response.getStatus();
//            if (status != 0) {
//                solrClient.rollback();
//            } else {
//                int delete = forumContentDao.deleteForumContentById(map);
//                if (delete != 1) {
//                    solrClient.rollback();
//                    outputObject.setreturnMessage("删除失败！");
//                }
//            }
//        } catch (Exception e) {
//            outputObject.setreturnMessage("删除失败！");
//        }
//    }

    /**
     * 查询帖子信息用以编辑
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumContentMationById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = forumContentDao.queryForumContentMationById(map);
        List<Map<String, Object>> beans = forumContentDao.selectForumTagById(bean);
        bean.put("tagName", beans);
        outputObject.setBean(bean);
        outputObject.settotal(1);
    }

//    /**
//     * 编辑帖子信息
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @Override
//    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
//    public void editForumContentMationById(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
////        String str = querySensitiveWordsByMap(map);
//        String str = "";
//        if (str.length() > 0) {
//            outputObject.setreturnMessage("该帖子包含以下敏感词：" + str.substring(0, str.length() - 1) + "！");
//        } else {
//            // 贴子纯文本内容
//            String content = map.get("textConent").toString();
//            // 简介
//            map.put("desc", content.length() > 400 ? content.substring(0, 400) : content);
//            Map<String, Object> bean = forumContentDao.queryForumContentMationById(map);
//            Forum forum = new Forum();
//            forum.setId(map.get("id").toString());
//            forum.setForumTitle(map.get("title").toString());
//            // 纯文本内容
//            forum.setForumContent(content);
//            forum.setForumDesc(map.get("desc").toString());
//            forum.setType(map.get("forumType").toString());
//            forum.setCreateId(bean.get("createId").toString());
//            try {
//                UpdateResponse response = solrClient.deleteById(map.get("id").toString(), 1000);
//                int delstatus = response.getStatus();
//                if (delstatus != 0) {
//                    solrClient.rollback();
//                } else {
//                    response = solrClient.addBean(forum, 1000);
//                    int addstatus = response.getStatus();
//                    if (addstatus != 0) {
//                        solrClient.rollback();
//                        outputObject.setreturnMessage("发布失败！");
//                    } else {
//                        int edit = forumContentDao.editForumContentMationById(map);
//                        if (edit != 1) {
//                            solrClient.rollback();
//                            outputObject.setreturnMessage("发布失败！");
//                        }
//                    }
//                }
//            } catch (Exception e) {
//                outputObject.setreturnMessage("发布失败！");
//            }
//        }
//    }

//    /**
//     * 帖子详情
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */

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
        iAuthUserService.setDataMation(bean, ForumContent::getLastUpdateId);
        return bean;
    }

    //    @Override
    public void queryForumContentMationToDetails(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> bean = forumContentDao.queryForumContentMationToDetails(map);
        outputObject.setBean(bean);
        outputObject.settotal(1);
        String userId = inputObject.getLogParams().get("id").toString();
        String forumId = map.get("id").toString();
        // 获取、设置浏览量
        String key = ForumConstants.forumBrowseNumsByForumId(forumId);
        if (ToolUtil.isBlank(jedisClient.get(key))) {
            jedisClient.set(key, "1");
        } else {
            String oldnum = jedisClient.get(key);
            jedisClient.set(key, String.valueOf(Integer.parseInt(oldnum) + 1));
        }
        // 设置今日被浏览的帖子
        String browseKey = ForumConstants.forumEverydayBrowseIdsByTime(DateUtil.getYmdTimeAndToString());
        if (ToolUtil.isBlank(jedisClient.get(browseKey))) {
            jedisClient.set(browseKey, forumId);
            jedisClient.set(browseKey, forumId);
        } else {
            String str = jedisClient.get(browseKey);
            if (str.indexOf(forumId) == -1) {
                jedisClient.set(browseKey, str + "," + forumId);
            }
        }
        //新增浏览信息
        String keys = ForumConstants.forumBrowseMationByUserid(userId);
        List<Map<String, Object>> beans = new ArrayList<>();
        if (ToolUtil.isBlank(jedisClient.get(keys))) {
            // 用户之前是否浏览过帖子
            Map<String, Object> m = new HashMap<>();
            m.put("forumId", forumId);
            m.put("tagName", bean.get("tagName"));
            m.put("title", bean.get("title"));
            m.put("desc", bean.get("desc"));
            m.put("userPhoto", bean.get("userPhoto"));
            m.put("userId", bean.get("userId"));
            m.put("browseTime", DateUtil.getTimeAndToString());
            beans.add(m);
            jedisClient.set(keys, JSONUtil.toJsonStr(beans));
        } else {
            beans = JSONUtil.toList(jedisClient.get(keys), null);
            // 用户之前是否浏览过当前帖子，浏览过则更改浏览时间为当前时间
            boolean ifexist = false;
            for (Map<String, Object> m : beans) {
                if (m.get("forumId").toString().equals(forumId)) {
                    m.put("tagName", bean.get("tagName"));
                    m.put("title", bean.get("title"));
                    m.put("desc", bean.get("desc"));
                    m.put("userPhoto", bean.get("userPhoto"));
                    m.put("userId", bean.get("userId"));
                    m.put("browseTime", DateUtil.getTimeAndToString());
                    ifexist = true;
                }
            }
            if (!ifexist) {
                // 没有浏览过则新增
                Map<String, Object> m = new HashMap<>();
                m.put("forumId", forumId);
                m.put("tagName", bean.get("tagName") == null ? "" : bean.get("tagName"));
                m.put("title", bean.get("title"));
                m.put("desc", bean.get("desc"));
                m.put("userPhoto", bean.get("userPhoto"));
                m.put("userId", bean.get("userId"));
                m.put("browseTime", DateUtil.getTimeAndToString());
                beans.add(m);
            }
            jedisClient.set(keys, JSONUtil.toJsonStr(beans));
        }
    }

    /**
     * 获取最新帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    /*@Override
    public void queryNewForumContentList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        List<Map<String, Object>> beans = forumContentDao.queryNewForumContentList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }*/
    @Override
    public void queryNewForumContentList(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();

        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE)
            .eq(MybatisPlusUtil.toColumns(ForumContent::getType), CommonNumConstants.NUM_ONE)
            .or().and(w -> w.eq(MybatisPlusUtil.toColumns(ForumContent::getCreateId), userId)
                .eq(MybatisPlusUtil.toColumns(ForumContent::getType), CommonNumConstants.NUM_TWO))
            .orderByDesc(MybatisPlusUtil.toColumns(ForumContent::getCreateTime));
        List<ForumContent> bean = list(queryWrapper);
        iAuthUserService.setDataMation(bean, ForumContent::getCreateId);
        List<ForumContent> beans = bean.stream().map(item -> {
            //如果时匿名的
            if (item.getAnonymous() == WhetherEnum.ENABLE_USING.getKey()) {
                Map<String, Object> createMation = item.getCreateMation();
                createMation.put("picture", "/images/upload/wallPost/1726212288676.jpg");
            }
            return item;
        }).collect(Collectors.toList());
        // 取前20条
        if (beans.size() > 20) {
            beans = beans.subList(0, 20);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 新增帖子评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertForumCommentMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("id", ToolUtil.getSurFaceId());
        map.put("commentId", user.get("id"));
        map.put("commentTime", DateUtil.getTimeAndToString());
        map.put("belongCommentId", "0");
        map.put("replyId", "");
        forumContentDao.insertForumCommentMation(map);
        // 获取、设置评论量
        String key = ForumConstants.forumCommentNumsByForumId(map.get("forumId").toString());
        if (ToolUtil.isBlank(jedisClient.get(key))) {
            jedisClient.set(key, "1");
        } else {
            String oldnum = jedisClient.get(key);
            jedisClient.set(key, String.valueOf(Integer.parseInt(oldnum) + 1));
        }
    }

    /**
     * 获取帖子评论信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumCommentList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = forumContentDao.queryForumCommentList(map);
        for (Map<String, Object> m : beans) {
            String commentTime = ToolUtil.timeFormat(m.get("commentTime").toString());
            m.put("commentTime", commentTime);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 新增帖子评论回复
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void insertForumReplyMation(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        Map<String, Object> user = inputObject.getLogParams();
        map.put("id", ToolUtil.getSurFaceId());
        map.put("commentId", user.get("id"));
        map.put("commentTime", DateUtil.getTimeAndToString());
        forumContentDao.insertForumReplyMation(map);
        // 获取、设置评论量
        String key = ForumConstants.forumCommentNumsByForumId(map.get("forumId").toString());
        if (ToolUtil.isBlank(jedisClient.get(key))) {
            jedisClient.set(key, "1");
        } else {
            String oldnum = jedisClient.get(key);
            jedisClient.set(key, String.valueOf(Integer.parseInt(oldnum) + 1));
        }
    }

    /**
     * 获取帖子评论回复信息
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumReplyList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = forumContentDao.queryForumReplyList(map);
        for (Map<String, Object> m : beans) {
            String commentTime = ToolUtil.timeFormat(m.get("commentTime").toString());
            m.put("commentTime", commentTime);
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
        iAuthUserService.setDataMation(beans, ForumContent::getLastUpdateId);
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }
//    @Override
//    public void queryForumMyBrowerList(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        String userId = inputObject.getLogParams().get("id").toString();
//        String keys = ForumConstants.forumBrowseMationByUserid(userId);
//        List<Map<String, Object>> beans = new ArrayList<>();
//        if (!ToolUtil.isBlank(jedisClient.get(keys))) {
//            beans = JSONUtil.toList(jedisClient.get(keys), null);
//            // 按浏览时间给集合排序
//            beans.sort(new Comparator<Map<String, Object>>() {
//                @Override
//                public int compare(Map<String, Object> m1, Map<String, Object> m2) {
//                    int flag = m1.get("browseTime").toString().compareTo(m2.get("browseTime").toString());
//                    return -flag;
//                }
//            });
//            int count = beans.size();
//            int pageMaxSize = Integer.parseInt(map.get("page").toString()) * Integer.parseInt(map.get("limit").toString());
//            if (count < pageMaxSize) {
//                pageMaxSize = count;
//            }
//            beans = beans.subList((Integer.parseInt(map.get("page").toString()) - 1) * Integer.parseInt(map.get("limit").toString()), pageMaxSize);
//            for (Map<String, Object> m : beans) {
//                String key = ForumConstants.forumBrowseNumsByForumId(m.get("forumId").toString());
//                if (ToolUtil.isBlank(jedisClient.get(key))) {
//                    // 浏览量
//                    m.put("browseNum", 0);
//                } else {
//                    String browseNum = jedisClient.get(key);
//                    m.put("browseNum", browseNum);
//                }
////                Map<String, Object> ma = forumContentDao.selectForumCommentNumById(m);
//                Integer countNumByForumId = forumCommentService.countNumByForumId(m.get("forumId").toString());
//
//                if (!ToolUtil.isBlank(String.valueOf(countNumByForumId))) {
//                    // 评论数
//                    m.put("commentNum", countNumByForumId);
//                } else {
//                    m.put("commentNum", 0);
//                }
//                // 浏览时间
//                m.put("browseTime", ToolUtil.timeFormat(m.get("browseTime").toString()));
//            }
//        }
//        outputObject.setBeans(beans);
//        outputObject.settotal(beans.size());
//    }

    /**
     * 获取最新评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryNewCommentList(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        List<Map<String, Object>> beans = forumContentDao.queryNewCommentList(map);
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.select(CommonConstants.ID)
            .ne(MybatisPlusUtil.toColumns(ForumContent::getCommentNum), CommonNumConstants.NUM_ZERO);
        List<ForumContent> onlyIds = list(queryWrapper);
        if (CollectionUtil.isEmpty(onlyIds)) {
            return;
        }
        List<String> idList = onlyIds.stream().map(ForumContent::getId).collect(Collectors.toList());
        List<String> forumContentIdList = forumCommentService.queryListByForumIds(idList);
        List<ForumContent> beans = forumContentService.selectByIds(String.valueOf(forumContentIdList));
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
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
        for (ForumContent bean : beans) {
            if (bean.getAnonymous() == WhetherEnum.DISABLE_USING.getKey()) {
                bean.setLastUpdateId(StrUtil.EMPTY);
                bean.setCreateId(StrUtil.EMPTY);
            }
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }
//    @Override
//    public void queryForumListByTagId(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        List<Map<String, Object>> beans = new ArrayList<>();
//        long total = 0;
//        if (!map.get("tagId").toString().equals("hot")) {
//            map.put("userId", inputObject.getLogParams().get("id"));
//            Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
//            beans = forumContentDao.queryForumListByTagId(map);
//            total = pages.getTotal();
//            for (Map<String, Object> m : beans) {
//                String createTime = ToolUtil.timeFormat(m.get("createTime").toString());
//                m.put("createTime", createTime);
//                String key = ForumConstants.forumBrowseNumsByForumId(m.get("id").toString());
//                if (ToolUtil.isBlank(jedisClient.get(key))) {
//                    // 浏览量
//                    m.put("browseNum", 0);
//                } else {
//                    String browseNum = jedisClient.get(key);
//                    m.put("browseNum", browseNum);
//                }
//            }
//        } else {
//            map.put("userId", inputObject.getLogParams().get("id"));
//            beans = forumContentDao.queryAllHotForumList(map);
//            for (Map<String, Object> m : beans) {
//                String createTime = ToolUtil.timeFormat(m.get("createTime").toString());
//                m.put("createTime", createTime);
//                String key = ForumConstants.forumBrowseNumsByForumId(m.get("id").toString());
//                if (ToolUtil.isBlank(jedisClient.get(key))) {
//                    // 浏览量
//                    m.put("browseNum", 0);
//                } else {
//                    String browseNum = jedisClient.get(key);
//                    m.put("browseNum", browseNum);
//                }
//            }
//            // 按浏览量和评论数给集合排序
//            beans.sort(new Comparator<Map<String, Object>>() {
//                @Override
//                public int compare(Map<String, Object> m1, Map<String, Object> m2) {
//                    Integer m1num = Integer.parseInt(m1.get("browseNum").toString()) + Integer.parseInt(m1.get("commentNum").toString());
//                    Integer m2num = Integer.parseInt(m2.get("browseNum").toString()) + Integer.parseInt(m2.get("commentNum").toString());
//                    int flag = m1num.compareTo(m2num);
//                    return -flag;
//                }
//            });
//            int count = beans.size();
//            int pageMaxSize = Integer.parseInt(map.get("page").toString()) * Integer.parseInt(map.get("limit").toString());
//            if (count < pageMaxSize) {
//                pageMaxSize = count;
//            }
//            beans = beans.subList((Integer.parseInt(map.get("page").toString()) - 1) * Integer.parseInt(map.get("limit").toString()), pageMaxSize);
//            total = count;
//        }
//        outputObject.setBeans(beans);
//        outputObject.settotal(total);
//    }

    /**
     * 获取热门标签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryHotTagList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = forumContentDao.queryHotTagList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
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

        outputObject.setBeans(userIds);
        outputObject.settotal(userIds.size());
    }
    /*@Override
    public void queryActiveUsersList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans = forumContentDao.queryActiveUsersList(map);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }*/

    /**
     * 获取热门贴
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryHotForumList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        List<Map<String, Object>> beans = forumContentDao.queryHotForumList(map);
        for (Map<String, Object> m : beans) {
            String createTime = ToolUtil.timeFormat(m.get("createTime").toString());
            m.put("createTime", createTime);
            String key = ForumConstants.forumBrowseNumsByForumId(m.get("id").toString());
            if (ToolUtil.isBlank(jedisClient.get(key))) {
                // 浏览量
                m.put("browseNum", 0);
            } else {
                String browseNum = jedisClient.get(key);
                m.put("browseNum", browseNum);
            }
        }
        //按浏览量和评论数给集合排序
        beans.sort(new Comparator<Map<String, Object>>() {
            @Override
            public int compare(Map<String, Object> m1, Map<String, Object> m2) {
                Integer m1num = Integer.parseInt(m1.get("browseNum").toString()) + Integer.parseInt(m1.get("commentNum").toString());
                Integer m2num = Integer.parseInt(m2.get("browseNum").toString()) + Integer.parseInt(m2.get("commentNum").toString());
                int flag = m1num.compareTo(m2num);
                return -flag;
            }
        });
        int count = beans.size();
        int pageMaxSize = 6;
        if (count < pageMaxSize) {
            pageMaxSize = count;
        }
        outputObject.setBeans(beans.subList(0, pageMaxSize));
        outputObject.settotal(beans.size());
    }

    /**
     * 获取用户搜索的帖子
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySearchForumList(InputObject inputObject, OutputObject outputObject) {
        String searchValue = inputObject.getParams().get("searchValue").toString();
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(MybatisPlusUtil.toColumns(ForumContent::getForumTitle), searchValue)
            .or().like(MybatisPlusUtil.toColumns(ForumContent::getForumDesc), searchValue);
        List<ForumContent> beans = list(queryWrapper);
        if (CollectionUtil.isEmpty(beans)){
            return;
        }
        forumTagService.setTagMationForContentList(beans);
        iAuthUserService.setDataMation(beans, ForumContent::getCreateId);
        iAuthUserService.setDataMation(beans, ForumContent::getLastUpdateId);
        setAnonymous(beans);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }
//    @Override
//    public void querySearchForumList(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        String searchValue = map.get("searchValue").toString();
//        List<Map<String, Object>> beans = new ArrayList<>();
//        List<Map<String, Object>> rbeans = new ArrayList<>();
//        // 关键字模糊查询
//        SolrQuery query = new SolrQuery();
//        String forumTitle = "forumTitle:" + searchValue;
//        String forumDesc = " OR forumDesc:" + searchValue;
//        String forumContent = " OR forumContent:" + searchValue;
//        query.set("q", forumTitle + forumDesc + forumContent);
//        query.setStart(0);
//        query.setRows(20);
//        query.setHighlight(true); //开启高亮
//        query.addHighlightField("forumContent"); //高亮字段
//        query.setHighlightSimplePre("<font color='red'>"); //高亮单词的前缀
//        query.setHighlightSimplePost("</font>"); //高亮单词的后缀
//        query.setHighlightFragsize(400);
//        int count = 0;
//        String createId = inputObject.getLogParams().get("id").toString();
//        try {
//            QueryResponse response = solrClient.query(query);
//            SolrDocumentList documentList = response.getResults();
//            for (SolrDocument document : documentList) {
//                beans.add(document);
//            }
//            Map<String, Map<String, List<String>>> maplist = response.getHighlighting();
//            if (beans != null) {
//                for (Map<String, Object> m : beans) {
//                    if (m.get("type").toString().replaceAll("\\[|\\]", "").substring(0, 1).equals("1")) {
//                        for (String key : maplist.keySet()) {
//                            if (key.equals(m.get("id").toString())) {
//                                Map<String, List<String>> fieldMap = maplist.get(key);
//                                if (fieldMap.size() > 0) {
//                                    m.put("forumContent", "..." + fieldMap.get("forumContent").get(0));
//                                }
//                            }
//                        }
//                        rbeans.add(m);
//                    } else {
//                        if (m.get("createId").toString().replaceAll("\\[|\\]", "").equals(createId)) {
//                            for (String key : maplist.keySet()) {
//                                if (key.equals(m.get("id").toString())) {
//                                    Map<String, List<String>> fieldMap = maplist.get(key);
//                                    if (fieldMap.size() > 0) {
//                                        m.put("forumContent", "..." + fieldMap.get("forumContent").get(0));
//                                    }
//                                }
//                            }
//                            rbeans.add(m);
//                        }
//                    }
//                }
//            }
//            count = rbeans.size();
//        } catch (SolrServerException e) {
//            outputObject.setreturnMessage("搜索失败！");
//        } catch (Exception e) {
//            outputObject.setreturnMessage("搜索失败！");
//        }
//        outputObject.setBeans(rbeans);
//        outputObject.settotal(count);
//    }

    /**
     * 获取solr上次同步数据的时间
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void querySolrSynchronousTime(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = new HashMap<>();
        String keys = ForumConstants.forumSolrSynchronoustime();
        if (!ToolUtil.isBlank(jedisClient.get(keys))) {
            String synchronousTime = jedisClient.get(keys);
            map.put("synchronousTime", synchronousTime);
        }
        outputObject.setBean(map);
        outputObject.settotal(1);
    }

//    /**
//     * solr同步数据
//     *
//     * @param inputObject  入参以及用户信息等获取对象
//     * @param outputObject 出参以及提示信息的返回值对象
//     */
//    @Override
//    public void updateSolrSynchronousData(InputObject inputObject, OutputObject outputObject) {
//        Map<String, Object> map = inputObject.getParams();
//        List<Map<String, Object>> beans = forumContentDao.queryAllForumList(map);
//        DocumentObjectBinder binder = new DocumentObjectBinder();
//        try {
//            for (Map<String, Object> t : beans) {
//                Forum forum = new Forum();
//                forum.setId(t.get("id").toString());
//                forum.setForumTitle(t.get("title").toString());
//                forum.setForumContent(t.get("content").toString());
//                forum.setForumDesc(t.get("desc").toString());
//                forum.setType(t.get("forumType").toString());
//                forum.setCreateId(t.get("createId").toString());
//                SolrInputDocument doc = binder.toSolrInputDocument(forum);
//                solrClient.add(doc);
//            }
//            solrClient.commit();
//            String keys = ForumConstants.forumSolrSynchronoustime();
//            String nowTime = DateUtil.getTimeAndToString();
//            if (!ToolUtil.isBlank(jedisClient.get(keys))) {
//                jedisClient.del(keys);
//                jedisClient.set(keys, nowTime);
//            } else {
//                jedisClient.set(keys, nowTime);
//            }
//            map.put("synchronousTime", nowTime);
//        } catch (SolrServerException e) {
//            outputObject.setreturnMessage("同步失败！");
//        } catch (Exception e) {
//            outputObject.setreturnMessage("同步失败！");
//        }
//        outputObject.setBean(map);
//        outputObject.settotal(1);
//    }

    /**
     * 获取我的帖子列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyCommentList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String userId = inputObject.getLogParams().get("id").toString();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ForumContent> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumContent::getCreateId), userId)
            .eq(MybatisPlusUtil.toColumns(ForumContent::getState), CommonNumConstants.NUM_ONE)
            .orderByDesc(MybatisPlusUtil.toColumns(ForumContent::getCreateTime));
        List<ForumContent> bean = list(queryWrapper);
        iAuthUserService.setName(bean, "createId", "createName");
        iAuthUserService.setName(bean, "lastUpdateId", "lastUpdateName");
        List<Map<String, Object>> mapList = new ArrayList<>();
        for (ForumContent forumContent : bean) {
            Map<String, Object> map = new HashMap<>();
            map.put("forumContentMation", forumContent);
            QueryWrapper<ForumComment> queryComment = new QueryWrapper<>();
            queryComment.eq(MybatisPlusUtil.toColumns(ForumComment::getForumId), forumContent.getId())
                .orderByDesc(MybatisPlusUtil.toColumns(ForumComment::getCommentTime));
            List<ForumComment> list = forumCommentService.list(queryComment);
            if (CollectionUtil.isNotEmpty(list)) {
                iAuthUserService.setDataMation(list, ForumComment::getReplyId);
                map.put("forumCommentMation", list);
            }
            mapList.add(map);
        }
        outputObject.setBeans(mapList);
        outputObject.settotal(page.getTotal());
    }
    /*@Override
    public void queryMyCommentList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = forumContentDao.queryMyCommentList(map);
        for (Map<String, Object> m : beans) {
            String commentTime = ToolUtil.timeFormat(m.get("commentTime").toString());
            m.put("commentTime", commentTime);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }*/

    /**
     * 根据评论id删除评论
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void deleteCommentById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        forumContentDao.deleteCommentById(map);
    }

    /**
     * 获取我的通知列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryMyNoticeList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        map.put("userId", inputObject.getLogParams().get("id"));
        Page pages = PageHelper.startPage(Integer.parseInt(map.get("page").toString()), Integer.parseInt(map.get("limit").toString()));
        List<Map<String, Object>> beans = forumContentDao.queryMyNoticeList(map);
        for (Map<String, Object> m : beans) {
            String sendTime = ToolUtil.timeFormat(m.get("sendTime").toString());
            m.put("sendTime", sendTime);
        }
        outputObject.setBeans(beans);
        outputObject.settotal(pages.getTotal());
    }

    /**
     * 根据通知id删除通知
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteNoticeById(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        forumContentDao.deleteNoticeById(map);
    }

    /**
     * 查找内容中的包含的敏感词
     *
     * @param forumContent
     * @return
     */
    public String querySensitiveWordsByMap(ForumContent forumContent) {
        String content = forumContent.getForumTitle() + "," + forumContent.getForumContent();
        List<Map<String, Object>> sensitiveWords;
        if (ToolUtil.isBlank(jedisClient.get(ForumConstants.forumSensitiveWordsAll()))) {
            sensitiveWords = forumSensitiveWordsService.queryAllDataForMap();
            jedisClient.set(ForumConstants.forumSensitiveWordsAll(), JSONUtil.toJsonStr(sensitiveWords));
        } else {
            sensitiveWords = JSONUtil.toList(jedisClient.get(ForumConstants.forumSensitiveWordsAll()), null);
        }
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
    public void setAnonymous(List<ForumContent> forumContentList) {
        for (ForumContent forumContent : forumContentList) {
            if (forumContent.getAnonymous() == WhetherEnum.ENABLE_USING.getKey()) {
                Map<String, Object> createMation = forumContent.getCreateMation();
                createMation.put("name", "匿名用户");
                createMation.put("picture", "/images/upload/wallPost/1726212288676.jpg");
                forumContent.setCreateMation(createMation);
            }
        }
    }

    /**
     * 更新浏览量-----wst
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
     * 更新评论量-----wst
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
}

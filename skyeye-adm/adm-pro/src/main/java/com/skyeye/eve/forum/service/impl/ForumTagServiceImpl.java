/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.classenum.ForumStateEnum;
import com.skyeye.eve.forum.dao.ForumTagDao;
import com.skyeye.eve.forum.entity.ForumTag;
import com.skyeye.eve.forum.service.ForumTagService;
import com.skyeye.exception.CustomException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @ClassName: ForumTagServiceImpl
 * @Description: 论坛标签管理服务类
 * @author: skyeye云系列--卫志强
 * @date: 2021/7/6 22:52
 * @Copyright: 2021 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "论坛标签管理", groupName = "论坛标签管理")
public class ForumTagServiceImpl extends SkyeyeBusinessServiceImpl<ForumTagDao, ForumTag> implements ForumTagService {


    /**
     * 查出所有论坛标签列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumTagList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        String keyword = commonPageInfo.getKeyword();
        String userId = inputObject.getLogParams().get("id").toString();
        Page pages = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ForumTag> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(keyword)) {
            // 根据标签名模糊搜索
            queryWrapper.like(MybatisPlusUtil.toColumns(ForumTag::getTagName), keyword);
        }
        queryWrapper.ne(MybatisPlusUtil.toColumns(ForumTag::getState), ForumStateEnum.IS_DELETE.getKey())
                .eq(MybatisPlusUtil.toColumns(ForumTag::getCreateId), userId)
                .orderByAsc(MybatisPlusUtil.toColumns(ForumTag::getOrderBy));
        List<ForumTag> list = list(queryWrapper);
        outputObject.setBeans(list);
        outputObject.settotal(pages.getTotal());
    }

    @Override
    public void validatorEntity(ForumTag entity) {
        String tagName = entity.getTagName();
        QueryWrapper<ForumTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumTag::getTagName), tagName);
        // tagName不能重复
        if (count(queryWrapper) > 0 && StrUtil.isEmpty(entity.getId())) {
            throw new CustomException("标签名已存在");
        }
        // 编辑时的校验
        ForumTag forumTag = selectById(entity.getId());
        if (count(queryWrapper) > 0 && !forumTag.getTagName().equals(tagName)) {
            // 如果编辑时修改了tagName，并且数据库中已经存在该tagName，则抛出异常
            throw new CustomException("标签名已存在");
        }
    }

    @Override
    public void createPrepose(ForumTag entity) {
        entity.setState(ForumStateEnum.NEW_Built.getKey());
        QueryWrapper<ForumTag> wrapper = new QueryWrapper<>();
        int count = (int) count(wrapper);
        entity.setOrderBy(count + CommonNumConstants.NUM_ONE);
    }


    /**
     * 删除论坛标签
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void deleteForumTagById(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String id = inputObject.getParams().get("id").toString();
        ForumTag forumTag = selectById(id);
        int state = forumTag.getState();
        if (state == ForumStateEnum.NEW_Built.getKey() || state == ForumStateEnum.DOWN_LINE.getKey()) {
            // 新建或者下线可以删除----逻辑删除
            forumTag.setState(ForumStateEnum.IS_DELETE.getKey());
            updateEntity(forumTag, userId);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 论坛标签上线或下线
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void updateUpOrDownForumTagById(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String id = inputObject.getParams().get("id").toString();
        ForumTag forumTag = selectById(id);
        int state = forumTag.getState();
        // 新建或者下线可以上线
        if (state == ForumStateEnum.NEW_Built.getKey() || state == ForumStateEnum.DOWN_LINE.getKey()) {
            forumTag.setState(ForumStateEnum.UP_LINE.getKey());
        } else {
            // 上线可以下线
            forumTag.setState(ForumStateEnum.DOWN_LINE.getKey());
        }
        updateEntity(forumTag, userId);
    }

    /**
     * 论坛标签上移
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editForumTagMationOrderNumUpById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        // 获取当前数据的同级分类下的上一条数据
        ForumTag forumTag = selectById(id);
        int orderBy = forumTag.getOrderBy();
        QueryWrapper<ForumTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(MybatisPlusUtil.toColumns(ForumTag::getState), ForumStateEnum.IS_DELETE.getKey())
                .lt(MybatisPlusUtil.toColumns(ForumTag::getOrderBy), orderBy)
                .orderByDesc(MybatisPlusUtil.toColumns(ForumTag::getOrderBy));
        List<ForumTag> bean = list(queryWrapper);
        if (CollectionUtils.isEmpty(bean)) {
            throw new CustomException("该数据已经是第一条数据，无法上移");
        } else {
            ForumTag upForumTag = bean.get(0);
            // 修改当前数据的排序
            forumTag.setOrderBy(upForumTag.getOrderBy());
            upForumTag.setOrderBy(orderBy);

            updateEntity(forumTag, userId);
            updateEntity(upForumTag, userId);
        }
    }

    /**
     * 论坛标签下移
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    @Transactional(value = "transactionManager", rollbackFor = Exception.class)
    public void editForumTagMationOrderNumDownById(InputObject inputObject, OutputObject outputObject) {
        String id = inputObject.getParams().get("id").toString();
        String userId = inputObject.getLogParams().get("id").toString();
        // 获取当前数据的同级分类下的下一条数据
        ForumTag forumTag = selectById(id);
        int orderBy = forumTag.getOrderBy();
        QueryWrapper<ForumTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne(MybatisPlusUtil.toColumns(ForumTag::getState), ForumStateEnum.IS_DELETE.getKey())
                .gt(MybatisPlusUtil.toColumns(ForumTag::getOrderBy), orderBy)
                .orderByAsc(MybatisPlusUtil.toColumns(ForumTag::getOrderBy));
        List<ForumTag> bean = list(queryWrapper);
        if (CollectionUtils.isEmpty(bean)) {
            throw new CustomException("已经是最后一条数据了,无法下移");
        } else {
            ForumTag downForumTag = bean.get(0);
            // 修改当前数据的排序
            forumTag.setOrderBy(downForumTag.getOrderBy());
            downForumTag.setOrderBy(orderBy);

            updateEntity(forumTag, userId);
            updateEntity(downForumTag, userId);
        }
    }

    /**
     * 获取已经上线的论坛标签列表
     *
     * @param inputObject  入参以及用户信息等获取对象
     * @param outputObject 出参以及提示信息的返回值对象
     */
    @Override
    public void queryForumTagUpStateList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ForumTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumTag::getState), ForumStateEnum.UP_LINE.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ForumTag::getOrderBy));
        List<ForumTag> beans = list(queryWrapper);
        outputObject.setBean(beans);
        outputObject.settotal(page.getTotal());
    }
/*
    @Override
    public void queryForumTagUpStateList(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        List<Map<String, Object>> beans;
        if (ToolUtil.isBlank(jedisClient.get(ForumConstants.FORUM_TAG_UP_STATE_LIST))) {
            beans = forumTagDao.queryForumTagUpStateList(map);
            jedisClient.set(ForumConstants.FORUM_TAG_UP_STATE_LIST, JSONUtil.toJsonStr(beans));
        } else {
            beans = JSONUtil.toList(jedisClient.get(ForumConstants.FORUM_TAG_UP_STATE_LIST), null);
        }
        if (!beans.isEmpty()) {
            outputObject.setBeans(beans);
            outputObject.settotal(beans.size());
        }
    }*/

}

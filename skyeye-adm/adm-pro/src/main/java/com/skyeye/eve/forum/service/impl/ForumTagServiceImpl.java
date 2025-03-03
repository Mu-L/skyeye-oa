/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.eve.forum.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.forum.classenum.ForumStateEnum;
import com.skyeye.eve.forum.dao.ForumTagDao;
import com.skyeye.eve.forum.entity.ForumContent;
import com.skyeye.eve.forum.entity.ForumTag;
import com.skyeye.eve.forum.service.ForumTagService;
import com.skyeye.eve.service.IAuthUserService;
import com.skyeye.exception.CustomException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    @Autowired
    private IAuthUserService iAuthUserService;

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        return beans;
    }

    @Override
    public void getQueryWrapper(InputObject inputObject, QueryWrapper<ForumTag> wrapper) {
        String currentUserId = inputObject.getLogParams().get("id").toString();
        wrapper.eq(MybatisPlusUtil.toColumns(ForumTag::getState), ForumStateEnum.NEW_Built.getKey())
            .orderByAsc(MybatisPlusUtil.toColumns(ForumTag::getOrderBy))
            .eq(MybatisPlusUtil.toColumns(ForumTag::getCreateId), currentUserId);
    }

    @Override
    public ForumTag selectById(String id) {
        ForumTag forumTag = super.selectById(id);
        iAuthUserService.setName(forumTag, "createId", "createName");
        iAuthUserService.setName(forumTag, "lastUpdateId", "lastUpdateName");
        return forumTag;
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
        wrapper.select(CommonConstants.ID);
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
        if (state == ForumStateEnum.NEW_Built.getKey()) {
            // 新建或者下线可以删除----逻辑删除
            forumTag.setState(ForumStateEnum.IS_DELETE.getKey());
            updateEntity(forumTag, userId);
        } else {
            outputObject.setreturnMessage("该数据状态已改变，请刷新页面！");
        }
    }

    /**
     * 为话题列表中设置标签信息
     *
     * @param beans
     */
    @Override
    public void setTagMationForContentList(List<ForumContent> beans) {
        List<String> tagIdList = new ArrayList<>();
        for (ForumContent bean : beans) {
            if (StrUtil.isNotEmpty(bean.getTagId())) {
                String[] tagIdArr = bean.getTagId().split(",");
                tagIdList.addAll(Arrays.asList(tagIdArr));
            }
        }
        List<String> distinctTagIds = tagIdList.stream().distinct().collect(Collectors.toList());
        if (CollectionUtils.isEmpty(distinctTagIds)) {
            return;
        }
        QueryWrapper<ForumTag> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(CommonConstants.ID, distinctTagIds);
        List<ForumTag> tagList = list(queryWrapper);
        Map<String, Map<String, Object>> tagMap = tagList.stream()
            .collect(Collectors.toMap(ForumTag::getId, forumTag -> {
                return JSONUtil.toBean(JSONUtil.toJsonStr(forumTag), null);
            }));
        for (ForumContent bean : beans) {
            for (String s : bean.getTagId().split(",")) {
                if (tagMap.containsKey(s)) {
                    if (bean.getTagList() == null) { // 检查是否为 null
                        bean.setTagList(new ArrayList<>()); // 初始化为一个空集合
                    }
                    bean.getTagList().add(tagMap.get(s));
                }
            }
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
        queryWrapper.eq(MybatisPlusUtil.toColumns(ForumTag::getState), ForumStateEnum.NEW_Built.getKey());
        queryWrapper.orderByDesc(MybatisPlusUtil.toColumns(ForumTag::getOrderBy));
        List<ForumTag> beans = list(queryWrapper);
        iAuthUserService.setName(beans, "createId", "createName");
        iAuthUserService.setName(beans, "lastUpdateId", "lastUpdateName");
        outputObject.setBean(beans);
        outputObject.settotal(page.getTotal());
    }

}

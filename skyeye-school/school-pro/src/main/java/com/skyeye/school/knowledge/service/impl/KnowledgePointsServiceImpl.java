/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.knowledge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.knowledge.dao.KnowledgePointsDao;
import com.skyeye.school.knowledge.entity.KnowledgePoints;
import com.skyeye.school.knowledge.service.KnowledgePointsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * @ClassName: KnowledgePointsServiceImpl
 * @Description: 知识点管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/8 15:28
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "知识点管理", groupName = "知识点管理", teamAuth = true)
public class KnowledgePointsServiceImpl extends SkyeyeBusinessServiceImpl<KnowledgePointsDao, KnowledgePoints> implements KnowledgePointsService {

    @Autowired
    private ChapterService chapterService;

    @Override
    public QueryWrapper<KnowledgePoints> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<KnowledgePoints> queryWrapper = super.getQueryWrapper(commonPageInfo);
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgePoints::getSubjectClassesId), commonPageInfo.getObjectId());
        }
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        chapterService.setMationForMap(beans, "chapterId", "chapterMation");
        return beans;
    }

    @Override
    public KnowledgePoints selectById(String id) {
        KnowledgePoints knowledgePoints = super.selectById(id);
        chapterService.setDataMation(knowledgePoints, KnowledgePoints::getChapterId);
        if (ObjectUtil.isNotEmpty(knowledgePoints.getChapterMation())) {
            knowledgePoints.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", knowledgePoints.getChapterMation().getSection(), knowledgePoints.getChapterMation().getName()));
        }
        return knowledgePoints;
    }

    @Override
    public void queryKnowledgePointsList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<KnowledgePoints> queryWrapper = getQueryWrapper(commonPageInfo);
        queryWrapper.eq(CommonConstants.ID,inputObject.getParams().get("id").toString());
        String keyword = commonPageInfo.getKeyword();
        if (StrUtil.isNotEmpty(keyword)) {
            queryWrapper.like(MybatisPlusUtil.toColumns(KnowledgePoints::getName), keyword);
        }
        List<KnowledgePoints> list = list(queryWrapper);
        outputObject.setBean(list);
        outputObject.settotal(page.getTotal());
    }
}

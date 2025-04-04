/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.knowledge.service.impl;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
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
            queryWrapper.eq(MybatisPlusUtil.toColumns(KnowledgePoints::getObjectId), commonPageInfo.getObjectId());
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
    protected void validatorEntity(KnowledgePoints entity) {
        super.validatorEntity(entity);
        String remark = entity.getRemark();
        if (remark != null && remark.length() > 200) {
            // 截取前200个字符
            remark = remark.substring(0, 200);
        }
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
    public List<KnowledgePoints> queryKnowledge(String knowledgeId) {
        QueryWrapper<KnowledgePoints> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, knowledgeId);
        List<KnowledgePoints> knowledgePointsList = list(queryWrapper);
        return knowledgePointsList;
    }
}

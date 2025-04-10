/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chapter.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.chapter.dao.ChapterDao;
import com.skyeye.school.chapter.entity.Chapter;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.courseware.service.CoursewareService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @ClassName: ChapterServiceImpl
 * @Description: 章节管理服务层
 * @author: skyeye云系列--卫志强
 * @date: 2023/8/25 11:08
 * @Copyright: 2023 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "章节管理", groupName = "章节管理")
public class ChapterServiceImpl extends SkyeyeBusinessServiceImpl<ChapterDao, Chapter> implements ChapterService {

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private CoursewareService coursewareService;

    @Override
    public void queryChapterListBySubjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectId = map.get("subjectId").toString();
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chapter::getObjectId), subjectId)
            .orderByAsc(MybatisPlusUtil.toColumns(Chapter::getSection));
        List<Chapter> chapterList = list(queryWrapper);
        chapterList.forEach(chapter -> {
            chapter.setName(String.format(Locale.ROOT, "第 %s 章 %s", chapter.getSection(), chapter.getName()));
        });
        iAuthUserService.setDataMation(chapterList, Chapter::getCreateId);
        iAuthUserService.setDataMation(chapterList, Chapter::getLastUpdateId);
        outputObject.setBeans(chapterList);
        outputObject.settotal(chapterList.size());
    }

    @Override
    public void queryChapterAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> params = inputObject.getParams();
        String subjectClassesId = params.get("subjectClassesId").toString();
        SubjectClasses subjectClasses = subjectClassesService.selectById(subjectClassesId);
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chapter::getObjectId), subjectClasses.getObjectId())
            .orderByAsc(MybatisPlusUtil.toColumns(Chapter::getSection));
        List<Chapter> chapterList = list(queryWrapper);
        if (CollectionUtil.isEmpty(chapterList)) {
            return;
        }
        // 查这个科目下的人数
        Integer classNum = subjectClassesService.queryStuNumBySubjectId(subjectClasses.getObjectId(), subjectClasses.getClassesId());
        if (classNum == CommonNumConstants.NUM_ZERO) {
            return;
        }
        List<Object> beans = new ArrayList<>();

        // 按章节分开的作业分析--
        Map<String, Map<String, Object>> assAnaMap = assignmentService.queryAssAnalysisByChapters(classNum, chapterList, null);
        // 全部作业分析
        Map<String, Map<String, Object>> assAnaAllMap = assignmentService.queryAssAnalysisByChapters(classNum, chapterList, "all");

        // 互动课件分析
        Map<String, Map<String, Object>> coursewareAnaMap = coursewareService.queryInterAnalysisByChapters(classNum, chapterList, null);
        // 全部互动课件分析
        Map<String, Map<String, Object>> coursewareAnaAllMap = coursewareService.queryInterAnalysisByChapters(classNum, chapterList, "all");
        Map<String, List<Map<String, Object>>> temp = new HashMap<>();
        Map<String, List<Map<String, Object>>> allTemp = new HashMap<>();
        for (Map.Entry<String, Map<String, Object>> entry : assAnaMap.entrySet()) {
            temp.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }
        for (Map.Entry<String, Map<String, Object>> entry : coursewareAnaMap.entrySet()) {
            temp.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }
        for (Map.Entry<String, Map<String, Object>> entry : assAnaAllMap.entrySet()) {
            allTemp.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }
        for (Map.Entry<String, Map<String, Object>> entry : coursewareAnaAllMap.entrySet()) {
            allTemp.computeIfAbsent(entry.getKey(), k -> new ArrayList<>()).add(entry.getValue());
        }
        beans.add(temp);
        beans.add(allTemp);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    @Override
    public List<Chapter> queryChaptersBySubjectId(String subjectId) {
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chapter::getObjectId), subjectId)
            .orderByAsc(MybatisPlusUtil.toColumns(Chapter::getSection));
        return list(queryWrapper);
    }

}

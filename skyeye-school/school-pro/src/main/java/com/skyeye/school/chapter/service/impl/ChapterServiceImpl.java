/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chapter.service.impl;

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
import com.skyeye.school.datum.service.DatumService;
import com.skyeye.school.measurement.service.MeasurementService;
import com.skyeye.school.subject.service.SubjectClassesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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
    private MeasurementService measurementService;

    @Autowired
    private DatumService datumService;

    @Autowired
    private CoursewareService coursewareService;

    @Override
    public void queryChapterListBySubjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectId = map.get("subjectId").toString();
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chapter::getObjectId), subjectId);
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
        String subjectId = params.get("subjectId").toString();
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chapter::getObjectId), subjectId)
            .orderByAsc(MybatisPlusUtil.toColumns(Chapter::getSection));
        // 查这个科目下的人数
        Long classNum = subjectClassesService.queryStuNumBySubjectId(subjectId);
        List<Map<String, Map<String, Map<String, Double>>>> beans = new ArrayList<>();
        List<Chapter> chapterList = list(queryWrapper);
        List<String> ids = chapterList.stream().map(Chapter::getId).collect(Collectors.toList());
        String[] idsArray = ids.toArray(new String[0]);
        Map<String, Map<String, Double>> temp = new HashMap<>();
        Map<String, Map<String, Map<String, Double>>> map = new HashMap<>();
        for (Chapter chapter : chapterList) {
            String name = "chapterAnalysis" + chapter.getSection();
            // 作业分析--
            Map<String, Double> assAnalysis = assignmentService.queryAssigmentByChapterId(classNum, chapter.getId());
            temp.put("assAnalysis", assAnalysis);

            // 资料分析--
            Map<String, Double> materialAnalysis = datumService.queryDatumByChapterId(classNum, chapter.getId());
            temp.put("materialAnalysis", materialAnalysis);

            // 互动课件分析
            Map<String, Double> coursewareAnalysis = coursewareService.queryCoursewareByChapterId(classNum, chapter.getId());
            temp.put("coursewareAnalysis", coursewareAnalysis);
            // TODO:互动答题分析--
            map.put(name, temp);
            beans.add(map);
            map = new HashMap<>();
        }
        if (idsArray.length > CommonNumConstants.NUM_ONE) {
            // 全部作业分析
            Map<String, Double> assAnalysis = assignmentService.queryAssigmentByChapterId(classNum, idsArray);
            // 全部资料分析
            Map<String, Double> materialAnalysis = datumService.queryDatumByChapterId(classNum, idsArray);
            // 全部互动课件分析
            Map<String, Double> coursewareAnalysis = coursewareService.queryCoursewareByChapterId(classNum, idsArray);
            temp.put("assAnalysis", assAnalysis);
//            temp.put("testAnalysis", testAnalysis);
            temp.put("materialAnalysis", materialAnalysis);
            temp.put("coursewareAnalysis", coursewareAnalysis);
            // TODO:全部互动答题分析--
        }
        map.put("allAnalysis", temp);
        beans.add(map);
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

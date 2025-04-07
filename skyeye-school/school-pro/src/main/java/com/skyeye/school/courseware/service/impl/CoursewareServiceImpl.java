/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.courseware.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.SchoolConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.object.PutObject;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.classenum.LoginIdentity;
import com.skyeye.school.assignment.entity.Assignment;
import com.skyeye.school.chapter.entity.Chapter;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.courseware.dao.CoursewareDao;
import com.skyeye.school.courseware.entity.Courseware;
import com.skyeye.school.courseware.entity.CoursewareStudy;
import com.skyeye.school.courseware.service.CoursewareService;
import com.skyeye.school.courseware.service.CoursewareStudyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.DecimalFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: CoursewareServiceImpl
 * @Description: 互动课件服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/7/2 9:29
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "互动课件", groupName = "互动课件")
public class CoursewareServiceImpl extends SkyeyeBusinessServiceImpl<CoursewareDao, Courseware> implements CoursewareService {

    @Autowired
    private CoursewareStudyService coursewareStudyService;

    @Autowired
    private ChapterService chapterService;

    @Override
    public Courseware selectById(String id) {
        Courseware courseware = super.selectById(id);
        chapterService.setDataMation(courseware, Courseware::getChapterId);
        if (ObjectUtil.isNotEmpty(courseware.getChapterMation())) {
            courseware.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", courseware.getChapterMation().getSection(), courseware.getChapterMation().getName()));
        }

        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份信息
            String userId = InputObject.getLogParamsStatic().get("id").toString();
            Map<String, String> stateMap = coursewareStudyService.queryStudyState(Arrays.asList(courseware.getId()), userId);
            courseware.setState(stateMap.get(courseware.getId()));
        }
        return courseware;
    }

    @Override
    public void queryCoursewareListBySubjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectId = map.get("subjectId").toString();
        QueryWrapper<Courseware> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Courseware::getObjectId), subjectId);
        List<Courseware> coursewareList = list(queryWrapper);

        String userIdentity = PutObject.getRequest().getHeader(SchoolConstants.USER_IDENTITY_KEY);
        if (StrUtil.equals(userIdentity, LoginIdentity.STUDENT.getKey())) {
            // 学生身份信息
            String userId = inputObject.getLogParams().get("id").toString();
            List<String> coursewareIdList = coursewareList.stream().map(Courseware::getId).collect(Collectors.toList());
            Map<String, String> stateMap = coursewareStudyService.queryStudyState(coursewareIdList, userId);
            coursewareList.forEach(courseware -> {
                courseware.setState(stateMap.get(courseware.getId()));
            });
        }

        chapterService.setDataMation(coursewareList, Courseware::getChapterId);
        String serviceClassName = getServiceClassName();
        coursewareList.forEach(courseware -> {
            if (ObjectUtil.isNotEmpty(courseware.getChapterMation())) {
                courseware.getChapterMation().setName(String.format(Locale.ROOT, "第 %s 章 %s", courseware.getChapterMation().getSection(), courseware.getChapterMation().getName()));
            }
            courseware.setServiceClassName(serviceClassName);
        });

        iAuthUserService.setDataMation(coursewareList, Courseware::getCreateId);
        iAuthUserService.setDataMation(coursewareList, Courseware::getLastUpdateId);
        outputObject.setBeans(coursewareList);
        outputObject.settotal(coursewareList.size());
    }

    @Override
    public Map<String, Double> queryCoursewareByChapterId(Long classNum, String... ids) {
        Map<String, Double> map = new HashMap<>();
        double sumSize = 0;
        double finishRate = 0;
        map.put("activeNum", sumSize);
        map.put("finishRate", finishRate);
        for (String id : ids) {
            QueryWrapper<Courseware> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq(MybatisPlusUtil.toColumns(Assignment::getChapterId), id);
            List<Courseware> list = list(queryWrapper);
            if (CollectionUtil.isEmpty(list)) {
                continue;
            }
            sumSize += list.size();
            List<String> cIds = list.stream().map(Courseware::getId).collect(Collectors.toList());
            double rate = coursewareStudyService.queryCoursewareFinshRate(cIds, classNum);
            finishRate = finishRate + rate;
        }
        if (finishRate == 0 && ids.length > 1) {
            finishRate = finishRate / ids.length;
        }
        map.put("finishRate", finishRate);
        return map;
    }

    @Override
    public Long queryClassCoursewareNum(String id, String chapterId) {
        QueryWrapper<Courseware> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(chapterId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Courseware::getChapterId), chapterId);
        }
        return count(queryWrapper);
    }

    @Override
    public Long queryStuCoursewareNum(String id, String stuId, String chapterId) {
        QueryWrapper<Courseware> queryWrapper = new QueryWrapper<>();
        if (StrUtil.isNotEmpty(chapterId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(Courseware::getChapterId), chapterId);
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(Courseware::getCreateId), stuId);
        return count(queryWrapper);
    }

    @Override
    public Map<String, Map<String, Object>> queryInterAnalysisByChapters(Integer classNum, List<Chapter> chapterList, String type) {
        List<String> chapterIds = chapterList.stream().map(Chapter::getId).collect(Collectors.toList());
        QueryWrapper<Courseware> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(Courseware::getChapterId), chapterIds);
        List<Courseware> list = list(queryWrapper); // 所有章节下的课件
        Map<String, Map<String, Object>> resultMap = new HashMap<>();
        Map<String, Map<String, Object>> temp = new HashMap<>();
        for (Chapter chapter : chapterList) {
            Map<String, Object> map = new HashMap<>();
            map.put("type", "互动课件");
            map.put("name", chapter.getName());
            map.put("activeNum", CommonNumConstants.NUM_ZERO);
            map.put("completeRate", CommonNumConstants.NUM_ZERO + "%");
            temp.put(chapter.getId(), map);
        }
        Map<String, Object> temp1 = new HashMap<>();
        if (StrUtil.isNotEmpty(type) && CollectionUtil.isEmpty(list)) {
            temp1.put("type", "互动课件");
            temp1.put("name", type);
            temp1.put("activeNum", CommonNumConstants.NUM_ZERO);
            temp1.put("completeRate", CommonNumConstants.NUM_ZERO + "%");
            resultMap.put(type, temp1);
            return resultMap;
        }
        if (CollectionUtil.isEmpty(list)) {
            return temp;
        }
        // 按章节id分组
        Map<String, List<Courseware>> map = list.stream().collect(Collectors.groupingBy(Courseware::getChapterId));
        List<String> coursewareIds = list.stream().map(Courseware::getId).collect(Collectors.toList()); // 所有课件id

        // 获取所有完成作业情况
        List<CoursewareStudy> coursewareSubs = coursewareStudyService.queryCoursewareSubByCoursewareIds(coursewareIds);
        Map<String, List<CoursewareStudy>> coursewareSubMap = coursewareSubs.stream().collect(Collectors.groupingBy(CoursewareStudy::getCoursewareId));
        // 互动课件分析


        if (StrUtil.isNotEmpty(type)) {
            double completeNum = coursewareSubs.size(); // 完成互动课件次数数
            double totalNum = list.size(); // 总互动课件次数
            temp1.put("type", "互动课件");
            temp1.put("name", type);
            temp1.put("activeNum", totalNum);
            String completeRate = new DecimalFormat("0.0%").format(completeNum / (totalNum * classNum));
            temp1.put("completeRate", completeRate);
            resultMap.put(type, temp1);
            return resultMap;
        }
        for (Chapter chapter : chapterList) {
            Map<String, Object> t = new HashMap<>();
            t.put("type", "互动课件");
            t.put("name", chapter.getName());
            List<Courseware> coursewares = map.get(chapter.getId());
            t.put("activeNum", coursewares.size());
            double completeNum = 0;
            for (Courseware courseware : coursewares) {
                List<CoursewareStudy> coursewareSub = coursewareSubMap.get(courseware.getId());
                if (CollectionUtil.isNotEmpty(coursewareSub)) {
                    completeNum += coursewareSub.size();
                }
            }
            String completeRate = new DecimalFormat("0.0%").format(completeNum / (coursewares.size() * classNum));
            t.put("completeRate", completeRate);
            resultMap.put(chapter.getId(), t);
        }
        return resultMap;
    }
}

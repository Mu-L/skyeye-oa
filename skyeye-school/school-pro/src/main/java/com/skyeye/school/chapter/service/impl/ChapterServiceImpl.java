/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.chapter.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.CalculationUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.assignment.entity.Assignment;
import com.skyeye.school.assignment.entity.AssignmentSub;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.assignment.service.AssignmentSubService;
import com.skyeye.school.chapter.dao.ChapterDao;
import com.skyeye.school.chapter.entity.Chapter;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.courseware.entity.Courseware;
import com.skyeye.school.courseware.entity.CoursewareStudy;
import com.skyeye.school.courseware.service.CoursewareService;
import com.skyeye.school.courseware.service.CoursewareStudyService;
import com.skyeye.school.exam.service.ExamDirectoryAnService;
import com.skyeye.school.exam.service.ExamService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
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
    private CoursewareService coursewareService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamDirectoryAnService examDirectoryAnService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private AssignmentSubService assignmentSubService;

    @Autowired
    private IUserService iUserService;

    @Autowired
    private CoursewareStudyService coursewareStudyService;

    @Override
    public void queryChapterListBySubjectId(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subjectId = map.get("subjectId").toString();
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chapter::getObjectId), subjectId)
            .orderByAsc(MybatisPlusUtil.toColumns(Chapter::getSection));
        List<Chapter> chapterList = list(queryWrapper);
        chapterList.forEach(chapter -> {
            String serviceClassName = getServiceClassName();
            chapter.setServiceClassName(serviceClassName);
            chapter.setRealName(String.format(Locale.ROOT, "第 %s 章 %s", chapter.getSection(), chapter.getName()));
        });
        iAuthUserService.setDataMation(chapterList, Chapter::getCreateId);
        iAuthUserService.setDataMation(chapterList, Chapter::getLastUpdateId);
        outputObject.setBeans(chapterList);
        outputObject.settotal(chapterList.size());
    }


    private Map<String, Integer> calculateAssignment(List<Assignment> assignmentList) {
        Map<String, Integer> assiIdSubNumMap = new HashMap<>();
        if (CollectionUtil.isEmpty(assignmentList)) {
            return assiIdSubNumMap;
        }
        List<String> assiIdList = assignmentList.stream().map(Assignment::getId).collect(Collectors.toList());
        // 查出所有的作业提交记录
        List<AssignmentSub> assignmentSubList = assignmentSubService.queryByAssignmentIdList(assiIdList);
        // 计算有提交记录的作业
        for (AssignmentSub assignmentSub : assignmentSubList) {
            if (assiIdSubNumMap.containsKey(assignmentSub.getAssignmentId())) {
                assiIdSubNumMap.put(assignmentSub.getAssignmentId(), assiIdSubNumMap.get(assignmentSub.getAssignmentId()) + CommonNumConstants.NUM_ONE);
            } else {
                assiIdSubNumMap.put(assignmentSub.getAssignmentId(), CommonNumConstants.NUM_ONE);
            }
        }
        // 计算没有提交记录的作业
        for (Assignment assignment : assignmentList) {
            if (!assiIdSubNumMap.containsKey(assignment.getId())) {
                assiIdSubNumMap.put(assignment.getId(), CommonNumConstants.NUM_ZERO);
            }
        }
        return assiIdSubNumMap;
    }

    private Map<String, Integer> calculateCourseware(List<Courseware> coursewareList, List<String> userIdList) {
        Map<String, Integer> coursewareIdSubNumMap = new HashMap<>();
        if (CollectionUtil.isEmpty(coursewareList)) {
            return coursewareIdSubNumMap;
        }
        List<String> coursewareIdList = coursewareList.stream().map(Courseware::getId).collect(Collectors.toList());
        // 查出所有的互动课件提交记录
        List<CoursewareStudy> coursewareStudyList = coursewareStudyService.queryByCoursewareIdList(coursewareIdList);
        // 过滤出某个班级的互动课件提交记录
        List<CoursewareStudy> coursewareStudyListFilter = coursewareStudyList.stream()
            .filter(coursewareStudy -> userIdList.contains(coursewareStudy.getCreateId())).collect(Collectors.toList());
        // 计算有提交记录的互动课件
        for (CoursewareStudy coursewareStudy : coursewareStudyListFilter) {
            if (coursewareIdSubNumMap.containsKey(coursewareStudy.getCoursewareId())) {
                coursewareIdSubNumMap.put(coursewareStudy.getCoursewareId(), coursewareIdSubNumMap.get(coursewareStudy.getCoursewareId()) + CommonNumConstants.NUM_ONE);
            } else {
                coursewareIdSubNumMap.put(coursewareStudy.getCoursewareId(), CommonNumConstants.NUM_ONE);
            }
        }
        // 计算没有提交记录的互动课件
        for (Courseware courseware : coursewareList) {
            if (!coursewareIdSubNumMap.containsKey(courseware.getId())) {
                coursewareIdSubNumMap.put(courseware.getId(), CommonNumConstants.NUM_ZERO);
            }
        }
        return coursewareIdSubNumMap;
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
        // 查询该科目下的班级的所有学生
        List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryBySubjectClassesId(subjectClassesId);
        if (CollectionUtil.isEmpty(subjectClassesStuList)) {
            return;
        }
        List<String> stuNoList = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
        List<Map<String, Object>> userList = iUserService.queryListBuStudentNumberList(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList));
        List<String> classUserIdList = userList.stream().map(user -> user.get("id").toString()).collect(Collectors.toList());
        // 取出班级下的所有作业------------------------作业操作
        List<Assignment> assignmentList = assignmentService.queryBySubjectClassesId(subjectClassesId);
        Map<String, Integer> assiIdSubNumMap = calculateAssignment(assignmentList);
        // 将作业按章节分组
        Map<String, List<Assignment>> chapterAssiMap = assignmentList.stream().collect(Collectors.groupingBy(Assignment::getChapterId));
        // 互动课件操作
        List<Courseware> coursewareList = coursewareService.queryBySubjectId(subjectClasses.getObjectId());
        Map<String, Integer> courStudyNumMap = calculateCourseware(coursewareList, classUserIdList);
        // 将互动课件按章节分组
        Map<String, List<Courseware>> chapterCoursewareMap = coursewareList.stream().collect(Collectors.groupingBy(Courseware::getChapterId));
        // 测试操作
        List<Map<String, Object>> examList = examService.queryListBySubjectIdAndState(subjectClasses.getObjectId(),CommonNumConstants.NUM_TWO);
        List<String> examIdList = examList.stream().map(map -> map.get("id").toString()).collect(Collectors.toList());
        // 查出所有的考试提交记录
        List<Map<String, Object>> examAnswerList = examDirectoryAnService.queryExamAnserByExamIds(examIdList);
        // 计算应交的数量。如果没有试卷则直接为0，否则计算
        String shouldSubNum = CollectionUtil.isEmpty(examList) ? String.valueOf(CommonNumConstants.NUM_ZERO) :
            CalculationUtil.multiply(String.valueOf(examList.size()), String.valueOf(stuNoList.size()), CommonNumConstants.NUM_TWO);

        List<Map<String, Object>> beans = new ArrayList<>();
        // 计算每一个章节的数据
        setValueForPart(beans, chapterList, subjectClassesStuList, assiIdSubNumMap, courStudyNumMap, chapterAssiMap, chapterCoursewareMap);
        // 计算所有章节的合数据
        setValueForAll(beans, subjectClassesStuList, coursewareList, examList, examAnswerList, assignmentList, shouldSubNum);
        beans = setPercentSign(beans);
        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    /**
     * 设置分章节数据
     *
     * @param beans                 所有章节的数据（不包括合数据）
     * @param chapterList           章节列表
     * @param subjectClassesStuList 学生列表
     * @param assiIdSubNumMap       作业提交的学生分组信息
     * @param courStudyNumMap       互动课件的提交学生分组信息
     * @param chapterAssiMap        根据章节分组后的作业
     * @param chapterCoursewareMap  根据章节分组后的互动课件
     */
    private void setValueForPart(List<Map<String, Object>> beans,
                                 List<Chapter> chapterList, List<SubjectClassesStu> subjectClassesStuList,
                                 Map<String, Integer> assiIdSubNumMap, Map<String, Integer> courStudyNumMap,
                                 Map<String, List<Assignment>> chapterAssiMap, Map<String, List<Courseware>> chapterCoursewareMap) {
        int i = CommonNumConstants.NUM_ZERO;
        for (Chapter chapter : chapterList) {
            Map<String, Object> chapterMap = new HashMap<>();
            chapterMap.put("sort", ++i);
            chapterMap.put("name", chapter.getName());
            List<Assignment> assignments = chapterAssiMap.getOrDefault(chapter.getId(), Collections.emptyList());
            chapterMap.put("assignmentSum", assignments.size());
            // 计算作业完成率
            int assignmentCompleteRate = assignments.stream().mapToInt(assignment -> assiIdSubNumMap.getOrDefault(assignment.getId(), CommonNumConstants.NUM_ZERO)).sum();
            chapterMap.put("assignmentCompleteRate", assignmentCompleteRate == CommonNumConstants.NUM_ZERO ? "0.0"
                : CalculationUtil.divide(String.valueOf(assignmentCompleteRate), String.valueOf(subjectClassesStuList.size()), CommonNumConstants.NUM_FOUR));

            List<Courseware> coursewares = chapterCoursewareMap.getOrDefault(chapter.getId(), Collections.emptyList());
            chapterMap.put("coursewareSum", coursewares.size());
            // 计算互动课件完成率
            int coursewareCompleteRate = coursewares.stream().mapToInt(courseware -> courStudyNumMap.getOrDefault(courseware.getId(), CommonNumConstants.NUM_ZERO)).sum();
            chapterMap.put("coursewareCompleteRate", coursewareCompleteRate == CommonNumConstants.NUM_ZERO ? "0.0":
                CalculationUtil.divide(String.valueOf(coursewareCompleteRate), String.valueOf(subjectClassesStuList.size()), CommonNumConstants.NUM_FOUR));
            beans.add(chapterMap);
        }
    }

    /**
     * 设置所有章节的合数据
     *
     * @param beans                 返回数据
     * @param subjectClassesStuList 学生数据
     * @param coursewareList        互动课件数据
     * @param examList              试卷数据
     * @param examAnswerList        试卷回答数据
     * @param assignmentList        作业数据
     * @param shouldSubNum          应交的考试数量
     */
    private void setValueForAll(List<Map<String, Object>> beans, List<SubjectClassesStu> subjectClassesStuList,
                                List<Courseware> coursewareList, List<Map<String, Object>> examList,
                                List<Map<String, Object>> examAnswerList, List<Assignment> assignmentList, String shouldSubNum) {
        Map<String, Object> allChapterMap = new HashMap<>();
        allChapterMap.put("sort", CommonNumConstants.NUM_ZERO);
        allChapterMap.put("name", "全部");
        allChapterMap.put("assignmentSum", assignmentList.size());
        allChapterMap.put("assignmentCompleteRate",
            CalculationUtil.divide(
                String.valueOf(beans.stream().mapToDouble(map -> Double.parseDouble(map.get("assignmentCompleteRate").toString())).sum()),
                String.valueOf(subjectClassesStuList.size()), CommonNumConstants.NUM_TWO));
        allChapterMap.put("coursewareSum", coursewareList.size());
        allChapterMap.put("coursewareCompleteRate",
            CalculationUtil.divide(
                String.valueOf(beans.stream().mapToDouble(map -> Double.parseDouble(map.get("coursewareCompleteRate").toString())).sum()),
                String.valueOf(subjectClassesStuList.size()), CommonNumConstants.NUM_TWO));
        allChapterMap.put("examSum", examList.size());
        allChapterMap.put("examCompleteRate",
            // 应交数量为0或者考试数量为0则直接为0，否则计算
            Double.parseDouble(shouldSubNum) == CommonNumConstants.NUM_ZERO || examList.size() == CommonNumConstants.NUM_ZERO ?
                "0.0" : CalculationUtil.divide(String.valueOf(examAnswerList.size()), shouldSubNum, CommonNumConstants.NUM_FOUR));
        beans.add(allChapterMap);
    }

    /**
     * 设置百分比和排序
     *
     * @param beans 被操作的数据
     */
    private List<Map<String, Object>> setPercentSign(List<Map<String, Object>> beans) {
        String zeroDouble = "0.0";
        String oneHundredDouble = "1.00";
        // 添加百分比, 并排序
        return beans.stream().map(bean -> {
            String assignmentCompleteRate = bean.get("assignmentCompleteRate").toString();
            String coursewareCompleteRate = bean.get("coursewareCompleteRate").toString();
            bean.put("assignmentCompleteRate", assignmentCompleteRate.equals(zeroDouble) ? CommonNumConstants.NUM_ZERO :
                assignmentCompleteRate.equals(oneHundredDouble) ? "100%" : CalculationUtil.multiply(assignmentCompleteRate, "100", CommonNumConstants.NUM_TWO) + "%");
            bean.put("coursewareCompleteRate", coursewareCompleteRate.equals(zeroDouble) ? CommonNumConstants.NUM_ZERO :
                coursewareCompleteRate.equals(oneHundredDouble) ? "100%" : CalculationUtil.multiply(coursewareCompleteRate, "100", CommonNumConstants.NUM_TWO) + "%");
            if (bean.containsKey("examCompleteRate")) {
                String examCompleteRate = bean.get("examCompleteRate").toString();
                bean.put("examCompleteRate", examCompleteRate.equals(zeroDouble) ? CommonNumConstants.NUM_ZERO :
                    examCompleteRate.equals(oneHundredDouble) ? "100%" : CalculationUtil.multiply(examCompleteRate, "100", CommonNumConstants.NUM_TWO) + "%");
            }

//            if (!assignmentCompleteRate.equals(zeroDouble)) {
//                bean.put("assignmentCompleteRate", CalculationUtil.multiply(assignmentCompleteRate, "100", CommonNumConstants.NUM_TWO) + "%");
//            }
//            if (!coursewareCompleteRate.equals(zeroDouble)) {
//                bean.put("coursewareCompleteRate", CalculationUtil.multiply(coursewareCompleteRate, "100", CommonNumConstants.NUM_TWO) + "%");
//            }
//            if (bean.containsKey("examCompleteRate")) {
//                String examCompleteRate = bean.get("examCompleteRate").toString();
//                if (!examCompleteRate.equals(zeroDouble)) {
//                    bean.put("examCompleteRate", CalculationUtil.multiply(examCompleteRate, "100", CommonNumConstants.NUM_TWO) + "%");
//                }
//            }
            return bean;
        }).sorted(Comparator.comparing(bean -> (Integer) bean.get("sort"))).collect(Collectors.toList());
    }

    @Override
    public List<Chapter> queryChaptersBySubjectId(String subjectId) {
        QueryWrapper<Chapter> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(Chapter::getObjectId), subjectId)
            .orderByAsc(MybatisPlusUtil.toColumns(Chapter::getSection));
        return list(queryWrapper);
    }

}

/*******************************************************************************
 * Copyright 卫志强 QQ：598748873@qq.com Inc. All rights reserved. 开源地址：https://gitee.com/doc_wei01/skyeye
 ******************************************************************************/

package com.skyeye.school.subject.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.constans.FileConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.enumeration.QRCodeLinkType;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.FileUtil;
import com.skyeye.common.util.ToolUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.common.util.qrcode.QRCodeLogoUtil;
import com.skyeye.exception.CustomException;
import com.skyeye.jedis.util.RedisLock;
import com.skyeye.school.announcement.service.AnnouncementService;
import com.skyeye.school.assignment.service.AssignmentService;
import com.skyeye.school.assignment.service.AssignmentSubService;
import com.skyeye.school.chapter.entity.Chapter;
import com.skyeye.school.chapter.service.ChapterService;
import com.skyeye.school.checkwork.service.CheckworkService;
import com.skyeye.school.courseware.service.CoursewareService;
import com.skyeye.school.datum.service.DatumService;
import com.skyeye.school.exam.service.ExamDirectoryAnService;
import com.skyeye.school.exam.service.ExamService;
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.semester.service.SemesterService;
import com.skyeye.school.subject.dao.SubjectClassesDao;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import com.skyeye.school.subject.service.SubjectClassesTopService;
import com.skyeye.school.subject.service.SubjectService;
import com.skyeye.school.topic.service.TopicService;
import com.skyeye.school.topiccomment.service.TopicCommentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @ClassName: SubjectClassesServiceImpl
 * @Description: 科目表与班级表的关系服务层
 * @author: skyeye云系列--卫志强
 * @date: 2024/6/10 14:52
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "科目表与班级表的关系管理", groupName = "科目管理")
public class SubjectClassesServiceImpl extends SkyeyeBusinessServiceImpl<SubjectClassesDao, SubjectClasses> implements SubjectClassesService {

    @Value("${IMAGES_PATH}")
    private String tPath;

    private static Logger LOGGER = LoggerFactory.getLogger(SubjectClassesServiceImpl.class);

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private ClassesService classesService;

    @Autowired
    private SemesterService semesterService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private AnnouncementService announcementService;

    @Autowired
    private TopicService topicService;

    @Autowired
    private TopicCommentService topicCommentService;

    @Autowired
    private AssignmentService assignmentService;

    @Autowired
    private AssignmentSubService assignmentSubService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CoursewareService coursewareService;

    @Autowired
    private CheckworkService checkworkService;

    @Autowired
    private SubjectClassesTopService subjectClassesTopService;

    @Autowired
    private DatumService datumService;

    @Autowired
    private ExamService examService;

    @Autowired
    private ExamDirectoryAnService examDirectoryAnService;

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @Override
    public QueryWrapper<SubjectClasses> getQueryWrapper(CommonPageInfo commonPageInfo) {
        QueryWrapper<SubjectClasses> queryWrapper = super.getQueryWrapper(commonPageInfo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), commonPageInfo.getObjectId());
        return queryWrapper;
    }

    @Override
    public List<Map<String, Object>> queryPageDataList(InputObject inputObject) {
        List<Map<String, Object>> beans = super.queryPageDataList(inputObject);
        classesService.setMationForMap(beans, "classesId", "classesMation");
        return beans;
    }

    @Override
    public void queryNoPageSubjectClassesList(InputObject inputObject, OutputObject outputObject) {
        String subjectId = inputObject.getParams().get("objectId").toString();
        if (StrUtil.isBlank(subjectId)) {
            return;
        }
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId);
        List<SubjectClasses> list = list(queryWrapper);
        classesService.setDataMation(list, SubjectClasses::getClassesId);
        outputObject.setBeans(list);
        outputObject.settotal(list.size());
    }

    @Override
    public void createPrepose(SubjectClasses entity) {
        Subject subject = subjectService.selectById(entity.getObjectId());
        String imgPath = tPath.replace("images", StrUtil.EMPTY) + subject.getImg();
        // 生成加课码编码
        String code = ToolUtil.getFourWord();
        entity.setSourceCode(code);
        // 生成二维码
        String content = QRCodeLinkType.getJsonStrByType(QRCodeLinkType.SUBJECT_CLASSES.getKey(), code);
        String sourceCodeLogo = QRCodeLogoUtil.encode(content, imgPath, tPath, true, FileConstants.FileUploadPath.SCHOOL_SUBJECT.getType()[0]);
        entity.setSourceCodeLogo(sourceCodeLogo);
        entity.setPeopleNum(CommonNumConstants.NUM_ZERO);
        refreshCache(entity.getId());
        scoreTypeChildService.initScoreTypeChild(entity.getObjectId(), entity.getId());

    }

    @Override
    public void deletePostpose(SubjectClasses entity) {
        FileUtil.deleteFile(tPath.replace("images", StrUtil.EMPTY) + entity.getSourceCode());
        // 删除班级学生关联表
        subjectClassesStuService.deleteBySubClassLinkId(Arrays.asList(entity.getId()));
        // 删除班级学生置顶课程
        subjectClassesTopService.deleteSubjectClassesTopBySubClassLinkId(entity.getId());
        // 删除成绩信息
        scoreTypeChildService.deleteBySubjectIdAndSubjectClassId(entity.getObjectId(), entity.getId());
    }

    @Override
    public SubjectClasses selectById(String id) {
        SubjectClasses subjectClasses = super.selectById(id);
        classesService.setDataMation(subjectClasses, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClasses, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClasses, SubjectClasses::getSemesterId);
        return subjectClasses;
    }

    @Override
    public List<SubjectClasses> selectByIds(String... ids) {
        List<SubjectClasses> subjectClassesList = super.selectByIds(ids);
        classesService.setDataMation(subjectClassesList, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClassesList, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClassesList, SubjectClasses::getSemesterId);
        return subjectClassesList;
    }

    @Override
    public void querySubjectClassesBySourceCode(InputObject inputObject, OutputObject outputObject) {
        String sourceCode = inputObject.getParams().get("sourceCode").toString();
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getSourceCode), sourceCode);
        SubjectClasses subjectClasses = getOne(queryWrapper, false);
        if (ObjectUtil.isEmpty(subjectClasses)) {
            throw new CustomException("该加课码对应的课程信息不存在");
        }
        subjectClasses = selectById(subjectClasses.getId());
        outputObject.setBean(subjectClasses);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public List<SubjectClasses> querySubjectClassesByObjectId(String... objectId) {
        List<String> objectIdList = Arrays.asList(objectId);
        if (CollectionUtil.isEmpty(objectIdList)) {
            return CollectionUtil.newArrayList();
        }
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), objectIdList);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        List<String> ids = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        if (CollectionUtil.isEmpty(ids)) {
            return CollectionUtil.newArrayList();
        }
        return selectByIds(ids.toArray(new String[ids.size()]));
    }

    @Override
    public void editSubjectClassesPeopleNum(String id, Boolean isAdd) {
        String lockKey = String.format("editSubjectClassesPeopleNum_%s", id);
        RedisLock lock = new RedisLock(lockKey);
        try {
            if (!lock.lock()) {
                // 加锁失败
                throw new CustomException("增减人员失败，当前并发量较大，请稍后再次尝试.");
            }
            LOGGER.info("get lock success, lockKey is {}.", lockKey);
            SubjectClasses subjectClasses = selectById(id);
            if (isAdd) {
                // 新增
                UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), subjectClasses.getPeopleNum() + CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            } else {
                // 减少
                UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
                updateWrapper.eq(CommonConstants.ID, id);
                updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), subjectClasses.getPeopleNum() - CommonNumConstants.NUM_ONE);
                update(updateWrapper);
            }
            refreshCache(id);
            LOGGER.info("editSubjectClassesPeopleNum is success.");
        } catch (Exception ee) {
            LOGGER.warn("editSubjectClassesPeopleNum error, because {}", ee);
            if (ee instanceof CustomException) {
                throw new CustomException(ee.getMessage());
            }
            throw new RuntimeException(ee.getMessage());
        } finally {
            lock.unlock();
        }
    }

    public void changeEnabled(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String enabled = map.get("enabled").toString();
        String subjectClassesId = map.get("id").toString();

        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subjectClassesId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getEnabled), enabled);
        update(updateWrapper);
        refreshCache(subjectClassesId);
    }

    public void changeQuit(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String quit = map.get("quit").toString();
        String subjectClassesId = map.get("id").toString();

        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subjectClassesId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getQuit), quit);
        update(updateWrapper);
        refreshCache(subjectClassesId);
    }

    @Override
    public void updatePeopleNum(String subClassLinkId, Integer count) {
        UpdateWrapper<SubjectClasses> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, subClassLinkId);
        updateWrapper.set(MybatisPlusUtil.toColumns(SubjectClasses::getPeopleNum), count);
        update(updateWrapper);
    }

    @Override
    public void queryTeacherMessage(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String subClassLinkId = map.get("subClassLinkId").toString();
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(CommonConstants.ID, subClassLinkId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        iAuthUserService.setDataMation(subjectClassesList, SubjectClasses::getCreateId);
        outputObject.setBean(subjectClassesList);
        outputObject.settotal(subjectClassesList.size());
    }

    @Override
    public SubjectClasses getSubjectClassesByObjectIdAndClassesId(String objectId, String classesId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), objectId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classesId);
        return getOne(queryWrapper);
    }

    @Override
    public void querySubjectClassesInfo(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 科目与班级的关系id
        String subjectId = selectById(id).getObjectId();
        String classId = selectById(id).getClassesId();

        Map<String, Object> resultMap = new HashMap<>();
        // 获取加课人数
        Long joinNum = subjectClassesStuService.queryClassStuNum(id);
        resultMap.put("joinNum", joinNum);
        // 资料个数
        Long dataNum = datumService.queryClassDataNum(subjectId);
        resultMap.put("dataNum", dataNum);
        // 公告数
        Long announcementNum = announcementService.queryClassNoticeNum(id);
        resultMap.put("announcementNum", announcementNum);
        // 话题发帖数
        Long topicNum = topicService.queryClassTopicNum(id);
        resultMap.put("topicNum", topicNum);
        // 话题参与人数
        Long topicJoinNum = topicCommentService.queryClassTopicJoinNum(id);
        resultMap.put("topicJoinNum", topicJoinNum);
        // 话题参与人次--评论总数
        Long topicJoinPersonNum = topicCommentService.queryClassTopicJoinPersonNum(id);
        resultMap.put("topicJoinPersonNum", topicJoinPersonNum);
        // 作业数
        Long assignmentNum = assignmentService.queryClassAssignmentNum(id, null);
        resultMap.put("assignmentNum", assignmentNum);
        // 作业参数人数
        Long assignmentJoinNum = assignmentSubService.queryClassAssignmentJoinNum(id);
        resultMap.put("assignmentJoinNum", assignmentJoinNum);
        // 测试数量
        Long testNum = examService.queryClassExamSurveyDirectoryNum(classId);
        resultMap.put("testNum", testNum);
        // 测试参与人次
        Long testJoinNum = examDirectoryAnService.queryClassExamSurveyAnswerNum(classId);
        resultMap.put("testJoinNum", testJoinNum);
        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStudentAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 科目与班级的关系id
        SubjectClasses subjectClasses = selectById(id);
        String subjectId = subjectClasses.getObjectId();
        String classesId = subjectClasses.getClassesId();

        // 查询班级学生信息
        List<Map<String, Object>> studentList = subjectClassesStuService.queryClassStuIds(id);
        // 获取章节数据
        List<Chapter> chapterList = chapterService.queryChaptersBySubjectId(subjectId);

        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> bean = new ArrayList<>();
        List<Map<String, Object>> beans = new ArrayList<>();

        List<String> chapterIds = chapterList.stream().map(Chapter::getId).collect(Collectors.toList());

        // 获取科目班级下的话题数量
        Long topicNum = topicService.queryClassTopicNum(id);
        // 获取考勤数量
        Long checkWorkNum = checkworkService.queryCheckWorkNum(id);
        // 获取测试数量--按班级查
        Long testNum = examService.queryClassExamSurveyDirectoryNum(classesId);
        // 获取资料数--按章节分组
        Map<String, Long> dataNumMap = datumService.queryDatumBySubjectIdAndChapterIds(subjectId,chapterIds,null);
        // 获取互动课件数 ---按章节分组
        Map<String, Long> coursewareNumMap = coursewareService.queryCoursewareBySubjectIdAndChapterIds(subjectId,chapterIds);
        // 获取作业数---按章节分组
        Map<String, Long> assignmentNumMap = assignmentService.queryAssignmentBySubjectClassesIdAndChapterIds(id,chapterIds);


        // 与学生有关的数据
        List<String> stuIds = new ArrayList<>();
        List<String> stuNumbers = new ArrayList<>();
        for (Map<String, Object> student : studentList) {
            stuIds.add(student.get("id").toString());
            stuNumbers.add(student.get("studentNumber").toString());
        }
        // 1.获取学生上传的资料数--按stuId分组
        Map<String, Long> stuDataNumMap = datumService.queryDatumBySubjectIdAndChapterIds(subjectId,chapterIds,stuIds);
        // 2.获取学生学习的互动课件数--按stuId分组
        Map<String, Long> stuCoursewareNumMap = coursewareService.queryStuCourBySubIdAndChapIdsAndStuIds(subjectId,chapterIds,stuIds);
        // 3.获取学生提交的作业数--按stuId分组
        Map<String, Long> stuAssignmentNumMap = assignmentService.queryStuAssignNumBySubClassesId(id,chapterIds,stuIds);
        // 4.获取学生考勤次数---按stuId分组
        Map<String, Long> stuCheckWorkNumMap = checkworkService.queryStuCheckWorkNumBySubClassesId(id,stuIds);
        // 5.获取学生的奖励星星数
        Map<String, String> stuStarNumMap = subjectClassesStuService.queryStuStarNumBySubClassesId(id,stuNumbers);
        // 6.获取学生的弹幕数量
        Map<String, Long> stuTopicCommentNumMap = topicService.queryStuCommentNumBySubClassesId(id,stuIds);
        // 7.获取学生参与的测试数量
        Map<String, Long> stuTestNumMap = examDirectoryAnService.queryClassExamSurveyAnswerNumByStuIds(classesId,stuIds);

        // 总数
        Map<String, Long> totalData = new HashMap<>();
        // 初始化
        totalData.put("coursewareNum", 0L);
        totalData.put("stuCoursewareNum", 0L);
        totalData.put("dataNum", 0L);
        totalData.put("stuDataNum", 0L);
        totalData.put("assignmentNum", 0L);
        totalData.put("stuAssignmentNum", 0L);
        totalData.put("checkWorkNum", checkWorkNum);
        totalData.put("stuCheckWorkNum", 0L);
        totalData.put("testNum", testNum);
        totalData.put("stuTestNum", 0L);
        totalData.put("topicNum", topicNum);
        // 1-n 章
        for(Chapter chapter : chapterList){
            String name = chapter.getName();
            for (Map<String, Object> student : studentList){
                String stuId = student.get("id").toString();
                // 互动课件
                student.put("coursewareNum",coursewareNumMap.getOrDefault(chapter.getId(),0L));
                student.put("stuCoursewareNum",stuCoursewareNumMap.getOrDefault(stuId,0L));
                totalData.put("coursewareNum",totalData.get("coursewareNum")+coursewareNumMap.getOrDefault(chapter.getId(),0L));
                totalData.put("stuCoursewareNum",totalData.get("stuCoursewareNum")+stuCoursewareNumMap.getOrDefault(stuId,0L));
                double  courseRate = getRate(stuCoursewareNumMap.getOrDefault(stuId,0L),coursewareNumMap.getOrDefault(chapter.getId(),0L));

                // 资料
                student.put("dataNum",dataNumMap.getOrDefault(chapter.getId(),0L));
                student.put("stuDataNum",stuDataNumMap.getOrDefault(stuId,0L));
                totalData.put("dataNum",totalData.get("dataNum") + dataNumMap.getOrDefault(chapter.getId(),0L));
                totalData.put("stuDataNum",totalData.get("stuDataNum") + stuDataNumMap.getOrDefault(stuId,0L));
                double  dataRate = getRate(stuDataNumMap.getOrDefault(stuId,0L),dataNumMap.getOrDefault(chapter.getId(),0L));
                // 作业
                student.put("assignmentNum",assignmentNumMap.getOrDefault(chapter.getId(),0L));
                student.put("stuAssignmentNum",stuAssignmentNumMap.getOrDefault(stuId,0L));
                totalData.put("assignmentNum",totalData.get("assignmentNum") + assignmentNumMap.getOrDefault(chapter.getId(),0L));
                totalData.put("stuAssignmentNum",totalData.get("stuAssignmentNum") + stuAssignmentNumMap.getOrDefault(stuId,0L));
                double  assignmentRate = getRate(stuAssignmentNumMap.getOrDefault(stuId,0L),assignmentNumMap.getOrDefault(chapter.getId(),0L));
                // 测试
                student.put("testNum",testNum);
                student.put("stuTestNum",stuTestNumMap.getOrDefault(stuId,0L));
                totalData.put("stuTestNum",totalData.get("stuTestNum") + stuTestNumMap.getOrDefault(stuId,0L));
                double  testRate = getRate(stuTestNumMap.getOrDefault(stuId,0L),testNum);
                // 考勤
                student.put("checkWorkNum",checkWorkNum);
                student.put("stuCheckWorkNum",stuCheckWorkNumMap.getOrDefault(stuId,0L));
                totalData.put("stuCheckWorkNum",totalData.get("stuCheckWorkNum") + stuCheckWorkNumMap.getOrDefault(stuId,0L));
                double  checkWorkRate = getRate(stuCheckWorkNumMap.getOrDefault(stuId,0L),checkWorkNum);
                // 话题数
                student.put("topicNum",topicNum);
                // 表现奖励
                student.put("rewardNum",stuStarNumMap.getOrDefault(stuId,"0"));
                // 发弹幕数
                student.put("stuTopicCommentNum",stuTopicCommentNumMap.getOrDefault(stuId,0L));
                // 整体完成率
                double rate = (courseRate+assignmentRate+testRate+checkWorkRate+dataRate)/5;
                // 保留三位
                student.put("rate",String.format("%.3f",rate*100)+'%');
                bean.add(student);
            }
            resultMap.put("name",name);
            resultMap.put("studentAnalysis", bean);
            beans.add(resultMap);
            resultMap = new HashMap<>();
        }

        // 全部数据
        bean = new ArrayList<>();
        for (Map<String, Object> student : studentList) {
            String stuId = student.get("id").toString();
            student.putAll(totalData);
            // 表现奖励
            student.put("rewardNum",stuStarNumMap.getOrDefault(stuId,"0"));
            // 发弹幕数
            student.put("stuTopicCommentNum",stuTopicCommentNumMap.getOrDefault(stuId,0L));

            double courseRate = getRate(totalData.get("stuCoursewareNum"),totalData.get("coursewareNum"));
            double assignmentRate = getRate(totalData.get("stuAssignmentNum"),totalData.get("assignmentNum"));
            double testRate = getRate(totalData.get("stuTestNum"),totalData.get("testNum"));
            double checkWorkRate = getRate(totalData.get("stuCheckWorkNum"),totalData.get("checkWorkNum"));
            double dataRate = getRate(totalData.get("stuDataNum"),totalData.get("dataNum"));
            // 整体完成率
            double rate = (courseRate+assignmentRate+testRate+checkWorkRate+dataRate)/5;
            student.put("rate",String.format("%.3f",rate*100)+'%');
            bean.add(student);
        }
        resultMap.put("name","全部");
        resultMap.put("studentAnalysis", bean);
        beans.add(resultMap);

        outputObject.setBeans(beans);
        outputObject.settotal(beans.size());
    }

    private double getRate(Long num, Long totalNum) {
        if (totalNum == 0) {
            return 0.0;
        }
        return (double) num / totalNum * 100;
    }

    @Override
    public void deleteBySubjectId(String subjectId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        if (CollectionUtil.isEmpty(subjectClassesList)) {
            return;
        }
        List<String> ids = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        // 删除班级学生关联表
        subjectClassesStuService.deleteBySubClassLinkId(ids);
        // 删除班级科目关联表
        remove(queryWrapper);
    }

    @Override
    public Integer queryStuNumBySubjectId(String subjectId, String classId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId)
            .eq(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classId);
        SubjectClasses one = getOne(queryWrapper);
        if (ObjectUtil.isEmpty(one)) {
            return null;
        }
        return one.getPeopleNum();
    }

    @Override
    public List<SubjectClasses> selectIdBySubJectId(String subjectId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        return subjectClassesList;
    }

    @Override
    public List<SubjectClasses> selectIdByClassId(String id1) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), id1);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        return subjectClassesList;
    }

    @Override
    public List<SubjectClasses> getSubjectClassesByObjectIdAndClassesIds(String subjectId, List<String> classIds) {
        if (CollectionUtil.isEmpty(classIds)) {
            return new ArrayList<>();
        }
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId)
            .in(MybatisPlusUtil.toColumns(SubjectClasses::getClassesId), classIds);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        return subjectClassesList;
    }
}
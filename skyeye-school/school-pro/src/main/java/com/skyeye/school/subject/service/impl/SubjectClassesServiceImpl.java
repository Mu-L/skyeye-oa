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
import com.skyeye.school.grade.service.ClassesService;
import com.skyeye.school.measurement.service.MeasurementService;
import com.skyeye.school.measurement.service.MeasurementSubService;
import com.skyeye.school.score.service.ScoreTypeService;
import com.skyeye.school.semester.service.SemesterService;
import com.skyeye.school.subject.dao.SubjectClassesDao;
import com.skyeye.school.subject.entity.Subject;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
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
    private ScoreTypeService scoreTypeService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private DatumService datumService;

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
    private MeasurementService measurementService;

    @Autowired
    private MeasurementSubService measurementSubService;

    @Autowired
    private ChapterService chapterService;

    @Autowired
    private CoursewareService coursewareService;

    @Autowired
    private CheckworkService checkworkService;

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
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), inputObject.getParams().get("objectId").toString());
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
    }

    @Override
    public void createPostpose(SubjectClasses entity, String userId) {
        scoreTypeService.createDeFaultInfo(entity, userId);
    }

    @Override
    public void deletePostpose(SubjectClasses entity) {
        FileUtil.deleteFile(tPath.replace("images", StrUtil.EMPTY) + entity.getSourceCode());
        // 删除班级学生关联表
        subjectClassesStuService.deleteBySubClassLinkId(Arrays.asList(entity.getId()));
    }

    @Override
    public SubjectClasses selectById(String id) {
        SubjectClasses subjectClasses = super.selectById(id);
        classesService.setDataMation(subjectClasses, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClasses, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClasses, SubjectClasses::getSemesterId);
        refreshCache(subjectClasses.getCreateId());//刷新缓存
        return subjectClasses;
    }

    @Override
    public List<SubjectClasses> selectByIds(String... ids) {
        List<SubjectClasses> subjectClassesList = super.selectByIds(ids);
        classesService.setDataMation(subjectClassesList, SubjectClasses::getClassesId);
        subjectService.setDataMation(subjectClassesList, SubjectClasses::getObjectId);
        semesterService.setDataMation(subjectClassesList, SubjectClasses::getSemesterId);
        refreshCache(subjectClassesList.get(0).getId());//刷新缓存
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
        Map<String, Object> resultMap = new HashMap<>();
        // 获取加课人数
        Long joinNum = subjectClassesStuService.queruClassStuNum(id);
        resultMap.put("joinNum", joinNum);
        // 资料个数
//        Long dataNum = datumService.queryClassDataNum(id, null);
//        resultMap.put("dataNum", dataNum);
        // 公告数
        Long noticeNum = announcementService.queryClassNoticeNum(id);
        resultMap.put("noticeNum", noticeNum);
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
//        Long testNum = measurementService.queryClassMeasurementNum(id, null);
//        resultMap.put("testNum", testNum);
        // 测试参与人数
        Long testJoinNum = measurementSubService.queryClassMeasurementJoinNum(id);
        resultMap.put("testJoinNum", testJoinNum);

        outputObject.setBean(resultMap);
        outputObject.settotal(CommonNumConstants.NUM_ONE);
    }

    @Override
    public void queryStudentAnalysis(InputObject inputObject, OutputObject outputObject) {
        Map<String, Object> map = inputObject.getParams();
        String id = map.get("id").toString(); // 科目与班级的关系id
        SubjectClasses subjectClasses = selectById(id);
        // 查询班级学生信息
        List<Map<String, Object>> studentList = subjectClassesStuService.queryClassStuIds(id);
        // 获取章节数据
        List<Chapter> chapterList = chapterService.queryChaptersBySubjectId(subjectClasses.getObjectId());
        // 获取科目班级下的话题数量
        Long topicNum = topicService.queryClassTopicNum(id);
        // 获取考勤数量
        Long checkWorkNum = checkworkService.queryCheckWorkNum(id);

        Map<String, Object> tempMap = new HashMap<>();
        Map<String, Object> resultMap = new HashMap<>();
        List<Map<String, Object>> bean = new ArrayList<>();
        List<Map<String, Object>> beans = new ArrayList<>();
        for (Chapter chapter : chapterList) {
            String name = "chapter" + chapter.getSection();
            // 获取科目班级的章节下的资料数
//            Long dataNum = datumService.queryClassDataNum(id, chapter.getId());
            // 获取科目班级的章节下的测试数
//            Long testNum = measurementService.queryClassMeasurementNum(id, chapter.getId());
            // 获取科目班级的章节下的作业数
            Long assignmentNum = assignmentService.queryClassAssignmentNum(id, chapter.getId());
            // 获取科目班级下的章节互动课件数量
            Long coursewareNum = coursewareService.queryClassCoursewareNum(id, chapter.getId());
            for (Map<String, Object> student : studentList) {
                String stuId = student.get("id").toString();
                String studentNumber = student.get("studentNumber").toString();
                String stuName = "student" + studentNumber;
                // 获取学生上传的资料数
//                Long stuDataNum = datumService.queryStuDataNum(id, stuId, chapter.getId());
                // 获取学生的某章节测试数
//                Long stuTestNum = measurementService.queryStuMeasurementNum(id, stuId, chapter.getId());
                // 获取学生的某章节作业数
                Long stuAssignmentNum = assignmentService.queryStuAssignmentNum(id, stuId, chapter.getId());
                // 获取学生的某章节互动课件数量
                Long stuCoursewareNum = coursewareService.queryStuCoursewareNum(id, stuId, chapter.getId());
                // 获取学生的考勤数量
                Long stuCheckWorkNum = checkworkService.queryStuCheckWorkNum(id, stuId);
                // 获取学生的弹幕数量
                Long stuTopicCommentNum = topicService.queryStuTopicCommentNum(id, stuId);
                // 获取奖励星星数量
                Long stuStarNum = subjectClassesStuService.queryStuStarNum(id, studentNumber);
                // 获取学生的发话题数
                Long stuTopicNum = topicService.queryStuTopicNum(id, stuId);
                // 互动课件上传率
                double courseRate = getRate(stuCoursewareNum, coursewareNum);
                // 考勤率
                double checkWorkRate = getRate(stuCheckWorkNum, checkWorkNum);
                // 测试完成率
//                double testRate = getRate(stuTestNum, testNum);
                // 作业完成率
                double assignmentRate = getRate(stuAssignmentNum, assignmentNum);
                // 资料上传率
//                double dataRate = getRate(stuDataNum, dataNum);
                // 整体完成率
                double overallRate = (courseRate + checkWorkRate  + assignmentRate ) / CommonNumConstants.NUM_FIVE;
//                student.put("stuDataNum", stuDataNum);
//                student.put("dataNum", dataNum);
//                student.put("stuTestNum", stuTestNum);
//                student.put("testNum", testNum);
                student.put("stuAssignmentNum", stuAssignmentNum);
                student.put("assignmentNum", assignmentNum);
                student.put("stuCoursewareNum", stuCoursewareNum);
                student.put("coursewareNum", coursewareNum);
                student.put("stuCheckWorkNum", stuCheckWorkNum);
                student.put("checkWorkNum", checkWorkNum);
                student.put("stuTopicNum", stuTopicNum);
                student.put("topicNum", topicNum);
                student.put("stuTopicCommentNum", stuTopicCommentNum);
                student.put("stuStarNum", stuStarNum);
                student.put("overallRate", overallRate);
                tempMap.put(stuName, student);
                bean.add(tempMap);
            }
            resultMap.put(name, bean);
            beans.add(resultMap);
            resultMap = new HashMap<>();
        }
        // 全部数据
        if (chapterList.size() > CommonNumConstants.NUM_ONE) {
            bean = new ArrayList<>();
            // 获取科目班级的互动课件数量
            Long coursewareNum = coursewareService.queryClassCoursewareNum(id, null);
            // 获取科目班级的作业数量
            Long assignmentNum = assignmentService.queryClassAssignmentNum(id, null);
            // 获取科目班级的资料
//            Long dataNum = datumService.queryClassDataNum(id, null);
            // 获取科目班级的测试数量
//            Long testNum = measurementService.queryClassMeasurementNum(id, null);
            for (Map<String, Object> student : studentList) {
                String stuId = student.get("id").toString();
                String studentNumber = student.get("studentNumber").toString();
                String stuName = "student" + studentNumber;
                // 获取学生互动课件数量
                Long stuCoursewareNum = coursewareService.queryStuCoursewareNum(id, stuId, null);
                // 获取学生作业数量
                Long stuAssignmentNum = assignmentService.queryStuAssignmentNum(id, stuId, null);
                // 获取学生资料数量
//                Long stuDataNum = datumService.queryStuDataNum(id, stuId, null);
                // 获取学生测试数量
//                Long stuTestNum = measurementService.queryStuMeasurementNum(id, stuId, null);
                // 获取考勤数量
                Long stuCheckWorkNum = checkworkService.queryStuCheckWorkNum(id, stuId);
                // 获取奖励星星数量
                Long stuStarNum = subjectClassesStuService.queryStuStarNum(id, studentNumber);
                // 获取学生的弹幕数量
                Long stuTopicCommentNum = topicService.queryStuTopicCommentNum(id, stuId);
                // 获取学生的发话题数
                Long stuTopicNum = topicService.queryStuTopicNum(id, stuId);
                student.put("stuCoursewareNum", stuCoursewareNum);
                student.put("coursewareNum", coursewareNum);
                student.put("stuAssignmentNum", stuAssignmentNum);
                student.put("assignmentNum", assignmentNum);
//                student.put("stuDataNum", stuDataNum);
//                student.put("dataNum", dataNum);
//                student.put("stuTestNum", stuTestNum);
//                student.put("testNum", testNum);
                student.put("stuCheckWorkNum", stuCheckWorkNum);
                student.put("checkWorkNum", checkWorkNum);
                student.put("stuTopicNum", stuTopicNum);
                student.put("topicNum", topicNum);
                student.put("stuStarNum", stuStarNum);
                student.put("stuTopicCommentNum", stuTopicCommentNum);
                double courseRate = getRate(stuCoursewareNum, coursewareNum);
                double checkWorkRate = getRate(stuCheckWorkNum, checkWorkNum);
//                double testRate = getRate(stuTestNum, testNum);
                double assignmentRate = getRate(stuAssignmentNum, assignmentNum);
//                double dataRate = getRate(stuDataNum, dataNum);
                double overallRate = (courseRate + checkWorkRate  + assignmentRate ) / CommonNumConstants.NUM_FIVE;
                student.put("overallRate", overallRate);
                tempMap.put(stuName, student);
                bean.add(tempMap);
            }
            resultMap.put("all", bean);
            beans.add(resultMap);
        }
        if (CollectionUtil.isNotEmpty(beans)) {
            resultMap.put("all", bean);
            beans.add(resultMap);
        }
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
    public Long queryStuNumBySubjectId(String subjectId) {
        QueryWrapper<SubjectClasses> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(SubjectClasses::getObjectId), subjectId);
        List<SubjectClasses> subjectClassesList = list(queryWrapper);
        if (CollectionUtil.isEmpty(subjectClassesList)) {
            return 0L;
        }
        List<String> ids = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        return null;
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
}
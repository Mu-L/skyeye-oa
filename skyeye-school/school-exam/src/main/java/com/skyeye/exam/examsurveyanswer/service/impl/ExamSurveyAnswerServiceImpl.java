package com.skyeye.exam.examsurveyanswer.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.google.common.base.Joiner;
import com.skyeye.annotation.service.SkyeyeService;
import com.skyeye.base.business.service.impl.SkyeyeBusinessServiceImpl;
import com.skyeye.common.client.ExecuteFeignClient;
import com.skyeye.common.constans.CommonCharConstants;
import com.skyeye.common.constans.CommonConstants;
import com.skyeye.common.constans.CommonNumConstants;
import com.skyeye.common.entity.search.CommonPageInfo;
import com.skyeye.common.object.InputObject;
import com.skyeye.common.object.OutputObject;
import com.skyeye.common.util.DateUtil;
import com.skyeye.common.util.mybatisplus.MybatisPlusUtil;
import com.skyeye.eve.entity.School;
import com.skyeye.eve.service.SchoolService;
import com.skyeye.exam.examananswer.service.ExamAnAnswerService;
import com.skyeye.exam.examancheckbox.service.ExamAnCheckboxService;
import com.skyeye.exam.examanchencheckbox.service.ExamAnChenCheckboxService;
import com.skyeye.exam.examanchenfbk.service.ExamAnChenFbkService;
import com.skyeye.exam.examanchenradio.service.ExamAnChenRadioService;
import com.skyeye.exam.examanchenscore.service.ExamAnChenScoreService;
import com.skyeye.exam.examancompchenradio.service.ExamAnCompChenRadioService;
import com.skyeye.exam.examandfillblank.service.ExamAnDfilllankService;
import com.skyeye.exam.examanenumqu.service.ExamAnEnumquService;
import com.skyeye.exam.examanfillblank.service.ExamAnFillblankService;
import com.skyeye.exam.examanorder.service.ExamAnOrderService;
import com.skyeye.exam.examanradio.service.ExamAnRadioService;
import com.skyeye.exam.examanscore.service.ExamAnScoreService;
import com.skyeye.exam.examanyesno.service.ExamAnYesnoService;
import com.skyeye.exam.examsurveyanswer.dao.ExamSurveyAnswerDao;
import com.skyeye.exam.examsurveyanswer.entity.ExamSurveyAnswer;
import com.skyeye.exam.examsurveyanswer.service.ExamSurveyAnswerService;
import com.skyeye.exam.examsurveydirectory.entity.ExamSurveyDirectory;
import com.skyeye.exam.examsurveydirectory.service.ExamSurveyDirectoryService;
import com.skyeye.exam.examsurveymarkexam.entity.ExamSurveyMarkExam;
import com.skyeye.exam.examsurveymarkexam.service.ExamSurveyMarkExamService;
import com.skyeye.exam.examsurveyquanswer.service.ExamSurveyQuAnswerService;
import com.skyeye.exception.CustomException;
import com.skyeye.rest.wall.certification.rest.ICertificationRest;
import com.skyeye.rest.wall.user.service.IUserService;
import com.skyeye.school.common.entity.UserOrStudent;
import com.skyeye.school.common.service.SchoolCommonService;
import com.skyeye.school.exam.service.ExamDirectoryAnService;
import com.skyeye.school.faculty.entity.Faculty;
import com.skyeye.school.faculty.service.FacultyService;
import com.skyeye.school.major.entity.Major;
import com.skyeye.school.major.service.MajorService;
import com.skyeye.school.score.classenum.NumberCodeEnum;
import com.skyeye.school.score.entity.ScoreTypeChild;
import com.skyeye.school.score.service.ScoreService;
import com.skyeye.school.score.service.ScoreTypeChildService;
import com.skyeye.school.subject.entity.SubjectClasses;
import com.skyeye.school.subject.entity.SubjectClassesStu;
import com.skyeye.school.subject.service.SubjectClassesService;
import com.skyeye.school.subject.service.SubjectClassesStuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @ClassName: ExamSurveyAnswerServiceImpl
 * @Description: 试卷回答信息表管理服务层
 * @author: skyeye云系列--lyj
 * @date: 2024/7/19 11:01
 * @Copyright: 2024 https://gitee.com/doc_wei01/skyeye Inc. All rights reserved.
 * 注意：本内容仅限购买后使用.禁止私自外泄以及用于其他的商业目的
 */
@Service
@SkyeyeService(name = "试卷回答信息表管理", groupName = "试卷回答信息表管理")
public class ExamSurveyAnswerServiceImpl extends SkyeyeBusinessServiceImpl<ExamSurveyAnswerDao, ExamSurveyAnswer> implements ExamSurveyAnswerService, ExamDirectoryAnService {

    @Autowired
    private ExamAnRadioService examAnRadioService;

    @Autowired
    private ExamAnScoreService examAnScoreService;

    @Autowired
    private ExamAnYesnoService examAnYesnoService;

    @Autowired
    private ExamAnAnswerService examAnAnswerService;

    @Autowired
    private ExamAnCheckboxService examAnCheckboxService;

    @Autowired
    private ExamAnChenCheckboxService examAnChenCheckboxService;

    @Autowired
    private ExamAnChenFbkService examAnChenFbkService;

    @Autowired
    private ExamAnChenRadioService examAnChenRadioService;

    @Autowired
    private ExamAnChenScoreService examAnChenScoreService;

    @Autowired
    private ExamAnCompChenRadioService examAnCompChenRadioService;

    @Autowired
    private ExamAnDfilllankService examAnDfilllankService;

    @Autowired
    private ExamAnEnumquService examAnEnumquService;

    @Autowired
    private ExamAnFillblankService examAnFillblankService;

    @Autowired
    private ExamAnOrderService examAnOrderService;

    @Autowired
    private ExamSurveyQuAnswerService examSurveyQuAnswerService;

    @Autowired
    private ICertificationRest iCertificationRest;

    @Autowired
    private SchoolService schoolService;

    @Autowired
    private ExamSurveyDirectoryService examSurveyDirectoryService;

    @Autowired
    private MajorService majorService;

    @Autowired
    private FacultyService facultyService;

    @Autowired
    private SubjectClassesService subjectClassesService;

    @Autowired
    private ExamSurveyMarkExamService examSurveyMarkExamService;

    @Autowired
    private SubjectClassesStuService subjectClassesStuService;

    @Autowired
    private SchoolCommonService schoolCommonService;

    @Override
    protected void createPrepose(ExamSurveyAnswer entity) {
        String id = InputObject.getLogParamsStatic().get("id").toString();
        String surveyId = entity.getSurveyId();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), id);
        if (CollectionUtil.isNotEmpty(list(queryWrapper))) {
            throw new CustomException("该试卷已回答,请勿重复回答");
        }
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        if (StrUtil.isNotEmpty(bgAnDate) && StrUtil.isNotEmpty(endAnDate)) {
            boolean compare = DateUtil.compare(bgAnDate, endAnDate);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
    }

    @Autowired
    private ScoreTypeChildService scoreTypeChildService;

    @Override
    protected void createPostpose(ExamSurveyAnswer entity, String userId) {
        String surveyId = entity.getSurveyId();
        ExamSurveyDirectory surveyDirectory = examSurveyDirectoryService.selectById(surveyId);
        String subjectId = surveyDirectory.getSubjectId();
        String classId = surveyDirectory.getClassId();
        UpdateWrapper<ExamSurveyAnswer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, entity.getId());
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSubjectId), subjectId);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getClassId), classId);
        update(updateWrapper);

        List<String> classIds = Arrays.asList(classId.split(","));
        List<SubjectClasses> subjectClassesList = subjectClassesService.selectIdBySubAndClassIds(subjectId, classIds);
        if (ObjectUtil.isEmpty(subjectClassesList)) {
            throw new CustomException("没有科目对应的班级");
        }
        String testKey = NumberCodeEnum.TEST.getKey();
        List<String> subjectClassesIds = subjectClassesList.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        List<ScoreTypeChild> scoreTypeChildren = scoreTypeChildService.selectIds(subjectId, subjectClassesIds, testKey);
        if (CollectionUtil.isEmpty(scoreTypeChildren)) {
            throw new CustomException("没有科目对应的班级");
        }
        Map<String, List<ScoreTypeChild>> collect = scoreTypeChildren.stream().collect(Collectors.groupingBy(ScoreTypeChild::getSubClassLinkId));
        List<ScoreTypeChild> list = new ArrayList<>();
        for (String subjectClassesId : subjectClassesIds) {
            ScoreTypeChild scoreTypeChild = new ScoreTypeChild();
            scoreTypeChild.setSubjectId(subjectId);
            scoreTypeChild.setSubClassLinkId(subjectClassesId);
            if (CollectionUtil.isEmpty(collect.get(subjectClassesId)) || ObjectUtil.isEmpty(collect.get(subjectClassesId).get(CommonNumConstants.NUM_ZERO))) {
                throw new CustomException("没有科目对应的班级");
            }

            scoreTypeChild.setParentId(collect.get(subjectClassesId).get(CommonNumConstants.NUM_ZERO).getId());
            scoreTypeChild.setName(surveyDirectory.getSurveyName());
            scoreTypeChild.setNameLinkId(entity.getId());
            scoreTypeChild.setNameLinkKey(getServiceClassName());
            scoreTypeChild.setProportion(CommonNumConstants.NUM_ZERO.toString());
            list.add(scoreTypeChild);
        }
        scoreTypeChildService.createEntity(list, userId);
    }

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ScoreService scorePartService;

    @Override
    protected void updatePrepose(ExamSurveyAnswer entity) {
        String bgAnDate = entity.getBgAnDate();
        String endAnDate = entity.getEndAnDate();
        // 判断是否结束
        String markStartTime = entity.getMarkStartTime();
        String markEndTime = entity.getMarkEndTime();
        if (StrUtil.isNotEmpty(markStartTime) && StrUtil.isNotEmpty(markEndTime)) {
            boolean compare = DateUtil.compare(markStartTime, markEndTime);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
        }
        if (StrUtil.isNotEmpty(bgAnDate) && StrUtil.isNotEmpty(endAnDate)) {
            boolean compare = DateUtil.compare(bgAnDate, endAnDate);
            if (!compare) {
                throw new CustomException("开始时间不能大于结束时间");
            }
            String distanceHMS = getDistanceHMS(bgAnDate, endAnDate);
            entity.setTotalTime(distanceHMS);
            String surveyId = entity.getSurveyId();
            String id = entity.getId();
            Long size = examAnRadioService.selectRadioBySurveyId(surveyId, id);
            Long size1 = examAnScoreService.selectBySurveyId(surveyId, id);
            Long size2 = examAnYesnoService.selectBySurveyId(surveyId, id);
            Long size3 = examAnAnswerService.selectBySurveyId(surveyId, id);
            Long size4 = examAnCheckboxService.slectBySurveyId(surveyId, id);
            Long size5 = examAnChenCheckboxService.selectBySurveyId(surveyId, id);
            Long size6 = examAnChenFbkService.selectBySurveyId(surveyId, id);
            Long size7 = examAnChenRadioService.selectBySurveyId(surveyId, id);
            Long size8 = examAnChenScoreService.selectBySurveyId(surveyId, id);
            Long size9 = examAnCompChenRadioService.selectBySurveyId(surveyId, id);
            Long size10 = examAnDfilllankService.selectBySurveyId(surveyId, id);
            Long size11 = examAnEnumquService.selectBySurveyId(surveyId, id);
            Long size12 = examAnFillblankService.selectBySurveyId(surveyId, id);
            Long size13 = examAnOrderService.selectBySurveyId(surveyId, id);
            Long total = size + size1 + size2 + size3 + size4 + size5 + size6 + size7 + size8 + size9 + size10 + size11 + size12 + size13;
            entity.setCompleteNum(total.intValue());
            if (total.intValue() == entity.getQuNum()) {
                entity.setIsComplete(CommonNumConstants.NUM_ONE);
            }
            if (entity.getHandleState().equals(CommonNumConstants.NUM_ONE) && entity.getState().equals(CommonNumConstants.NUM_TWO)) {
                float fraction = examSurveyQuAnswerService.selectFractionBySurveyId(entity.getSurveyId(), entity.getId());
                entity.setMarkFraction(fraction);
                ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(surveyId);
                //已经批阅的学生人数加一
                if (examSurveyDirectory.getReadNum() == null) {
                    examSurveyDirectory.setReadNum(CommonNumConstants.NUM_ONE);
                }
                examSurveyDirectory.setReadNum(examSurveyDirectory.getReadNum() + CommonNumConstants.NUM_ONE);
                if (examSurveyDirectory.getAllNumber().equals(examSurveyDirectory.getReadNum() + CommonNumConstants.NUM_ONE)) {
                    examSurveyDirectory.setIsMarkState(CommonNumConstants.NUM_ONE);
                }
                examSurveyDirectoryService.updateEntity(examSurveyDirectory, examSurveyDirectory.getCreateId());

                ExamSurveyAnswer examSurveyAnswer = selectById(entity.getId());
                String subjectId = examSurveyAnswer.getSubjectId();
                String classId = examSurveyAnswer.getClassId();
                String[] classIdArray = classId.split(",");
                List<String> classIds = Arrays.asList(classIdArray);
                List<SubjectClasses> subjectClassesByObjectIdAndClassesIds = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(subjectId, classIds);
                List<Map<String, Object>> userList = iUserService.queryEntityMationByIds(examSurveyAnswer.getCreateId());
                for (SubjectClasses subjectClassesByObjectIdAndClassesId : subjectClassesByObjectIdAndClassesIds) {
                    scorePartService.updateStudentScore(examSurveyAnswer.getSubjectId(), subjectClassesByObjectIdAndClassesId.getId(),
                        userList.get(CommonNumConstants.NUM_ZERO).get("studentNumber").toString(), entity.getId(), getServiceClassName(),
                        examSurveyDirectory.getSurveyName(), String.valueOf(examSurveyAnswer.getMarkFraction()));
                    refreshCache(id);
                }
            }
        }
    }

    @Override
    protected void updatePostpose(ExamSurveyAnswer entity, String userId) {
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(entity.getSurveyId());
        String subjectId = examSurveyDirectory.getSubjectId();
        String classId = examSurveyDirectory.getClassId();
        String[] classIdArray = classId.split(",");
        List<String> classIdList = Arrays.asList(classIdArray);
        List<SubjectClasses> subjectClassesByObjectIdAndClassesIds = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(subjectId, classIdList);
        List<String> collect = subjectClassesByObjectIdAndClassesIds.stream().map(SubjectClasses::getId).collect(Collectors.toList());
        if (CollectionUtil.isNotEmpty(collect)) {
            // 修改成绩子类型名称ry.getSurveyName());
            scoreTypeChildService.editNames(examSurveyDirectory.getSubjectId(), collect, examSurveyDirectory.getId(), examSurveyDirectory.getSurveyName());
        }
    }

    public static String getDistanceHMS(String bgAnDate, String endAnDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime start = LocalDateTime.parse(bgAnDate, formatter);
        LocalDateTime end = LocalDateTime.parse(endAnDate, formatter);
        Duration duration = Duration.between(start, end);
        long hours = duration.toHours();
        long minutes = duration.toMinutes() % 60;
        long seconds = duration.getSeconds() % 60;
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }


    @Override
    protected void deletePreExecution(ExamSurveyAnswer entity) {
        String surveyId = entity.getSurveyId();
        String createId = entity.getCreateId();
        examAnRadioService.deleteBySurAndCreateId(surveyId, createId);
        examAnScoreService.deleteBySurAndCreateId(surveyId, createId);
        examAnYesnoService.deleteBySurAndCreateId(surveyId, createId);
        examAnAnswerService.deleteBySurAndCreateId(surveyId, createId);
        examAnCheckboxService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenCheckboxService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenFbkService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenRadioService.deleteBySurAndCreateId(surveyId, createId);
        examAnChenScoreService.deleteBySurAndCreateId(surveyId, createId);
        examAnCompChenRadioService.deleteBySurAndCreateId(surveyId, createId);
        examAnDfilllankService.deleteBySurAndCreateId(surveyId, createId);
        examAnEnumquService.deleteBySurAndCreateId(surveyId, createId);
        examAnFillblankService.deleteBySurAndCreateId(surveyId, createId);
        examAnOrderService.deleteBySurAndCreateId(surveyId, createId);
    }

    @Override
    public ExamSurveyAnswer selectById(String id) {
        ExamSurveyAnswer examSurveyAnswer = super.selectById(id);
        String surveyId = examSurveyAnswer.getSurveyId();
        String studentId = examSurveyAnswer.getCreateId();
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectBySurAndStuIds(surveyId, studentId, id);
        examSurveyAnswer.setSurveyMation(examSurveyDirectory);
        String createId = examSurveyAnswer.getCreateId();
        UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(createId);
        if (userOrStudent.getUserOrStudent()) {
            examSurveyAnswer.setUserMation(userOrStudent);
        } else {
            examSurveyAnswer.setTeacherMation(userOrStudent);
        }
        return examSurveyAnswer;
    }

    @Override
    public void queryMySurveyAnswerList(InputObject inputObject, OutputObject outputObject) {
        String createId = inputObject.getLogParams().get("id").toString();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), createId);
        queryWrapper.isNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
            .or()
            .eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), "");
        List<ExamSurveyAnswer> examSurveyAnswerList = list(queryWrapper);
        List<String> collect = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId).collect(Collectors.toList());
        //试卷id及信息
        Map<String, List<ExamSurveyDirectory>> stringListMap = examSurveyDirectoryService.querySurveyListByIds(collect, createId);
        for (ExamSurveyAnswer examSurveyAnswer : examSurveyAnswerList) {
            examSurveyAnswer.setSurveysMation(stringListMap.get(examSurveyAnswer.getSurveyId()));
        }
        outputObject.setBeans(examSurveyAnswerList);
        outputObject.settotal(examSurveyAnswerList.size());
    }

    @Override
    public ExamSurveyAnswer queryWhetherExamIngByStuId(String userId, String id) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), id);
        return getOne(queryWrapper);
    }

    @Override
    public List<ExamSurveyAnswer> querySurveyAnswer(String surveyId, String answerId, String userId) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        queryWrapper.eq(CommonConstants.ID, answerId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        return list(queryWrapper);
    }

    @Override
    public void queryAllSurveyList(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        //老师的账号Id
        String userId = inputObject.getLogParams().get("id").toString();
        //科目Id
        String objectId = commonPageInfo.getObjectId();
        //班级Id
        String holderId = commonPageInfo.getHolderId();
        //作为阅卷人的试卷
        List<ExamSurveyMarkExam> examSurveyMarkExams = examSurveyMarkExamService.selectByUserId(userId);
        List<ExamSurveyDirectory> examSurveyDirectoryList = examSurveyDirectoryService.queryCreatedSurveyListByUserId(userId);
        //作为创建人的所有试卷Id
        List<String> surveyIds1 = examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getId).collect(Collectors.toList());
        //作为阅卷人和创建人的所有试卷Id
        List<String> combinedSurveyIds = Stream.concat(
                examSurveyMarkExams.stream().map(ExamSurveyMarkExam::getSurveyId),
                examSurveyDirectoryList.stream().map(ExamSurveyDirectory::getId)
            )
            .distinct()
            .collect(Collectors.toList());
        //试卷id和对应的试卷信息（有回答过的信息）
        Map<String, List<ExamSurveyDirectory>> stringListMap = examSurveyDirectoryService.querySurveyListByIds(combinedSurveyIds, userId);
        stringListMap.replaceAll((k, v) -> v == null ? new ArrayList<>() : v);
        //老师回答过的答卷
        List<ExamSurveyAnswer> examSurveyAnswerList = selectSurveyIdByteacherId(userId);
        List<String> yesDoSurveyList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(examSurveyAnswerList)) {
            //老师回答过的试卷id
            yesDoSurveyList = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId)
                .collect(Collectors.toList());
        }
        Set<String> yesDoSurveySet = new HashSet<>(yesDoSurveyList);
        stringListMap.values().stream()
            .flatMap(Collection::stream)
            .forEach(directory -> {
                // 判断当前试卷ID是否在老师回答过的集合中
                boolean isAnswered = yesDoSurveySet.contains(directory.getId());
                directory.setIsAnswered(isAnswered);
            });
        Set<String> createdSurveyIdSet = new HashSet<>(surveyIds1);
        for (List<ExamSurveyDirectory> directories : stringListMap.values()) {
            for (ExamSurveyDirectory directory : directories) {
                // 检查当前试卷ID是否在创建者的ID集合中
                boolean isCreated = createdSurveyIdSet.contains(directory.getId());
                directory.setIsCreated(isCreated);
            }
        }
        List<ExamSurveyDirectory> examSurveyDirectories = stringListMap.values().stream()
            .flatMap(Collection::stream)
            .filter(d -> d.getSubjectId() != null && d.getClassId() != null)
            .sorted(Comparator.comparing(ExamSurveyDirectory::getId)) // 固定排序
            .collect(Collectors.toList());
        //所有的试卷信息
        Map<String, Map<String, List<ExamSurveyDirectory>>> resultMap = examSurveyDirectories.stream()
            .filter(d -> d.getSubjectId() != null && d.getClassId() != null) // 过滤空ID
            .collect(
                Collectors.groupingBy(
                    ExamSurveyDirectory::getSubjectId,
                    Collectors.groupingBy(
                        ExamSurveyDirectory::getClassId,
                        Collectors.toList()
                    )
                )
            );
        List<ExamSurveyDirectory> resultList = Optional.ofNullable(resultMap.get(objectId))
            .map(classMap -> classMap.get(holderId))
            .orElse(Collections.emptyList());
        // 内存分页处理
        int pageSize = Math.max(1, commonPageInfo.getLimit());
        int pageNum = Math.max(1, commonPageInfo.getPage());
        int total = resultList.size();
        int fromIndex = (pageNum - 1) * pageSize;

        List<ExamSurveyDirectory> pagedList;
        if (fromIndex >= total) {
            pagedList = Collections.emptyList();
        } else {
            int toIndex = Math.min(fromIndex + pageSize, total);
            pagedList = resultList.subList(fromIndex, toIndex);
        }
        outputObject.settotal(total);
        outputObject.setBeans(pagedList);
    }

    @Override
    public void querySurveyAnswerBySurveyId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        //试卷id
        String surveyId = commonPageInfo.getHolderId();
        //科目Id
        String objectId = commonPageInfo.getObjectId();
        //班级Id
        String companyId = commonPageInfo.getCompanyId();
        SubjectClasses subjectClasses = subjectClassesService.selectIdBySubAndClassId(objectId, companyId);
        //老师创建班级的创建Id
        String createId = subjectClasses.getCreateId();
        List<String> stuNoLists = new ArrayList<>();
        if (StrUtil.isNotEmpty(subjectClasses.getId())) {
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.selectNumBySubClassLinkId(subjectClasses.getId());
            if (CollectionUtil.isNotEmpty(subjectClassesStuList)) {
                stuNoLists = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
            }
        }
        String state = commonPageInfo.getState();
        Integer starts = Integer.valueOf(state);
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId);
        if (CollectionUtil.isEmpty(stuNoLists)) {
            throw new CustomException("当前班级没有学生,没有答卷");
        }
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), starts);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
            .ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), "");
        if (CollectionUtil.isNotEmpty(stuNoLists)) {
            List<String> finalStuNoLists = stuNoLists;
            queryWrapper.and(wrapper ->
                wrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getStudentNumber), finalStuNoLists)
                    .or()
                    .eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), createId)
            );
        } else {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), createId);
        }
        //学生回答的答卷
        List<ExamSurveyAnswer> list = list(queryWrapper);
        UserOrStudent teacherInfo = schoolCommonService.queryUserOrStudent(createId);
        List<String> stuNoList = list.stream().map(ExamSurveyAnswer::getStudentNumber).distinct().collect(Collectors.toList());
        List<Map<String, Object>> userList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(stuNoList)) {
            userList = ExecuteFeignClient.get(() ->
                iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
        }
        List<String> schoolIds = list.stream().map(ExamSurveyAnswer::getSchoolId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<String> surveyIds = list.stream().map(ExamSurveyAnswer::getSurveyId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<String> facultyIds = list.stream().map(ExamSurveyAnswer::getFacultyId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        List<String> majorIds = list.stream().map(ExamSurveyAnswer::getMajorId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, List<School>> schoolMap = schoolIds.isEmpty() ? new HashMap<>() : schoolService.selectByIdList(schoolIds);
        Map<String, ExamSurveyDirectory> surveyMap = surveyIds.isEmpty() ? new HashMap<>() : examSurveyDirectoryService.selectMapBysurveyIds(surveyIds);
        Map<String, List<Faculty>> facultyMap = facultyIds.isEmpty() ? new HashMap<>() : facultyService.selectByIdList(facultyIds);
        Map<String, List<Major>> majorMap = majorIds.isEmpty() ? new HashMap<>() : majorService.selectByIdList(majorIds);
        Map<String, Map<String, Object>> userMap = userList.stream()
            .filter(user -> user.get("studentNumber") != null) // 过滤空学号
            .collect(Collectors.toMap(
                user -> user.get("studentNumber").toString(),
                Function.identity(),
                (oldValue, newValue) -> oldValue
            ));

        for (ExamSurveyAnswer answer : list) {
            if (answer.getCreateId().equals(createId)) {
                answer.setTeacherMation(teacherInfo);
            }
            List<School> schools = schoolMap.getOrDefault(answer.getSchoolId(), Collections.emptyList());
            answer.setSchoolMation(schools.isEmpty() ? null : schools.get(CommonNumConstants.NUM_ZERO));
            answer.setSurveyMation(surveyMap.get(answer.getSurveyId()));
            List<Faculty> faculties = facultyMap.getOrDefault(answer.getFacultyId(), Collections.emptyList());
            answer.setFacultyMation(faculties.isEmpty() ? null : faculties.get(CommonNumConstants.NUM_ZERO));
            List<Major> majors = majorMap.getOrDefault(answer.getMajorId(), Collections.emptyList());
            answer.setMajorMation(majors.isEmpty() ? null : majors.get(CommonNumConstants.NUM_ZERO));
            String studentNumber = answer.getStudentNumber();
            answer.setStuMation(StrUtil.isNotBlank(studentNumber) ? userMap.get(studentNumber) : null);
        }
        iAuthUserService.setName(list, "createId", "createName");
        outputObject.setBeans(list);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryFilterApprovedSurveys(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        Integer page = commonPageInfo.getPage();
        Integer limit = commonPageInfo.getLimit();
        String state = commonPageInfo.getState();
        Integer IntState = Integer.valueOf(state);
        String userId = inputObject.getLogParams().get("id").toString();
        List<ExamSurveyMarkExam> examSurveyMarkExams = examSurveyMarkExamService.selectByUserId(userId);
        List<String> surveyIds = examSurveyMarkExams.stream().map(ExamSurveyMarkExam::getSurveyId).collect(Collectors.toList());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), IntState);
        extracted(outputObject, queryWrapper, commonPageInfo, page, limit);
    }

    @Override
    public Map<String, Integer> queryAnswerNum(List<String> directoryIds, String createId, String holderId, String objectId, Integer numState) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directoryIds);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSubjectId), objectId);
        queryWrapper.like(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getClassId), holderId);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
            .ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), "");
        queryWrapper.ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), createId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), numState);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        Map<String, List<ExamSurveyAnswer>> collect = list.stream().collect(Collectors.groupingBy(ExamSurveyAnswer::getSurveyId));
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, List<ExamSurveyAnswer>> entry : collect.entrySet()) {
            map.put(entry.getKey(), entry.getValue().size());
        }
        if (CollectionUtil.isEmpty(map)) {
            return Collections.emptyMap();
        }
        return map;
    }

    @Override
    public Map<String, Integer> queryAlreadyAnswerNum(List<String> directoryIds) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directoryIds);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
            .ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), "");
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), CommonNumConstants.NUM_TWO);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        Map<String, List<ExamSurveyAnswer>> collect = list.stream().collect(Collectors.groupingBy(ExamSurveyAnswer::getSurveyId));
        Map<String, Integer> map = new HashMap<>();
        for (Map.Entry<String, List<ExamSurveyAnswer>> entry : collect.entrySet()) {
            map.put(entry.getKey(), entry.getValue().size());
        }
        if (CollectionUtil.isEmpty(map)) {
            return Collections.emptyMap();
        }
        return map;
    }

    @Override
    public void IsTakeSurveyAnswer(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String surveyId = inputObject.getParams().get("surveyId").toString();
        ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectById(surveyId);
        if (ObjectUtil.isEmpty(examSurveyDirectory)) {
            throw new CustomException("试卷不存在");
        }
        String classId = examSurveyDirectory.getClassId();
        String[] split = classId.split(",");
        List<String> classIds = Arrays.asList(split);
        String subjectId = examSurveyDirectory.getSubjectId();
        boolean yesOrNo = false;
        UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(userId);
        if (ObjectUtil.isEmpty(userOrStudent)) {
            throw new CustomException("用户不存在");
        }
        if (userOrStudent.getUserOrStudent()) {
            Map<String, Object> dataMation = userOrStudent.getDataMation();
            String no = null;
            if (dataMation != null) {
                Object noObject = dataMation.get("no");
                if (noObject != null) {
                    no = noObject.toString();
                } else {
                    no = dataMation.get("studentNumber").toString();
                }
            }
            List<SubjectClasses> subjectClassesByObjectIdAndClassesIds = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(subjectId, classIds);
            List<String> subLinkIds = subjectClassesByObjectIdAndClassesIds.stream().map(SubjectClasses::getId).collect(Collectors.toList());
            List<SubjectClassesStu> subjectClassesStuList = subjectClassesStuService.queryListBySubClassLinkIds(subLinkIds);
            List<String> studentNumber = subjectClassesStuList.stream().map(SubjectClassesStu::getStuNo).collect(Collectors.toList());
            if (studentNumber.contains(no)) {
                yesOrNo = true;
            }
        } else {
            List<SubjectClasses> subjectClassesByObjectIdAndClassesIds = subjectClassesService.getSubjectClassesByObjectIdAndClassesIds(subjectId, classIds);
            List<String> createIds = subjectClassesByObjectIdAndClassesIds.stream().map(SubjectClasses::getCreateId).collect(Collectors.toList());
            if (createIds.contains(userId)) {
                yesOrNo = true;
            }
        }
        outputObject.setBean(yesOrNo);
    }

    @Override
    public Long queryClassExamSurveyAnswerNum(String classId, String stuId, String subjectId) {
        // 获取试卷id
        List<String> directorIds = examSurveyDirectoryService.queryDirectoryIdsByClassId(classId, subjectId);
        if (CollectionUtil.isEmpty(directorIds)) {
            return 0L;
        }
        // 获取参与人次、移除老师的记录
        String createId = examSurveyDirectoryService.selectById(directorIds.get(CommonNumConstants.NUM_ZERO)).getCreateId();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getIsComplete), CommonNumConstants.NUM_ONE);
        queryWrapper.ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), createId);
        if (StrUtil.isNotEmpty(stuId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), stuId);
        }
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directorIds);
        return count(queryWrapper);
    }

    @Override
    public List<ExamSurveyAnswer> selectSurveyIdByUserId(String userId) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId)
            .and(wrapper -> wrapper.isNotNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
                .ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), ""));
        return list(queryWrapper);
    }

    @Override
    public List<ExamSurveyAnswer> selectSurveyIdByteacherId(String userId) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId)
            .and(wrapper -> wrapper.isNotNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
                .ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), ""));
        return list(queryWrapper);
    }

    @Override
    public Map<String, List<ExamSurveyAnswer>> queryAnswerList(List<String> collect) {
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), collect);
        queryWrapper.isNotNull(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate))
            .ne(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getEndAnDate), "");
        return list(queryWrapper).stream().collect(Collectors.groupingBy(ExamSurveyAnswer::getSurveyId));
    }

    @Override
    public void updateMarkFraction(String id) {
        UpdateWrapper<ExamSurveyAnswer> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(CommonConstants.ID, id);
        updateWrapper.set(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getMarkFraction), CommonNumConstants.NUM_ZERO);
        update(updateWrapper);
    }

    @Override
    public void queryAllSurveyAnswerListBySurveyId(InputObject inputObject, OutputObject outputObject) {
        CommonPageInfo commonPageInfo = inputObject.getParams(CommonPageInfo.class);
        //试卷Id
        String holderId = commonPageInfo.getHolderId();
        //1.否  2.是
        String state = commonPageInfo.getState();
        Page page = PageHelper.startPage(commonPageInfo.getPage(), commonPageInfo.getLimit());
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), holderId);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), Integer.valueOf(state));
        List<ExamSurveyAnswer> examSurveyAnswerList = list(queryWrapper);
        examSurveyAnswerList.forEach(
            examSurveyAnswer -> {
                String createId = examSurveyAnswer.getCreateId();
                UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(createId);
                if (userOrStudent.getUserOrStudent()) {
                    examSurveyAnswer.setUserMation(userOrStudent);
                } else {
                    examSurveyAnswer.setTeacherMation(userOrStudent);
                }
            }
        );
        List<String> surveyIds = examSurveyAnswerList.stream().map(ExamSurveyAnswer::getSurveyId).filter(StrUtil::isNotBlank).distinct().collect(Collectors.toList());
        Map<String, ExamSurveyDirectory> surveyMap = surveyIds.isEmpty() ? new HashMap<>() : examSurveyDirectoryService.selectMapBysurveyIds(surveyIds);
        for (ExamSurveyAnswer answer : examSurveyAnswerList) {
            answer.setSurveyMation(surveyMap.get(answer.getSurveyId()));
        }
        outputObject.setBeans(examSurveyAnswerList);
        outputObject.settotal(page.getTotal());
    }

    @Override
    public void queryAnswerListByAll(String subjectId, String classId, String id, List<String> stuNos) {

    }

    @Override
    public List<ExamSurveyAnswer> queryListByStuNoListAndExamId(List<String> allStuNo, String examId) {
        if (CollectionUtil.isEmpty(allStuNo) || StrUtil.isEmpty(examId)) {
            return null;
        }
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), allStuNo);
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), examId);
        return list(queryWrapper);
    }


    @Override
    public Map<String, Long> queryClassExamSurveyAnswerNumByStuIds(String classesId, List<String> stuIds, String subjectId) {
        List<String> directorIds = examSurveyDirectoryService.queryDirectoryIdsByClassId(classesId, subjectId);
        if (CollectionUtil.isEmpty(directorIds)) {
            return new HashMap<>();
        }
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getIsComplete), CommonNumConstants.NUM_ONE);
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directorIds);
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), stuIds);
        List<ExamSurveyAnswer> list = list(queryWrapper);
        if (CollectionUtil.isEmpty(list)) {
            return Collections.emptyMap();
        }
        return list.stream().collect(Collectors.groupingBy(ExamSurveyAnswer::getCreateId, Collectors.counting()));
    }

    @Override
    public Double queryClassExamSurveyAvgScore(String classesId, String stuId, String subjectId) {
        List<String> directorIds = examSurveyDirectoryService.queryDirectoryIdsByClassId(classesId, subjectId);
        if (CollectionUtil.isEmpty(directorIds)) {
            return 0.0;
        }
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        // 已批阅的试卷
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getState), CommonNumConstants.NUM_TWO);
        if (StrUtil.isNotEmpty(stuId)) {
            queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), stuId);
        }
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), directorIds);
        List<ExamSurveyAnswer> list = list(queryWrapper);

        // 求最后得分平均值，还需要判断是否为空-为空代表0
        return list.stream()
            // 确保对象本身不为null
            .filter(Objects::nonNull)
            .mapToDouble(answer -> answer.getMarkFraction() != null ? answer.getMarkFraction() : 0.0)
            .average()
            .orElse(0.0);
    }

    @Override
    public List<Map<String, Object>> queryExamAnserByExamIds(List<String> examIdList) {
        if (CollectionUtil.isEmpty(examIdList)) {
            return Collections.emptyList();
        }
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.in(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), examIdList);
        return JSONUtil.toList(JSONUtil.toJsonStr(list(queryWrapper)), null);
    }

    @Override
    public void querySurveyBySurveyIdAndUserId(InputObject inputObject, OutputObject outputObject) {
        String userId = inputObject.getLogParams().get("id").toString();
        String surveyId = inputObject.getParams().get("surveyId").toString();
        QueryWrapper<ExamSurveyAnswer> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getSurveyId), surveyId)
            .eq(MybatisPlusUtil.toColumns(ExamSurveyAnswer::getCreateId), userId);
        ExamSurveyAnswer examSurveyAnswer = getOne(queryWrapper);
        if (ObjectUtil.isNotEmpty(examSurveyAnswer)) {
            ExamSurveyDirectory examSurveyDirectory = examSurveyDirectoryService.selectBySurAndStuId(surveyId, userId);
            examSurveyAnswer.setSurveyMation(examSurveyDirectory);
            outputObject.setBean(examSurveyAnswer);
            outputObject.settotal(1);
        }
    }

    private void extracted(OutputObject outputObject, QueryWrapper<ExamSurveyAnswer> queryWrapper, CommonPageInfo commonPageInfo, Integer page, Integer limit) {
        List<ExamSurveyAnswer> beans = list(queryWrapper); // 获取所有的已批阅回答者信息
        for (ExamSurveyAnswer bean : beans) {
            UserOrStudent userOrStudent = schoolCommonService.queryUserOrStudent(bean.getCreateId());
            bean.setUserMation(userOrStudent);
        }
        // 设置信息：
        List<String> stuNoList = beans.stream().map(ExamSurveyAnswer::getStudentNumber).collect(Collectors.toList());
        List<Map<String, Object>> userList = new ArrayList<>();
        if (CollectionUtil.isNotEmpty(stuNoList)) {
            userList = ExecuteFeignClient.get(() ->
                iCertificationRest.queryUserByStudentNumber(Joiner.on(CommonCharConstants.COMMA_MARK).join(stuNoList))).getRows();
        }
        List<String> schoolIds = beans.stream().map(ExamSurveyAnswer::getSchoolId).distinct().collect(Collectors.toList());
        List<String> surveyIds = beans.stream().map(ExamSurveyAnswer::getSurveyId).distinct().collect(Collectors.toList());
        List<String> facultyIds = beans.stream().map(ExamSurveyAnswer::getFacultyId).distinct().collect(Collectors.toList());
        List<String> majorIds = beans.stream().map(ExamSurveyAnswer::getMajorId).distinct().collect(Collectors.toList());
        Map<String, School> schoolMap = schoolService.selectMapByIds(schoolIds);
        Map<String, ExamSurveyDirectory> surveyMap = examSurveyDirectoryService.selectMapBysurveyIds(surveyIds);
        Map<String, Faculty> facultyMap = facultyService.selectMapByIds(facultyIds);
        Map<String, Major> majorMap = majorService.selectMapByIds(majorIds);
        Map<String, Map<String, Object>> userMap = userList.stream()
            .collect(Collectors.toMap(
                user -> user.get("studentNumber").toString(),
                Function.identity(),
                (oldValue, newValue) -> oldValue  // 保留第一个值，忽略后续重复值
            ));
        for (ExamSurveyAnswer answer : beans) {
            answer.setSchoolMation(schoolMap.get(answer.getSchoolId()));
            answer.setSurveyMation(surveyMap.get(answer.getSurveyId()));
            answer.setFacultyMation(facultyMap.get(answer.getFacultyId()));
            answer.setMajorMation(majorMap.get(answer.getMajorId()));
            answer.setStuMation(userMap.get(answer.getStudentNumber()));
        }

        // 学校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderKey())) {
            beans = beans.stream().filter(examSurveyAnswer -> examSurveyAnswer.getSchoolId().equals(commonPageInfo.getHolderKey())).collect(Collectors.toList());
        }
        // 院校
        if (StrUtil.isNotEmpty(commonPageInfo.getHolderId())) {
            beans = beans.stream().filter(examSurveyAnswer -> examSurveyAnswer.getFacultyId().equals(commonPageInfo.getHolderId())).collect(Collectors.toList());
        }
        // 专业
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectKey())) {
            beans = beans.stream().filter(examSurveyAnswer -> examSurveyAnswer.getMajorId().equals(commonPageInfo.getObjectKey())).collect(Collectors.toList());
        }
        // 学号
        if (StrUtil.isNotEmpty(commonPageInfo.getObjectId())) {
            beans = beans.stream().filter(examSurveyAnswer -> {
                Map<String, Object> stuMation = examSurveyAnswer.getStuMation();
                return StrUtil.contains((String) stuMation.get("studentNumber"), commonPageInfo.getObjectId());
            }).collect(Collectors.toList());
        }
        // 是否包含模糊搜索学生名字
        if (StrUtil.isNotEmpty(commonPageInfo.getType())) {
            beans = beans.stream().filter(examSurveyAnswer -> {
                Map<String, Object> stuMation = examSurveyAnswer.getStuMation();
                return StrUtil.contains((String) stuMation.get("realName"), commonPageInfo.getType());
            }).collect(Collectors.toList());
        }
        // 试卷名
        if (StrUtil.isNotEmpty(commonPageInfo.getKeyword())) {
            beans = beans.stream().filter(examSurveyAnswer -> {
                ExamSurveyDirectory surveyMation = examSurveyAnswer.getSurveyMation();
                return StrUtil.contains(surveyMation.getSurveyName(), commonPageInfo.getKeyword());
            }).collect(Collectors.toList());
        }

        // 将筛选后端beans按分页参数返回
        int fromIndex = (page - 1) * limit;
        if (fromIndex >= beans.size()) {
            outputObject.setBeans(new ArrayList<>());
            outputObject.settotal(CommonNumConstants.NUM_ONE);
        }

        int toIndex = Math.min(fromIndex + limit, beans.size());
        outputObject.setBeans(beans.subList(fromIndex, toIndex));
        outputObject.settotal(beans.size());
    }

}
